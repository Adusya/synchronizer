package ru.unisuite.synchronizer.disktool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import ru.unisuite.synchronizer.SyncObject;

public class DiskTool {

	private String rootDirectory;

	public DiskTool(String rootDirectory) {

		this.rootDirectory = rootDirectory;

		File rootFolder = new File(rootDirectory);

		if (!rootFolder.exists()) {
			rootFolder.mkdirs();
		}

	}

	public SyncObject getSyncObjectByName(String fileName) throws IOException {

		String fileFullPath = rootDirectory + File.separator + fileName;

		File file = new File(fileFullPath);

		SyncObject syncObject = null;

		if (file.exists() && file.isFile()) {
			Timestamp modificationDate = new Timestamp(file.lastModified());
			String clob = readFileToString(fileFullPath);

			syncObject = new SyncObject(null, fileName, modificationDate, clob);
		}

		return syncObject;

	}

	public void saveSyncObjectToDisk(SyncObject syncObject) throws IOException {

		File syncFile = new File(rootDirectory + File.separator + syncObject.getAlias());

		try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(syncFile),
				StandardCharsets.UTF_8)) {

			writer.write(syncObject.getClob());
		}

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

	public List<String> getFullFileList() {

		List<String> arrayListFiles = new ArrayList<>();

		File rootFolder = new File(rootDirectory);

		File[] listFiles = rootFolder.listFiles();

		for (File file : listFiles) {
			if (file.isFile())
				arrayListFiles.add(file.getName());

		}

		return arrayListFiles;

	}

	public String readFileToString(String fileName) throws IOException {

		StringBuilder sb = new StringBuilder();

		// read file into stream, try-with-resources
		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {

			stream.forEach(line -> {
				sb.append(line + "\n"); // add space so that lines don't stick to each other
			});

		}

		return sb.toString();
	}
}
