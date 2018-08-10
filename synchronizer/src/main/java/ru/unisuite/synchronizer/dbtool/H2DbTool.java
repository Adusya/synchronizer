package ru.unisuite.synchronizer.dbtool;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import ru.unisuite.synchronizer.SyncObject;
import ru.unisuite.synchronizer.Synchronizer;

public class H2DbTool implements DbTool {

	Logger logger = Logger.getLogger(Synchronizer.class.getName());

	public boolean saveStringToDb(String string) {

		return false;
	}

	public Connection getConnection() {

		try {
			Class.forName("org.h2.Driver");
		} catch (ClassNotFoundException e) {
			logger.log(Level.SEVERE, "Can not set driver for DB. ", e);
		}

		try {
			return DriverManager.getConnection("jdbc:h2:~/test", "sa", "");
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Can not get db connection. ", e);
			return null;
		}

	}

	public SyncObject fetchSyncObjectFromDB(String fileName) throws SQLException, IOException {

		final String selectSQKQuery = "SELECT id, alias, modification_date, clob FROM CLOBS  where alias = ?";

		SyncObject syncObject = null;

		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = (PreparedStatement) connection.prepareStatement(selectSQKQuery)) {
			int i = 1;
			preparedStatement.setString(i++, fileName);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {

				resultSet.next();

				Integer id = resultSet.getInt(DbToolParamName.id);

				String alias = resultSet.getString(DbToolParamName.alias);

				Timestamp modificationDate = resultSet.getTimestamp(DbToolParamName.modificationDate);

				Reader reader = resultSet.getCharacterStream(DbToolParamName.clob);

				String clob = readToString(reader);

				syncObject = new SyncObject(id, alias, modificationDate, clob);

			}

		}

		return syncObject;

	}

	public void saveSyncObjectToDB(SyncObject syncObject) throws SQLException, IOException {

		final String mergeSQLQuery = "MERGE INTO clobs KEY(ALIAS) VALUES(select id from clobs where ALIAS = ?, ?, ?, ?)";

		String clob = syncObject.getClob();

		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = (PreparedStatement) connection.prepareStatement(mergeSQLQuery);
				Reader clobReader = new StringReader(clob)) {
			int i = 1;
			preparedStatement.setString(i++, syncObject.getAlias());
			preparedStatement.setString(i++, syncObject.getAlias());
			preparedStatement.setTimestamp(i++, syncObject.getTimestamp());
			preparedStatement.setClob(i++, clobReader);
			preparedStatement.executeUpdate();
		}

	}

	public ArrayList<String> getFullFileList() throws SQLException {

		final String selectSQLQuery = "select alias from clobs";

		ArrayList<String> array = new ArrayList<>();

		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = (PreparedStatement) connection.prepareStatement(selectSQLQuery)) {

			try (ResultSet resultSet = preparedStatement.executeQuery()) {

				while (resultSet.next() != false) {
					
					array.add(resultSet.getString(DbToolParamName.alias));
					
				}
				
			}

		}
		
		return array;

	}

	public String readToString(Reader reader) throws IOException {

		int intValueOfChar;
		String targetString = "";
		while ((intValueOfChar = reader.read()) != -1) {
			targetString += (char) intValueOfChar;
		}

		return targetString;
	}

}
