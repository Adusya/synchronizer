package ru.unisuite.synchronizer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import ru.unisuite.synchronizer.SynchronizerPropertiesException;

public class SyncProperties {

	public SyncProperties() throws SynchronizerPropertiesException {
		initFromProperties();
	}
	
	private static final Logger logger = Logger.getLogger(SyncProperties.class.getName());

	private String dbUrl;
	private String dbUserName;
	private String dbPassword;
	private String driverClassName;

	private void initFromProperties() throws SynchronizerPropertiesException {

		String filename = "sync.properties";
		try (InputStream input = this.getClass().getClassLoader().getResourceAsStream(filename)) {

			if (input == null) {
				String errorMessage = "Unable to load " + filename;
				logger.severe(errorMessage);
				throw new SynchronizerPropertiesException(errorMessage);
			}

			Properties prop = new Properties();
			prop.load(input);

			String dbUrl = prop.getProperty("ru.unisuite.synchronizer.db.datasource.url");

			this.dbUrl = dbUrl;

			String dbUserName = prop.getProperty("ru.unisuite.synchronizer.db.datasource.username");

			this.dbUserName = dbUserName;

			String dbPassword = prop.getProperty("ru.unisuite.synchronizer.db.datasource.password");

			this.dbPassword = dbPassword;

			String driverClassName = prop.getProperty("ru.unisuite.synchronizer.db.datasource.driver-class-name");
			
			this.driverClassName = driverClassName;

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

}
