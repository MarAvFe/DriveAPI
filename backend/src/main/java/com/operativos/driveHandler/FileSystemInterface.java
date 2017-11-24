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
				if(d == null) return "File not found.";
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

	public static String copyFile(String userEmail, String name, String newPath) {
		ArrayList<User> users = FileSystemInterface.readDrive();
		for (User user : users) {
			if(user.getEmail().equals(userEmail)) {
				System.out.println("Copying...");
				DriveFile d = (DriveFile) user.getCurrentDirectory().getReference(name);
				if(d == null) return "File not found.";
				String tmpPath = user.getWd();
				String cd1 = FileSystemInterface.changeWorkingDirectory(userEmail, newPath);
				if(!cd1.equals("Success")) return cd1;
				String cf = FileSystemInterface.createFile(userEmail, d.getName(), d.getContent());
				if(!cf.equals("Success")) {
					FileSystemInterface.changeWorkingDirectory(userEmail, tmpPath);
					return cf;
				};
				String cd2 = FileSystemInterface.changeWorkingDirectory(userEmail, tmpPath);
				if(!cd2.equals("Success")) return cd2;
			}
		}
		return "Success";
	}

	public static String moveFile(String userEmail, String name, String newPath) {
		ArrayList<User> users = FileSystemInterface.readDrive();
		for (User user : users) {
			if(user.getEmail().equals(userEmail)) {
				DriveFile d = (DriveFile) user.getCurrentDirectory().getReference(name);
				if(d == null) return "File not found.";
				String tmpPath = user.getWd();
				String cd1 = FileSystemInterface.changeWorkingDirectory(userEmail, newPath);
				if(!cd1.equals("Success")) return cd1;
				String cf = FileSystemInterface.createFile(userEmail, d.getName(), d.getContent());
				if(!cf.equals("Success")) {
					FileSystemInterface.changeWorkingDirectory(userEmail, tmpPath);
					return cf;
				};
				String cd2 = FileSystemInterface.changeWorkingDirectory(userEmail, tmpPath);
				if(!cd2.equals("Success")) return cd2;
				String dl = FileSystemInterface.deleteFile(userEmail, name);
				if(!dl.equals("Success")) return dl;
			}
		}
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
		ArrayList<User> users = FileSystemInterface.readDrive();
		boolean dirExists = false;
		for (User user : users) {
			if(user.getEmail().equals(userEmail)) {
				if(existsPath(path,user.getRoot())) {
					user.setWd(path);
					dirExists = true;
				}
			}
		}
		if(!dirExists) return "Invalid directory.";
		saveDrive(users);
		return "Success";
	}

	private static boolean existsPath(String path, Directory root) {
		if(path.equals("/")) return true;
		String[] p;
		p = path.split("/");
		return pathDepth(Arrays.copyOfRange(p,1,p.length), root);
	}

	private static boolean pathDepth(String[] path, Directory dir) {
		if(path[0].length() == 0) return true;
		if(dir.getReference(path[0]) == null) return false;
		if(!dir.getReference(path[0]).isDirectory()) return false;
		if(path.length == 1) return true;
		return true & pathDepth(Arrays.copyOfRange(path,1,path.length), (Directory)dir.getReference(path[0]));
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
