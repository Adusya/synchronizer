package ru.unisuite.synchronizer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Date;

public class JournalWriter {

	public JournalWriter(String rootDirectory) throws UnsupportedEncodingException, FileNotFoundException {

		String journalFileName = "/journal.txt";
		String journalFullPath = rootDirectory + "/journal";
		
		checkIfExists(journalFullPath);

		fileWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(journalFullPath + journalFileName, true), "UTF-8"));
	}

	private Writer fileWriter;

	public void appendUploaded(String fileName) throws IOException {

		StringBuilder builder = new StringBuilder();
		builder.append(new Date(System.currentTimeMillis()) + String.format(" uploaded %s\n", fileName));
		
		fileWriter.append(builder);
		fileWriter.flush();
	}

	public void appendDownloaded(String fileName) throws IOException {

		StringBuilder builder = new StringBuilder();
		builder.append(new Date(System.currentTimeMillis()) + String.format(" downloaded %s\n", fileName));
		
		fileWriter.append(builder);
		fileWriter.flush();
	}

	public void close() throws IOException {
		if (fileWriter != null)
			fileWriter.flush();
			fileWriter.close();
	}
	
	private void checkIfExists(String journalFullPath) {
		
		File journalFile = new File(journalFullPath);
		
		if (!journalFile.exists()) {
			journalFile.mkdirs();
		}
		
	}
}
