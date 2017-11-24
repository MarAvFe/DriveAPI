package com.operativos.driveObj;

public class FileReference implements References {

	private int fileId;
	private boolean parent;
	private int pointers;
	
	public FileReference() {}

	public int getFileId() {
		return fileId;
	}

	public void setFileId(int fileId) {
		this.fileId = fileId;
	}

	public boolean isParent() {
		return parent;
	}

	public void setParent(boolean parent) {
		this.parent = parent;
	}

	public int getPointers() {
		return pointers;
	}

	public void setPointers(int pointers) {
		this.pointers = pointers;
	}
	
	@Override
	public String toString() {
		return "FileReference [fileId=" + fileId + "]";
	}

	@Override
	public boolean isDirectory() {
		return false;
	}
}
