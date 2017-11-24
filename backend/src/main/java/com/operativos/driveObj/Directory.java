package com.operativos.driveObj;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Directory implements References{

	private String name;
	private int size;
	private Map<String,References> content;
	private Directory parent;
	
	
	public Directory() {
		this.content = new HashMap<String, References>();
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

	public void setSize(int size) {
		this.size = size;
	}
	
	public void updateSize() {
		Map<String, References> map = this.content;
		int nuSize = 0;
		Iterator<Entry<String, References>> it = map.entrySet().iterator();
		for(; it.hasNext(); ) {
			Entry<String, References> entry = it.next();
			if (entry.getValue().isDirectory()) {
				Directory d = (Directory) entry.getValue();
				nuSize += d.getSize();
			} else {
				DriveFile d = (DriveFile) entry.getValue();
				nuSize += d.getSize();
			}
		}
		setSize(nuSize);
		if (parent != null) parent.updateSize();
	}

	public Map<String, References> getContent() {
		return content;
	}

	public void setContent(Map<String,References> content) {
		this.content = content;
	}
	
	public References getReference(String key) {
		return this.content.get(key);
	}
	
	public void setReference(String key, References value) {
		this.content.put(key, value);
	}

	public Directory getParent() {
		return parent;
	}

	public void setParent(Directory parent) {
		this.parent = parent;
	}
	
	public String getPath() {
		if(parent == null) return "root";
		String parentPath = parent.getPath();
		return parentPath + "/" + name;
	}

	@Override
	public boolean isDirectory() {
		return true;
	}

	@Override
	public String toString() {
		String strContent = "";
		Map<String, References> map = this.content;
		Iterator<Entry<String, References>> it = map.entrySet().iterator();
		for(; it.hasNext(); ) {
			Entry<String, References> entry = it.next();
			if (entry.getValue().isDirectory()) {
				Directory d = (Directory) entry.getValue();
				strContent += d.toString() + ",";
			} else {
				DriveFile d = (DriveFile) entry.getValue();
				strContent += d.toString() + ",";
			}
		}
		return "{\"name\":\"" + name + "\",\"size\":" + size + ",\"content\":[" + 
			(strContent.equals("") ? "" : strContent.substring(0, strContent.length()-1)) + "], \"isDir\":true}";
	}
	
	

}
