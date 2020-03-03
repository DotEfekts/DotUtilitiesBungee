package net.dotefekts.bungee.dotutils;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class UtilityFunctions {

	public static String joinArray(String[] arr, String seperator){
		String result = "";
		for(String str : arr)
			if(result.isEmpty())
				result = str;
			else
				result = result + seperator + str;
		return result;
	}
	
	public static void sendLegacyMessage(CommandSender commandSender, String legacyMessage) {
		commandSender.sendMessage(TextComponent.fromLegacyText(legacyMessage));
	}
}
