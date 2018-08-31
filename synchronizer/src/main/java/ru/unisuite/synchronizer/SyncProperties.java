package ru.unisuite.synchronizer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import ru.unisuite.synchronizer.SynchronizerPropertiesException;

public class SyncProperties {

	public SyncProperties(StandartTag tag) throws SynchronizerPropertiesException {
		
		String configFileName;
		
		if (tag == StandartTag.production || tag == StandartTag.p) {
			
			configFileName = productionConfigFile;
		} else {
			configFileName = developmentConfigFile;
		}
		
		initFromProperties(configFileName);
	}
	
	private static final Logger logger = Logger.getLogger(SyncProperties.class.getName());

	private String dbUrl;
	private String dbUserName;
	private String dbPassword;
	private String driverClassName;
	
	private String dbName;
	private String id;
	private String alias;
	private String modificationDate;
	private String clob;
	
	private final String developmentConfigFile = "development.properties";
	
	private final String productionConfigFile = "production.properties";

	private void initFromProperties(String filename) throws SynchronizerPropertiesException {

		try (InputStream input = this.getClass().getClassLoader().getResourceAsStream(filename)) {

			if (input == null) {
				String errorMessage = "Unable to load " + filename;
				logger.severe(errorMessage);
				throw new SynchronizerPropertiesException(errorMessage);
			}

			Properties prop = new Properties();
			prop.load(input);

			String dbUrl = prop.getProperty(PropertiesParamName.URL);
			this.dbUrl = dbUrl;

			String dbUserName = prop.getProperty(PropertiesParamName.USERNAME);
			this.dbUserName = dbUserName;

			String dbPassword = prop.getProperty(PropertiesParamName.PASSWORD);
			this.dbPassword = dbPassword;

			String driverClassName = prop.getProperty(PropertiesParamName.DRIVER_CLASS_NAME);
			this.driverClassName = driverClassName;
			
			String dbName = prop.getProperty(PropertiesParamName.TABLE_NAME);
			this.dbName = dbName;
			
			String id = prop.getProperty(PropertiesParamName.ID);
			this.id = id;
			
			String alias = prop.getProperty(PropertiesParamName.ALIAS);
			this.alias = alias;
			
			String modificationDate = prop.getProperty(PropertiesParamName.MODIFICATION_DATE);
			this.modificationDate = modificationDate;
			
			String clob = prop.getProperty(PropertiesParamName.CLOB);
			this.clob = clob;

		} catch (IOException e) {
			// e.printStackTrace();
			String errorMessage = "Unable to load " + filename;
			logger.severe(errorMessage);
			throw new SynchronizerPropertiesException(errorMessage, e);
		}

	}

	public Logger getLogger() {
		return logger;
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
}
