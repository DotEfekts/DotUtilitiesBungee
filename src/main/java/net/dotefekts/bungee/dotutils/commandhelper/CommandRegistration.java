package net.dotefekts.bungee.dotutils.commandhelper;

import net.md_5.bungee.api.plugin.Plugin;

class CommandRegistration {
	
	void registerCommand(HelperCommand executor, Plugin plugin){
		plugin.getProxy().getPluginManager().registerCommand(plugin, executor);
	}
}
