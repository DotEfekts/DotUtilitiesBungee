package net.dotefekts.bungee.dotutils.commandhelper;

import java.util.HashMap;
import net.md_5.bungee.api.plugin.Plugin;

public class CommandHelper {
	private static CommandHelper instance;
	
	private CommandManager manager;
	private CommandRegistration register;
	private HashMap<String, HelperCommand> commands;
	private HashMap<String, Plugin> commandSource;
	
	private CommandHelper(Plugin plugin){
		manager = new CommandManager(plugin, this);
		register = new CommandRegistration();
		commands = new HashMap<String, HelperCommand>();
		commandSource = new HashMap<String, Plugin>();
	}
	
	HelperCommand getCommand(String command){
		if(commands.containsKey(command))
			return commands.get(command);
		else
			return null;
	}
	
	void registerCommand(HelperCommand cmd, Plugin plugin){
		register.registerCommand(cmd, plugin);
		commands.put(cmd.getName(), cmd);
		commandSource.put(cmd.getName(), plugin);
	}
	
	public static CommandManager get(Plugin plugin){
		if(instance == null)
			instance = new CommandHelper(plugin);
		return instance.manager;
	}
}
