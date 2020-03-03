package net.dotefekts.bungee.dotutils.commandhelper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

public class CommandManager {
	private Plugin plugin;
	private HashMap<String, Plugin> commandLookup;
	private CommandHelper helper;
	private SubmenuManager submanager;
	
	CommandManager(Plugin plugin, CommandHelper helper) {
		this.plugin = plugin;
		this.helper = helper;
		commandLookup = new HashMap<String, Plugin>();
		submanager = new SubmenuManager(plugin);
	}
	
	public void registerCommands(Listener listener, Plugin plugin) {
		int intMethods = 0;
		int intCommands = 0;
		Class<? extends Listener> listenerClass = listener.getClass();
		for(Method method : listenerClass.getDeclaredMethods()){			
			if(method.isAnnotationPresent(CommandHandlers.class)) {
				if(method.getReturnType() != void.class) {
					plugin.getLogger().warning("[CommandHelper] Tried to register " + method.getName() + " but had a return type that was not void.");
					continue;
				}
					
				if(method.getParameterTypes().length == 1) {
					if(method.getParameterTypes()[0].equals(CommandEvent.class)) {
						CommandHandler[] annos = method.getAnnotation(CommandHandlers.class).value();
						ArrayList<HelperCommand> newCommands = new ArrayList<HelperCommand>();
						HashMap<String, Plugin> newCommandLookup = new HashMap<String, Plugin>();
						
						String command = "";
						String permission = "";
						String format = "";
						boolean serverCommand = false;
					
						for(CommandHandler handler : annos) {
							
							if(checkValidSilent(handler.command(), method.getName()))
								command = handler.command();
							else if(command.isEmpty())
								if(!checkValid(handler.command(), method.getName()))
									continue;
							
							if(!handler.permission().isEmpty())
								permission = handler.permission();
								
							if(checkFormat(handler.format()))
								if(!handler.format().isEmpty())
									format = handler.format();
								else;
							else {
								plugin.getLogger().warning("[CommandHelper] Format error on command " + handler.command());
								continue;
							}
								
							serverCommand = handler.serverCommand();
								
							newCommands.add(new HelperCommand(plugin, command, permission, format, serverCommand, method, listener));
							intCommands++;
							if(!newCommandLookup.containsKey(command))
								newCommandLookup.put(command, plugin);
						}
						
						for(HelperCommand cmd : newCommands){
							registerCommand(cmd, plugin);
						}

						submanager.addList(newCommands);
						commandLookup.putAll(newCommandLookup);
						intMethods++;
					} else {
						plugin.getLogger().warning("[CommandHelper] Parameter type for method " + method.getName() + " incorrect for CommandHandler");
					}
				} else {
					plugin.getLogger().warning("[CommandHelper] Number of parameters for method " + method.getName() + " incorrect.");
				}
			} else if(method.isAnnotationPresent(CommandHandler.class)){
				if(method.getReturnType() != void.class) {
					plugin.getLogger().warning("[CommandHelper] Tried to register " + method.getName() + " but had a return type that was not void.");
					continue;
				}
				
				CommandHandler handler = method.getAnnotation(CommandHandler.class);
				
				if(checkValid(handler.command(), method.getName())){
					
					if(!checkFormat(handler.format())) {
						plugin.getLogger().warning("[CommandHelper] Format error on command " + handler.command());
						continue;
					}
					
					HelperCommand command = new HelperCommand(plugin, handler.command(), handler.permission(), handler.format(), handler.serverCommand(), method, listener);
					submanager.addSubcommand(command);
					
					intCommands++;
					commandLookup.put(handler.command(), plugin);
						
					registerCommand(command, plugin);
					intMethods++;
				}
			}
		}
		plugin.getLogger().info("[CommandHelper] " + intCommands + " commands registered to " + intMethods + " methods.");
	}
	
	private boolean checkValidSilent(String command, String name) {
		if(command.isEmpty())
			return false;
		else if(commandLookup.containsKey(command))
			return false;
		return true;
	}

	private boolean checkFormat(String format) {
		String[] arr = format.split(" ");
		
		for(String str : arr){
			if(!str.isEmpty())
			if(str.equalsIgnoreCase("n"))
				if(arr.length != 1)
					return false;
				else;
			else if(str.length() < 3)
				return false;
			else {
				switch(str.substring(1, 2)) {
					case "[":
						if(!str.substring(str.length() - 1, str.length()).equalsIgnoreCase("]"))
							return false;
						break;
					case "<":
						if(!str.substring(str.length() - 1, str.length()).equalsIgnoreCase(">"))
							return false;
						break;
					case ".":
						if(!str.equalsIgnoreCase("..."))
							return false;
						break;
				}
				
				switch(str.substring(0, 1)) {
					case "i":
					case "d":
					case "p":
					case "s":
					case ".":
						break;
					default:
						return false;
				}
			}
		}
		return true;
	}

	ArrayList<HelperCommand> matchCommands(String command, boolean server) {
		ArrayList<HelperCommand> cmdList = new ArrayList<HelperCommand>();
		for(HelperCommand cmd : submanager.getSubcommands(command))
			if(cmd.isServerCommand() == server)
				cmdList.add(cmd);
		return cmdList;
	}
	
	HelperCommand matchCommand(String command, String[] args, boolean server){
		HelperCommand matched = null;
		int len = -1;
		for(HelperCommand cmd : matchCommands(command, server)){
			if(matchArgs(cmd.getSubcommand(), args) && len < cmd.getSubcommand().length) {
				matched = cmd;
				len = cmd.getSubcommand().length;
			}
		}
		return matched;
	}
	
	private boolean matchArgs(String[] subcommand, String[] args) {
		if(args.length < subcommand.length)
			return false;
		else for(int i = 0; i < subcommand.length; i++)
			if(!subcommand[i].equalsIgnoreCase(args[i]))
				return false;
		return true;
	}

	private void registerCommand(HelperCommand cmd, Plugin plugin) {
		helper.registerCommand(cmd, plugin);
	}

	private boolean checkValid(String command, String method) {
		if(command.isEmpty()) {
			plugin.getLogger().warning("[CommandHelper] No command supplied for the method " + method);
			return false;
		} else if(commandLookup.containsKey(command)) {
			plugin.getLogger().warning("[CommandHelper] Method " + method+ " tried to register a command registered to another method.");
			return false;
		}
		return true;
	}

	SubmenuManager getSubmanager() {
		return submanager;		
	}
}
