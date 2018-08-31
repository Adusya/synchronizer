package ru.unisuite.synchronizer.dbtool;

import ru.unisuite.synchronizer.SyncProperties;

public class DbToolProperties {

	public DbToolProperties(SyncProperties properties) {
		
		this.dbUrl = properties.getDbUrl();
		this.dbUserName = properties.getDbUserName();
		this.dbPassword = properties.getDbPassword();
		this.driverClassName = properties.getDriverClassName();
		
		this.dbName = properties.getDbName();
		this.id = properties.getId();
		this.alias = properties.getAlias();
		this.modificationDate = properties.getModificationDate();
		this.clob = properties.getClob();
		
	}

	private String dbUrl;
	private String dbUserName;
	private String dbPassword;
	private String driverClassName;
	
	private String dbName;
	private String id;
	private String alias;
	private String modificationDate;
	private String clob;
	
	public String getDbName() {
		return dbName;
	}
	public String getId() {
		return id;
	}
	public String getAlias() {
		return alias;
	}
	public String getModificationDate() {
		return modificationDate;
	}
	public String getClob() {
		return clob;
	}
	public String getDbUrl() {
		return dbUrl;
	}
	public String getDbUserName() {
		return dbUserName;
	}
	public String getDbPassword() {
		return dbPassword;
	}
	public String getDriverClassName() {
		return driverClassName;
	}
	
}
