package ru.unisuite.synchronizer.dbtool;

public class DbToolProperties {

	public DbToolProperties(String dbUrl, String dbUserName, String dbPassword, String driverClassName) {
		
		this.dbUrl = dbUrl;
		this.dbUserName = dbUserName;
		this.dbPassword = dbPassword;
		this.driverClassName = driverClassName;
		
	}

	private String dbUrl;
	private String dbUserName;
	private String dbPassword;
	private String driverClassName;
	
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
