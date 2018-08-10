package ru.unisuite.synchronizer;

import java.sql.Timestamp;

public class SyncObject {

	public SyncObject(Integer id, String alias, Timestamp timestamp, String clob) {
		
		this.id = id;
		this.alias = alias;
		this.timestamp = timestamp;
		this.clob = clob;
		
	}
	
	private Integer id;
	
	private String alias;
	
	private Timestamp timestamp;

	private String clob;
	
	public Integer getId() {
		return id;
	}

	public String getAlias() {
		return alias;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public String getClob() {
		return clob;
	}
	
}
