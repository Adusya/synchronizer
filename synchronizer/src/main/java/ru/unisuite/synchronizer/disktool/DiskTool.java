package ru.unisuite.synchronizer.disktool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.sql.Timestamp;

import ru.unisuite.synchronizer.SyncObject;

public class DiskTool {

	private String rootDirectory;

	public DiskTool(String rootDirectory) {

		this.rootDirectory = rootDirectory;
//		this.rootDirectory = "C:\\Users\\romanov\\Desktop\\Synchronizer\\SyncFolder";

		File rootFolder = new File(rootDirectory);

		if (!rootFolder.exists()) {
			rootFolder.mkdirs();
		}

	}

	public SyncObject getSyncObjectByName(String fileName) throws IOException {

		String fileFullPath = rootDirectory + File.separator + fileName;

		File file = new File(fileFullPath);
		
		SyncObject syncObject = null;
		
		if (file.exists()) {
			Timestamp modificationDate = new Timestamp(file.lastModified());
			Reader fileReader = new FileReader(file);
			String clob = readToString(fileReader);
			fileReader.close();

			syncObject = new SyncObject(null, fileName, modificationDate, clob);
		}

		return syncObject;

	}

	public void saveSyncObjectToDisk(SyncObject syncObject) throws IOException {

		File syncFile = new File(rootDirectory + File.separator + syncObject.getAlias());

		FileWriter writer = new FileWriter(syncFile);
		writer.write(syncObject.getClob());
		writer.close();

		syncFile.setLastModified(syncObject.getTimestamp().getTime());

	}

	public String readToString(Reader reader) throws IOException {

		int intValueOfChar;
		String targetString = "";
		while ((intValueOfChar = reader.read()) != -1) {
			targetString += (char) intValueOfChar;
		}

		return targetString;
	}
	
	public String[] getFullFileList() {
		
		File rootFolder = new File(rootDirectory);
		
		return rootFolder.list();
		
	}
}
