package ru.unisuite.synchronizer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Synchronizer {

	private final static String defaultTag = "development";
	private final static String defaultCommand = "sync";

	// В качестве аргумента сначала приходит действие, которое необходимо выполнить,
	// далее тег, определяющий парметры запуска, потом перечисление файлов.
	// Если список параметров пусть, выполняется полная синхронизация
	public static void main(String args[]) throws SQLException, IOException, SynchronizerPropertiesException {
		
		StandartTag tag = getTag(args);

		SyncExecutor executor = new SyncExecutor(tag);

		StandartCommand command = getCommand(args);

		boolean conTag = false;
		boolean conCommand = false;
		if (tag != null)
			conTag = true;
		
		if (command != null)
			conCommand = true;
		
		List<String> fileNamesList = getFileNamesList(args, conTag, conCommand);
		
		switch (command) {
		case upload:
			executor.upload(fileNamesList);
			break;
		case download:
			executor.download(fileNamesList);
			break;
		case sync:
			executor.sync(fileNamesList);
			break;
		case help:
			executor.helpCommand();
			break;
		default:
			System.out.println(
					String.format("unknown command %s. To view the list of commands use command \"help\"", args[0]));
			break;
		}

		executor.close();

	}

	private static StandartCommand getCommand(String args[]) {

		StandartCommand command = null;

		if (args.length == 0) {
			return command = StandartCommand.valueOf(defaultCommand);
		}

		if (StandartCommand.contains(args[0])) {
			command = StandartCommand.valueOf(args[0]);
		}

		return command;
	}

	private static StandartTag getTag(String args[]) {

		StandartTag tag = null;

		if (args.length == 0) {
			return StandartTag.valueOf(defaultTag);
		}

		for (String argument : args) {
			if (argument.startsWith("-"));
				if (StandartTag.contains(argument))
					tag = StandartTag.valueOf(argument);
		}

		return tag;
	}
	
	private static List<String> getFileNamesList(String args[], boolean conTag, boolean conCommand) {
		
		List<String> fileNamesList = new ArrayList<>();
		
		int start = 0;
		
		if (conTag)
			start++;
		
		if (conCommand)
			start++;
		
		for (int i = start; i < args.length; i++) {
			
			fileNamesList.add(args[0]);
			
		}
		
		return fileNamesList;
	}

}
