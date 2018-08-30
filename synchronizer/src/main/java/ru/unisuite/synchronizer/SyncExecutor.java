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
		
		jdbcDbTool = new JdbcDbTool(new DbToolProperties(properties.getDbUrl(), properties.getDbUserName(), properties.getDbPassword(), properties.getDriverClassName()));
		
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

	public void sync(String args[]) {

	}

	public void upload(String args[]) throws SQLException, IOException {

		if (args.length < 2) {

			List<String> array = diskTool.getFullFileList();

			for (String fileName : array) {
				saveFromDiskToDb(fileName);
				journalWriter.appendUploaded(fileName);
			}
		} else {

			for (int i = 1; i < args.length; i++) {
				saveFromDiskToDb(args[i]);
				journalWriter.appendUploaded(args[i]);
			}
		}
	}

	public void download(String args[]) throws SQLException, IOException {

		if (args.length < 2) {

			ArrayList<String> array = jdbcDbTool.getFullFileList();

			for (String fileName : array) {
				saveFromDbToDisk(fileName);
				journalWriter.appendDownloaded(fileName);
			}

		}

		for (int i = 1; i < args.length; i++) {
			saveFromDbToDisk(args[i]);
			journalWriter.appendDownloaded(args[i]);
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
