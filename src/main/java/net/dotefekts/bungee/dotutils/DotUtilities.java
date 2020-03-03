package net.dotefekts.bungee.dotutils;

import net.dotefekts.bungee.dotutils.commandhelper.CommandHelper;
import net.dotefekts.bungee.dotutils.commandhelper.CommandManager;
import net.md_5.bungee.api.plugin.Plugin;

public class DotUtilities extends Plugin {
	private static CommandManager commandHelper;
	
	@Override
	public void onEnable(){
		DotUtilities.commandHelper = CommandHelper.get(this);
		
		getLogger().info("DotUtilities loaded.");
	}
	
	public static CommandManager getCommandHelper() {
		return commandHelper;
	}
}
