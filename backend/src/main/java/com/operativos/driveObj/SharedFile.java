package com.operativos.driveObj;

public class SharedFile {

	private String userEmail;
	private String filePath;
	
	public SharedFile() {};
	
	public SharedFile(String userEmail, String filePath) {
		super();
		this.userEmail = userEmail;
		this.filePath = filePath;
	}
	
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	@Override
	public String toString() {
		return "{\"userId\":\"" + userEmail + "\", \"filePath\":\"" + filePath + "\"}";
	}
	
	
}
