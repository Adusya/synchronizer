package ru.unisuite.synchronizer;

public class SyncObject {

	public SyncObject(String fileName, String alias, String clob, String description) {
		
		this.fileName = fileName;
		this.alias = alias;
		this.clob = clob;
		this.description = description;
		
	}
	
	private String description;
	
	private String fileName;
	
	private String alias;

	private String clob;
	
	public String getDescription() {
		return description;
	}
	
	public String getFileName() {
		return fileName;
	}

	public String getAlias() {
		return alias;
	}
	
	public String getClob() {
		return clob;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
}
