package com.operativos.driveObj;

import java.util.Date;

public class DriveFile implements References{

	private int id;
	private String name;
	private int size;
	private String extension;
	private Date creationDate;
	private Date modificationDate;
	private String content;
	
	public DriveFile() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int i) {
		this.size = i;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public boolean isDirectory() {
		return false;
	}

	@Override
	public String toString() {
		return "{\"id\":\"" + id + "\", \"name\":\"" + name + "\", \"size\":" + size + ", \"extension\":\"" + extension + "\",\"creationDate\":\""
				+ creationDate + "\", \"modificationDate\":\"" + modificationDate + "\", \"content\":\"" + content + "\", \"isDir\":false}";
	}
	
	
}
