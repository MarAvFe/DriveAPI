package com.operativos.driveObj;

import java.util.ArrayList;
import java.util.Arrays;

public class User {

	private String email;
	private String pwd;
	private int driveSize;
	private String wd;
	private Directory root;
	private ArrayList<SharedFile> sharedFiles;

	public User() {}

	public Directory getCurrentDirectory() {
		String[] path = wd.split("/");
		return getDirectory(root, path);
	}

	private Directory getDirectory(Directory dir, String[] remainingPath) {
		if (remainingPath.length < 2) return dir;
		References r = dir.getContent().get(remainingPath[remainingPath.length-1]);
		if(r != null) {
			if (r.isDirectory()) {
				return (Directory) dir.getReference(remainingPath[remainingPath.length-1]);
			} else {
				//System.out.println("nonDir");
			}
		}else {
			return getDirectory((Directory) dir.getReference(remainingPath[1]), Arrays.copyOfRange(remainingPath, 1, remainingPath.length));
		}
		return null;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public int getDriveSize() {
		return driveSize;
	}

	public void setDriveSize(int driveSize) {
		this.driveSize = driveSize;
	}

	public String getWd() {
		return wd;
	}

	public void setWd(String wd) {
		this.wd = wd;
	}

	public Directory getRoot() {
		return root;
	}

	public void setRoot(Directory root) {
		this.root = root;
	}

	public ArrayList<SharedFile> getSharedFiles() {
		return sharedFiles;
	}

	public void setSharedFiles(ArrayList<SharedFile> sharedFiles) {
		this.sharedFiles = sharedFiles;
	}

	@Override
	public String toString() {
		String strSharedFiles = "";
		for (SharedFile s : sharedFiles) {
			strSharedFiles += s.toString() + ",";
		}
		return "{\"email\":\"" + email + "\", \"pwd\":\"" + pwd + "\", \"driveSize\":" + driveSize + ", \"wd\":\"" + wd + "\", \"root\":" + root
				+ ", \"sharedFiles\":[" + (strSharedFiles.equals("") ? "" : strSharedFiles.substring(0, strSharedFiles.length()-1)) + "]}";
	}




}
