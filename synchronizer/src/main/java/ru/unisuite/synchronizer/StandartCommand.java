package ru.unisuite.synchronizer;

public enum StandartCommand {
	sync, upload, download, help;

	public static boolean contains(String argument) {
		
		boolean result = false;
		
		for (StandartCommand command: StandartCommand.values()) {
			if (command.name().equals(argument))
				result = true;
		}
		
		return result;
		
	}
	
}