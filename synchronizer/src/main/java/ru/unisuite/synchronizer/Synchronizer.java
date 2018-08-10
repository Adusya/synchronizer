package ru.unisuite.synchronizer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.diff_match_patch.diff_match_patch;
import com.google.diff_match_patch.diff_match_patch.Diff;
import com.google.diff_match_patch.diff_match_patch.Operation;

import ru.unisuite.synchronizer.dbtool.DbTool;
import ru.unisuite.synchronizer.dbtool.H2DbTool;
import ru.unisuite.synchronizer.disktool.DiskTool;

public class Synchronizer {

	private static Logger logger = Logger.getLogger(Synchronizer.class.getName());

	private static final String rootDirectory = "C:\\Users\\romanov\\Desktop\\Synchronizer\\SyncFolder";

	private static DiskTool diskTool = new DiskTool(rootDirectory);
	private static DbTool h2DbTool = new H2DbTool();

	// ¬ качестве аргумента сначала приходит действие, которое необходимо выполнить,
	// потом перечисление файлов.
	// ≈сли список параметров пусть, выполн€етс€ полна€ синхронизаци€
	public static void main(String args1[]) throws SQLException, IOException {

		String[] args = { "upload" };

		if (args.length == 0) {
			sync();
		} else {
			switch (args[0]) {
			case "upload":
				upload(args);
			case "download":
				download(args);
			case "sync":
				sync(args);
			case "help":
				;
			default:
				;
			}
		}

	}

	public static void saveFromDiskToDb(String fileName) throws SQLException, IOException {

		SyncObject syncObject = getSyncObjectFromDisk(fileName);

		h2DbTool.saveSyncObjectToDB(syncObject);

	}

	public static void saveFromDbToDisk(String fileName) throws SQLException, IOException {

		SyncObject syncObject = h2DbTool.fetchSyncObjectFromDB(fileName);

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
			args = diskTool.getFullFileList();

			for (int i = 0; i < args.length; i++) {
				saveFromDiskToDb(args[i]);
			}
		} else {

			for (int i = 1; i < args.length; i++) {
				saveFromDiskToDb(args[i]);
			}
		}
	}

	private static void download(String args[]) throws SQLException, IOException {

		for (int i = 1; i < args.length; i++) {
			saveFromDbToDisk(args[i]);
		}

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
