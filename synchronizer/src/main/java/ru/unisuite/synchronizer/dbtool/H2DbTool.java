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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import ru.unisuite.synchronizer.SyncObject;
import ru.unisuite.synchronizer.SyncProperties;

public class H2DbTool implements DbTool {

	private DbToolProperties dbProperties;

	public H2DbTool(SyncProperties properties) {

		dbProperties = new DbToolProperties(properties);
	}

	Logger logger = Logger.getLogger(H2DbTool.class.getName());

	public Connection getConnection() {

		try {
			Class.forName(dbProperties.getDriverClassName());
		} catch (ClassNotFoundException e) {
			logger.log(Level.SEVERE, "Can not set driver for DB. ", e);
		}

		try {
			return DriverManager.getConnection(dbProperties.getDbUrl(), dbProperties.getDbUserName(),
					dbProperties.getDbPassword());
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Can not get db connection. ", e);
			return null;
		}

	}

	public SyncObject fetchSyncObjectFromDB(String fileName) throws SQLException, IOException {

		final String selectSQKQuery = String.format("SELECT %s, %s, %s, %s FROM %s  where %s = ?", dbProperties.getId(),
				dbProperties.getAlias(), dbProperties.getModificationDate(), dbProperties.getClob(),
				dbProperties.getDbName(), dbProperties.getAlias());

		SyncObject syncObject = null;

		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = (PreparedStatement) connection.prepareStatement(selectSQKQuery)) {
			int i = 1;
			preparedStatement.setString(i++, fileName);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {

				if (resultSet.isBeforeFirst()) {

					resultSet.next();

					Integer id = resultSet.getInt(dbProperties.getId());

					String alias = resultSet.getString(dbProperties.getAlias());

					Timestamp modificationDate = resultSet.getTimestamp(dbProperties.getModificationDate());

					Reader reader = resultSet.getCharacterStream(dbProperties.getClob());

					String clob = readToString(reader);

					syncObject = new SyncObject(null, alias, clob);

				}
			}

		}

		return syncObject;

	}

	public void saveSyncObjectToDB(SyncObject syncObject) throws SQLException, IOException {

		final String mergeSQLQuery = String.format(
				"MERGE INTO %s KEY(%s) VALUES((select %s from %s where %s = ?), ?, ?, ?)", dbProperties.getDbName(),
				dbProperties.getAlias(), dbProperties.getId(), dbProperties.getDbName(), dbProperties.getAlias());

		String clob = syncObject.getClob();

		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = (PreparedStatement) connection.prepareStatement(mergeSQLQuery);
				Reader clobReader = new StringReader(clob)) {
			int i = 1;
			preparedStatement.setString(i++, syncObject.getAlias());
			preparedStatement.setString(i++, syncObject.getAlias());
			preparedStatement.setClob(i++, clobReader);
			preparedStatement.executeUpdate();
		}

	}

	public List<String> getFullFileList() throws SQLException {

		final String selectSQLQuery = String.format("select %s from %s", dbProperties.getAlias(),
				dbProperties.getDbName());

		List<String> array = new ArrayList<>();

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

	@Override
	public boolean exists(String alias) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createSyncObjectInDB(SyncObject syncObject) throws SQLException, IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateSyncObjectInDB(SyncObject syncObject) throws SQLException, IOException {
		// TODO Auto-generated method stub
		
	}

}
