package ru.unisuite.synchronizer;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

import ru.unisuite.synchronizer.dbtool.DbTool;
import ru.unisuite.synchronizer.dbtool.DbToolProperties;
import ru.unisuite.synchronizer.dbtool.JdbcDbTool;
import ru.unisuite.synchronizer.disktool.DiskTool;

public class Synchronizer {

	private static Logger logger = Logger.getLogger(Synchronizer.class.getName());

	private static JournalWriter journalWriter;
	
	private static String rootDirectory;

	private static DiskTool diskTool;
	private static DbTool jdbcDbTool;

	// В качестве аргумента сначала приходит действие, которое необходимо выполнить,
	// потом перечисление файлов.
	// Если список параметров пусть, выполняется полная синхронизация
	public static void main(String args[]) throws SQLException, IOException, SynchronizerPropertiesException {
		
		SynchronizerProperties properties = new SynchronizerProperties();
		
		String myJarPath = Synchronizer.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		rootDirectory = new File(myJarPath).getParent();
		
//		rootDirectory = "C:\\Users\\romanov\\Desktop\\Synchronizer\\SyncFolder";
		
		journalWriter = new JournalWriter(rootDirectory);
		
		jdbcDbTool = new JdbcDbTool(new DbToolProperties(properties.getDbUrl(), properties.getDbUserName(), properties.getDbPassword(), properties.getDriverClassName()));
		
		diskTool = new DiskTool(rootDirectory);

		if (args.length == 0) {
			sync();
		} else {
			switch (args[0]) {
			case "upload":
				upload(args);
				break;
			case "download":
				download(args);
				break;
			case "sync": 
				sync(args);
				break;
			case "help":
				helpCommand();
				break;
			default:
				System.out.println(String.format("unknown command %s. To view the list of commands use command \"help\"", args[0]));
				break;
			}
		}

		journalWriter.close();
		
	}

	public static void saveFromDiskToDb(String fileName) throws SQLException, IOException {

		SyncObject syncObject = getSyncObjectFromDisk(fileName);

		jdbcDbTool.saveSyncObjectToDB(syncObject);

	}

	public static void saveFromDbToDisk(String fileName) throws SQLException, IOException {

		SyncObject syncObject = jdbcDbTool.fetchSyncObjectFromDB(fileName);

		diskTool.saveSyncObjectToDisk(syncObject);

	}

	private static SyncObject getSyncObjectFromDisk(String fileName) throws IOException {

		return diskTool.getSyncObjectByName(fileName);

	}

	private static void sync() {

	}

	private static void sync(String args[]) {

	}

	private static void upload(String args[]) throws SQLException, IOException {

		if (args.length < 2) {
			
			ArrayList<String> array = diskTool.getFullFileList();
			
			for (String fileName: array) {
				System.out.println(fileName);
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

	private static void download(String args[]) throws SQLException, IOException {

		if (args.length < 2) {
			
			ArrayList<String> array = jdbcDbTool.getFullFileList();
			
			for (String fileName: array) {
				saveFromDbToDisk(fileName);
				journalWriter.appendDownloaded(fileName);
			}
			
		}
		
		for (int i = 1; i < args.length; i++) {
			saveFromDbToDisk(args[i]);
			journalWriter.appendDownloaded(args[i]);
		}

	}
	
	private static void helpCommand() {
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("usage: sync <command> [fileNames] \n \n");
		builder.append("List of commands: \n");
		builder.append("   upload      uplouads files from disk to db \n");
		builder.append("   download    downloads files from db to disk \n");
		builder.append("   sync        sync \n");
		
		System.out.println(builder);
		
	}


	// private static void printDiffs(LinkedList<Diff> diffList) {
	//
	// Diff prevDiff = null;
	// Diff nextDiff = null;
	//
	// for (int i = 0; i < diffList.size(); i++) {
	//
	// Diff diff = diffList.get(i);
	//
	// if (i + 1 != diffList.size()) {
	// nextDiff = diffList.get(i + 1);
	// } else {
	// nextDiff = null;
	// }
	//
	// if (diff.operation == Operation.EQUAL) {
	//
	// int diffLength = diff.text.length();
	//
	// if (prevDiff == null) {
	// if (diffLength > 30) {
	// System.out.println(diff.text.substring(diffLength - 30));
	// } else {
	// System.out.println(diff.text);
	// }
	// } else {
	// if (nextDiff == null) {
	// System.out.println(diff.text.substring(0, 30) + "/n");
	// } else {
	// if (diffLength < 62) {
	// System.out.println(diff.text);
	// } else {
	// System.out.println(diff.text.substring(0, 30) + "/n" +
	// diff.text.substring(diffLength - 30));
	// }
	// }
	//
	// }
	//
	// }
	//
	// if (diff.operation == Operation.DELETE) {
	//
	// System.out.println("- " + diff.text);
	//
	// }
	//
	// if (diff.operation == Operation.INSERT) {
	//
	// System.out.println("+ " + diff.text);
	//
	// }
	//
	// }
	//
	// }

}
