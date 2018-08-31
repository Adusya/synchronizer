package ru.unisuite.synchronizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import ru.unisuite.synchronizer.dbtool.DbTool;
import ru.unisuite.synchronizer.dbtool.DbToolProperties;
import ru.unisuite.synchronizer.dbtool.JdbcDbTool;
import ru.unisuite.synchronizer.disktool.DiskTool;

public class SyncExecutor {

	public SyncExecutor() throws SynchronizerPropertiesException, UnsupportedEncodingException, FileNotFoundException {

		SyncProperties properties = new SyncProperties();

		rootDirectory = getRootDirectory();
		jarName = getJarName();

		journalWriter = new JournalWriter(rootDirectory);

		jdbcDbTool = new JdbcDbTool(new DbToolProperties(properties.getDbUrl(), properties.getDbUserName(),
				properties.getDbPassword(), properties.getDriverClassName()));

		diskTool = new DiskTool(rootDirectory);
	}

	private static Logger logger = Logger.getLogger(Synchronizer.class.getName());

	private JournalWriter journalWriter;

	private String rootDirectory;
	private String jarName;

	private DiskTool diskTool;
	private DbTool jdbcDbTool;

	public void saveFromDiskToDb(String fileName) throws SQLException, IOException {

		SyncObject syncObject = getSyncObjectFromDisk(fileName);

		jdbcDbTool.saveSyncObjectToDB(syncObject);

	}

	public void saveFromDbToDisk(String fileName) throws SQLException, IOException {

		SyncObject syncObject = jdbcDbTool.fetchSyncObjectFromDB(fileName);

		diskTool.saveSyncObjectToDisk(syncObject);

	}

	private SyncObject getSyncObjectFromDisk(String fileName) throws IOException {

		return diskTool.getSyncObjectByName(fileName);

	}

	public void sync() {

	}

	public void sync(List<String> fileNamesList) {

	}

	public void upload() throws IOException, SQLException {

		List<String> fullFileNamesList = diskTool.getFullFileList();
		
		if (!fullFileNamesList.isEmpty()) {
			
			for (String fileName : fullFileNamesList) {
				saveFromDiskToDb(fileName);
				journalWriter.appendUploaded(fileName);
				
			}
		}
		
	}

	public void upload(List<String> fileNamesList) throws SQLException, IOException {

		if (fileNamesList.isEmpty()) {
			upload();
		} else {

			for (String fileName : fileNamesList) {
				saveFromDiskToDb(fileName);
				journalWriter.appendUploaded(fileName);
			}
		}

	}
	
	public void download() throws SQLException, IOException {
		
		List<String> fullFileNamesList = jdbcDbTool.getFullFileList();
		
		if (!fullFileNamesList.isEmpty()) {
			
			for (String fileName : fullFileNamesList) {
				saveFromDbToDisk(fileName);
				journalWriter.appendDownloaded(fileName);
			}
		}
		
	}

	public void download(List<String> fileNamesList) throws SQLException, IOException {

		if (fileNamesList.isEmpty()) {
			download();
		} else {
			
			for (String fileName : fileNamesList) {
				saveFromDbToDisk(fileName);
				journalWriter.appendDownloaded(fileName);
			}
			
		}
		
	}

	public void helpCommand() {

		StringBuilder builder = new StringBuilder();

		builder.append("usage: sync <command> [fileNames] \n\n");
		builder.append("List of commands: \n");
		builder.append("   upload      uplouad all files from disk to db \n");
		builder.append("   download    download all files from db to disk \n");
		builder.append("   sync        sync(under construction) \n");

		System.out.println(builder);

	}

	private String getRootDirectory() {

		return Synchronizer.class.getProtectionDomain().getCodeSource().getLocation().getPath();

	}

	private String getJarName() {

		String jarPath = Synchronizer.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		return new File(jarPath).getName();

	}

	public void close() throws IOException {
		if (journalWriter != null)
			journalWriter.close();
	}

}
