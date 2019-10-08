package ru.unisuite.synchronizer;

public class SyncObject {

	public SyncObject(String fileName, String alias, String clob) {
		
		this.fileName = fileName;
		this.alias = alias;
		this.clob = clob;
		
	}
	
	private String fileName;
	
	private String alias;

	private String clob;
	
	public String getFileName() {
		return fileName;
	}

	public String getAlias() {
		return alias;
	}
	
	public String getClob() {
		return clob;
	}
	
}
