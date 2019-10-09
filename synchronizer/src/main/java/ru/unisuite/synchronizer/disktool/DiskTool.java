package ru.unisuite.synchronizer.disktool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ru.unisuite.synchronizer.SyncObject;

public class DiskTool {

	private String rootDirectory;
	
	private String reportExtention = ".rptdesign";

	public DiskTool(String rootDirectory) {

		this.rootDirectory = rootDirectory;

		File rootFolder = new File(rootDirectory);

		if (!rootFolder.exists()) {
			rootFolder.mkdirs();
		}

	}

	public SyncObject getSyncObjectByName(String alias) throws IOException {

		String fileFullPath = rootDirectory + File.separator + alias + reportExtention;

		deleteTagValue(fileFullPath, "encrypted-property");
		
		File file = new File(fileFullPath);

		SyncObject syncObject = null;

		if (file.exists() && file.isFile()) {
//			Timestamp modificationDate = new Timestamp(file.lastModified());
			String clob = readFileToString(fileFullPath);

			syncObject = new SyncObject(null, alias, clob, null);
		}

		return syncObject;

	}

	public void saveSyncObjectToDisk(SyncObject syncObject) throws IOException {

		File syncFile = new File(rootDirectory + File.separator + syncObject.getAlias() + reportExtention);

		try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(syncFile),
				StandardCharsets.UTF_8)) {

			writer.write(syncObject.getClob());
		}

//		syncFile.setLastModified(syncObject.getTimestamp().getTime());

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

	public void deleteTagValue(String fileName, String tagName) {

		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(fileName);
			doc.getDocumentElement().normalize();
					
			Element dataSource = (Element) doc.getElementsByTagName("data-sources").item(0);

			NodeList list = dataSource.getElementsByTagName(tagName);

			for (int i = 0; i < list.getLength(); i++) {
				Node node = list.item(i);
				if (list.item(i).getAttributes().getNamedItem("name").getTextContent().equals("odaPassword"))
					node.setTextContent("password");
			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(fileName);
			transformer.transform(source, result);

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (SAXException sae) {
			sae.printStackTrace();
		}

	}
}
