package ru.unisuite.synchronizer.dbtool;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import ru.unisuite.synchronizer.SyncObject;

public interface DbTool {
	
	public Connection getConnection();
	
	public SyncObject fetchSyncObjectFromDB(String fileName) throws SQLException, IOException;
	
	public void createSyncObjectInDB(SyncObject syncObject) throws SQLException, IOException;
	
	public String readToString(Reader reader) throws IOException;
	
	public List<String> getFullFileList() throws SQLException;
	
	public boolean exists(String alias) throws SQLException;
	
	public void updateSyncObjectInDB(SyncObject syncObject) throws SQLException, IOException;

}
