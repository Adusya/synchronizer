package ru.unisuite.synchronizer;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import ru.unisuite.synchronizer.dbtool.DbTool;
import ru.unisuite.synchronizer.dbtool.DbToolProperties;
import ru.unisuite.synchronizer.dbtool.JdbcDbTool;
import ru.unisuite.synchronizer.disktool.DiskTool;

public class Synchronizer {

	// В качестве аргумента сначала приходит действие, которое необходимо выполнить,
	// потом перечисление файлов.
	// Если список параметров пусть, выполняется полная синхронизация
	public static void main(String args[]) throws SQLException, IOException, SynchronizerPropertiesException {
		
		SyncExecutor executor = new SyncExecutor();

		if (args.length == 0) {
			executor.sync();
		} else {
			switch (args[0]) {
			case "upload":
				executor.upload(args);
				break;
			case "download":
				executor.download(args);
				break;
			case "sync": 
				executor.sync(args);
				break;
			case "help":
				executor.helpCommand();
				break;
			default:
				System.out.println(String.format("unknown command %s. To view the list of commands use command \"help\"", args[0]));
				break;
			}
		}

		executor.close();
		
	}

}
