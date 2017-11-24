package com.operativos.driveHandler;

import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.operativos.driveObj.Directory;
import com.operativos.driveObj.DriveFile;
import com.operativos.driveObj.References;
import com.operativos.driveObj.SharedFile;
import com.operativos.driveObj.User;

public class FileSystemInterface {
	
	static String driveName = System.getProperty("user.dir") + "/drive.xml";
	static int consecutiveFile = 1;
	
	public static void createDrive() {
		ArrayList<User> users = new ArrayList<User>();
		saveDrive(users);
	}
	
	public static String addUser(String email, String pwd, int driveSize) {
		ArrayList<User> users = FileSystemInterface.readDrive();
		
		for (User user : users) {
			if(user.getEmail().equals(email)) {
				return "Duplicate user";
			}
		}
		
		Directory root = new Directory();
		root.setName("root");
		root.setSize(0);
		root.setContent(new HashMap<>());
		root.setParent(null);
		
		User u = new User();
		u.setEmail(email);
		u.setPwd(pwd);
		u.setDriveSize(driveSize);
		u.setWd("root");
		u.setSharedFiles(new ArrayList<>());
		u.setRoot(root);
		users.add(u);
		saveDrive(users);
		return "Success";
	}
	
	public static boolean login(String email, String pwd) {
		ArrayList<User> users = FileSystemInterface.readDrive();
		for (User user : users) {
			if(user.getEmail().equals(email) && user.getPwd().equals(pwd)) {
				return true;
			}
		}
		return false;
	}
	
	public static String createFile(String userEmail, String name, String content) {
		DriveFile f = new DriveFile();
		f.setId(consecutiveFile);
		f.setName(name);
		f.setSize(content.length());
		f.setExtension("txt");
		f.setCreationDate(new Date());
		f.setModificationDate(new Date());
		f.setContent(content);
		
		ArrayList<User> users = FileSystemInterface.readDrive();
		for (User user : users) {
			if(user.getEmail().equals(userEmail)) {
				if(user.getRoot().getSize() + f.getSize() < user.getDriveSize() ) {
					user.getCurrentDirectory().getContent().put(name,f);
					user.getCurrentDirectory().updateSize();					
				}else {
					return "Not enough space for new file.";
				}
			}
		}
		saveDrive(users);
		return "Success";
	}
	
	public static String modifyFile(String userEmail, String name, String content) {		
		ArrayList<User> users = FileSystemInterface.readDrive();
		for (User user : users) {
			if(user.getEmail().equals(userEmail)) {
				DriveFile d = (DriveFile) user.getCurrentDirectory().getReference(name);
				d.setContent(content);
				d.setModificationDate(new Date());
				if(user.getRoot().getSize() + content.length() < user.getDriveSize() ) {
					user.getCurrentDirectory().getContent().put(name,d);
					user.getCurrentDirectory().updateSize();
				}else {
					return "Not enough space for new file.";
				}
			}
		}
		saveDrive(users);
		return "Success";
	}
	
	public static String deleteFile(String userEmail, String name) {		
		ArrayList<User> users = FileSystemInterface.readDrive();
		boolean found = false;
		for (User user : users) {
			if(user.getEmail().equals(userEmail)) {
				Map<String, References> map = user.getCurrentDirectory().getContent();
				Iterator<Entry<String, References>> it = map.entrySet().iterator();
				for(; it.hasNext(); ) {
					Entry<String, References> entry = it.next();
					if(entry.getKey().equals(name)) {
						it.remove();
						found = true;
					}
				}
				if(!found) return "File not found.";
				user.getCurrentDirectory().setContent(map);
				user.getCurrentDirectory().updateSize();
			}
		}
		saveDrive(users);
		return "Success";
	}
	
	public static String shareFile(String userEmail, String fileName, String theirEmail) {		
		ArrayList<User> users = FileSystemInterface.readDrive();
		boolean themFound = false, fileFound = false;
		for (User user : users) {
			if(user.getEmail().equals(userEmail)) {
				Map<String, References> map = user.getCurrentDirectory().getContent();
				Iterator<Entry<String, References>> it = map.entrySet().iterator();
				for(; it.hasNext(); ) {
					Entry<String, References> entry = it.next();
					if(entry.getKey().equals(fileName)) {
						for (User their : users) {
							if(their.getEmail().equals(theirEmail)) {
								SharedFile sf = new SharedFile(userEmail, user.getCurrentDirectory().getPath() + "/" + fileName + "." + ((DriveFile)entry.getValue()).getExtension());
								their.getSharedFiles().add(sf);
								themFound = true;
							}
						}
						if(!themFound) return "Destiny mail not found.";
						saveDrive(users);
						fileFound = true;
					}
				}
				if(!fileFound) return "File not found.";
				user.getCurrentDirectory().setContent(map);
				user.getCurrentDirectory().updateSize();
			}
		}
		saveDrive(users);
		return "Success";
	}
	
	public static String createDirectory(String userEmail, String name) {
		Directory d = new Directory();
		d.setName(name);
		d.setSize(0);
		d.setContent(new HashMap<>());
		
		ArrayList<User> users = FileSystemInterface.readDrive();
		for (User user : users) {
			if(user.getEmail().equals(userEmail)) {
				d.setParent(user.getCurrentDirectory());
				if(user.getCurrentDirectory().getContent().get(name) != null) return "File or directory exists.";
				user.getCurrentDirectory().getContent().put(name,d);
			}
		}
		saveDrive(users);
		return "Success";
	}
	
	public static String ls(String userEmail) {
		ArrayList<User> users = FileSystemInterface.readDrive();
		for (User user : users) {
			if(user.getEmail().equals(userEmail)) {
				return user.getRoot().toString();
			}
		}
		return "";
	}
	
	public static String changeWorkingDirectory(String userEmail, String path) {
		if(path.equals(".")) return "Success";
		ArrayList<User> users = FileSystemInterface.readDrive();
		for (User user : users) {
			if(user.getEmail().equals(userEmail)) {
				String newWd;
				String[] p;
				boolean dirExists = false;
				if(path.equals("..")) {
					newWd = user.getWd();
					if(newWd.equals("root")) return "Can't go over root.";
					p = newWd.split("/");
					String[] tmp = Arrays.copyOfRange(p, 0, p.length-1);
					newWd = tmp[0];
					for (int i = 1; i < tmp.length; i++) {
						newWd += "/";
						newWd += tmp[i];
					}
					dirExists = true;
				}else{
					path = "/" + path;
					newWd = user.getWd() + path;
					p = newWd.split("/");
					References r = user.getCurrentDirectory().getContent().get(p[p.length-1]); 
					if (r.isDirectory()) {
						if(((Directory)r).getName().equals(p[p.length-1])) {
							dirExists = true;
						}
					}
				};
				if(!dirExists) return "Directory not found.";
				user.setWd(newWd);
			}
		}
		saveDrive(users);
		return "Success";
	}

	private static void saveDrive(ArrayList<User> users){
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(driveName);
			XMLEncoder encoder = new XMLEncoder(fos);
			encoder.setExceptionListener(new ExceptionListener() {
				public void exceptionThrown(Exception e) {
					System.out.println("Exception! :"+e.toString());
				}
			});
			encoder.writeObject(users);
			encoder.close();
			fos.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private static ArrayList<User> readDrive() {
		FileInputStream fis;
		try {
			fis = new FileInputStream(driveName);
			XMLDecoder decoder = new XMLDecoder(fis);
			@SuppressWarnings("unchecked")
			ArrayList<User> decodedSettings = (ArrayList<User>) decoder.readObject();
			decoder.close();
			fis.close();
			return decodedSettings;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
