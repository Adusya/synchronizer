package ru.unisuite.synchronizer;

public enum StandartTag {

	development, d, production, p;

	public static boolean contains(String argument) {
		
		boolean result = false;
		
		for (StandartTag tag: StandartTag.values()) {
			if (tag.name().equals(argument))
				result = true;
		}
		
		return result;
		
	}
	
}
