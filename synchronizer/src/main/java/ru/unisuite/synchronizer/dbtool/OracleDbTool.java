package ru.unisuite.synchronizer.dbtool;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import ru.unisuite.synchronizer.SyncObject;
import ru.unisuite.synchronizer.SyncProperties;

public class OracleDbTool implements DbTool {

	private DbToolProperties dbProperties;

	public OracleDbTool(SyncProperties properties) {

		dbProperties = new DbToolProperties(properties);
	}

	Logger logger = Logger.getLogger(OracleDbTool.class.getName());

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

	static String selectSQLQuery = "select t.id_templates, t.name, tv.note, td.template, td.use_sign, t.alias, t.qualifier\r\n"
			+ ", t.public_sign, td.rowid from templates_data_rs td, templates_rs t, template_versions_rs tv where tv.id_templates = t.id_templates\r\n"
			+ "and td.id_template_versions = tv.id_template_versions";

	public SyncObject fetchSyncObjectFromDB(String fileName) throws SQLException, IOException {

		SyncObject syncObject = null;

		String parameterizedSelectSQLQuery = selectSQLQuery + " and t.alias = ?";

		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = (PreparedStatement) connection
						.prepareStatement(parameterizedSelectSQLQuery)) {
			int i = 1;
			preparedStatement.setString(i++, fileName);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {

				if (resultSet.isBeforeFirst()) {

					resultSet.next();

					String alias = resultSet.getString("ALIAS");

					String clob;

					try (Reader reader = resultSet.getCharacterStream("TEMPLATE")) {
						if (reader == null) {
							return null;
						}

						clob = readToString(reader);
					}

					String description = resultSet.getString("NAME");

					syncObject = new SyncObject(null, alias, clob, description);

				}
			}

		}

		return syncObject;

	}

	public void createSyncObjectInDB(SyncObject syncObject) throws SQLException, IOException {

		String createSQLQuery = "select lxse_template_rs.temp_create_birt_template(?, '', ?) from dual";

		String clob = syncObject.getClob();

		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = (PreparedStatement) connection.prepareStatement(createSQLQuery);
				Reader clobReader = new StringReader(clob)) {
			int i = 1;
			preparedStatement.setString(i++, syncObject.getDescription());
			preparedStatement.setString(i++, syncObject.getAlias());
			preparedStatement.executeUpdate();
		}

		updateSyncObjectInDB(syncObject);

	}

	public void updateSyncObjectInDB(SyncObject syncObject) throws SQLException, IOException {

		String updateSQLQuery = "update templates_data_rs td set td.template = ? where td.id_template_versions = (select tv.id_template_versions \r\n"
				+ "from template_versions_rs tv, templates_rs t where tv.id_templates = t.id_templates and t.alias = ?)";

		String clob = syncObject.getClob();

		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = (PreparedStatement) connection.prepareStatement(updateSQLQuery);
				Reader clobReader = new StringReader(clob)) {
			int i = 1;
			preparedStatement.setClob(i++, clobReader);
			preparedStatement.setString(i++, syncObject.getAlias());
			preparedStatement.executeUpdate();
		}

	}

	public List<String> getFullFileList() throws SQLException {

		List<String> array = new ArrayList<>();

		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = (PreparedStatement) connection.prepareStatement(selectSQLQuery)) {

			try (ResultSet resultSet = preparedStatement.executeQuery()) {

				while (resultSet.next() != false) {

					array.add(resultSet.getString("ALIAS"));

				}

			}

		}

		return array;

	}

	public String readToString(Reader reader) throws IOException {

		char[] arr = new char[8 * 1024];
		StringBuilder buffer = new StringBuilder();
		int numCharsRead;
		while ((numCharsRead = reader.read(arr, 0, arr.length)) != -1) {
			buffer.append(arr, 0, numCharsRead);
		}

		return buffer.toString();
	}

	public boolean exists(String alias) throws SQLException {

		String parameterizedSelectSQLQuery = selectSQLQuery + " and t.alias = ?";

		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = (PreparedStatement) connection
						.prepareStatement(parameterizedSelectSQLQuery)) {
			int i = 1;
			preparedStatement.setString(i++, alias);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {

				return resultSet.isBeforeFirst();

			}

		}

	}

}
