package ru.unisuite.synchronizer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Synchronizer {

	private final static String defaultTag = "p";
	private final static String defaultCommand = "download";

	// В качестве аргумента сначала приходит действие, которое необходимо выполнить,
	// далее тег, определяющий парметры запуска, потом перечисление файлов.
	// Если список параметров пусть, выполняется полная синхронизация
	public static void main(String args[]) throws SQLException, IOException, SynchronizerPropertiesException {

		StandartTag tag = getTag(args);
		StandartCommand command = getCommand(args);

		boolean conTag = false;
		boolean conCommand = false;
		if (tag != null) {
			conTag = true;
		} else {
			tag = StandartTag.valueOf(defaultTag);
		}

		if (command != null) {
			conCommand = true;
		} else {
			command = StandartCommand.valueOf(defaultCommand);
		}

		SyncExecutor executor = new SyncExecutor(tag);

		List<String> fileNamesList = getFileNamesList(args, conTag, conCommand);

		switch (command) {
		case upload:
			executor.upload(fileNamesList);
			break;
		case download:
			executor.download(fileNamesList);
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

		if (args.length != 0) {
			if (StandartCommand.contains(args[0])) {
				command = StandartCommand.valueOf(args[0]);
			}
		}

		return command;
	}

	private static StandartTag getTag(String args[]) {

		StandartTag tag = null;

		if (args.length != 0) {
			for (String argument : args) {
				if (argument.startsWith("-")) {
					argument = argument.substring(1);
					if (StandartTag.contains(argument))
						tag = StandartTag.valueOf(argument);
					else
						System.out.println(String.format("Unknown tag -%s", argument));
				}
			}
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
			if (!args[i].startsWith("-"))
				fileNamesList.add(args[i]);
		}

		return fileNamesList;
	}

}
