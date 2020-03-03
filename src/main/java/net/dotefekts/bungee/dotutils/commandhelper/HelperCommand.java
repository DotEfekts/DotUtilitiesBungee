package net.dotefekts.bungee.dotutils.commandhelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.dotefekts.bungee.dotutils.UtilityFunctions;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

public class HelperCommand extends Command {
	private Plugin plugin;
	private String command;
	private String[] subcommand;
	private String permission;
	private String format;
	private boolean serverCommand;
	private Method executor;
	private Listener listener;
	
	HelperCommand(Plugin plugin, String command, String permission, String format, boolean serverCommand, Method executor, Listener listener, String... aliases) {
		super(command, permission, aliases);
		
		this.plugin = plugin;
		
		this.command = command.split(" ")[0];
		
		subcommand = new String[command.split(" ").length - 1];
		for(int i = 1; i < command.split(" ").length; i++)
			subcommand[i - 1] = command.split(" ")[i];
		
		this.format = format;
		this.serverCommand = serverCommand;
		this.permission = permission;
		this.executor = executor;
		this.listener = listener;
	}
	
	@Override
	public void execute(CommandSender commandSender, String[] args) {
		if(!serverCommand) {
			if(commandSender instanceof ProxiedPlayer) {
				if(!((ProxiedPlayer)commandSender).hasPermission(permission)) {
					UtilityFunctions.sendLegacyMessage(commandSender, ChatColor.RED + "You don't have permission to do that.");
					return;
				}
			} else {
				UtilityFunctions.sendLegacyMessage(commandSender, ChatColor.RED + "You must be a player to run this command.");
			}
		} else {
			if(commandSender instanceof ProxiedPlayer) {
				UtilityFunctions.sendLegacyMessage(commandSender, ChatColor.RED + "You cannot run this command as a player.");
			}
		}
		
		int len = subcommand.length;
		String[] newArgs = new String[args.length - len];
		
		for(int i = len; i < args.length; i++)
			newArgs[i - len] = args[i];
		
		if(!format.isEmpty()) {	
			if(format.equalsIgnoreCase("n") && newArgs.length > 0) {
				UtilityFunctions.sendLegacyMessage(commandSender, ChatColor.RED + "Error, too many arguments provided.");
				return;
			}
			
			String[] format = this.format.toLowerCase().split(" ");
			boolean optional = false;
			if(format.length > 0)
			if(!format[0].equalsIgnoreCase("n"))
			for(int i = 0; i < format.length || i < newArgs.length; i++){
				if(i < newArgs.length && i < format.length){
					if(format[i].startsWith("i")){
						try {
							Integer.parseInt(newArgs[i]);
						} catch(NumberFormatException e) {
							UtilityFunctions.sendLegacyMessage(commandSender, ChatColor.RED + "Error, the argument " + newArgs[i] + " must be a valid whole number.");
							return;
						}
					} else if(format[i].startsWith("d")){
						try {
							Double.parseDouble(newArgs[i]);
						} catch(NumberFormatException e) {
							UtilityFunctions.sendLegacyMessage(commandSender, ChatColor.RED + "Error, the argument " + newArgs[i] + " must be a valid decimal number.");
							return;
						}
					} else if(format[i].startsWith("p")){
						if(plugin.getProxy().getPlayer(newArgs[i]) == null) {
							UtilityFunctions.sendLegacyMessage(commandSender, ChatColor.RED + "Error, the argument " + newArgs[i] + " must be an online player.");
							return;
						}
					}
					
					if(format[i].substring(1, 1).equals("["))
						optional = true;
				} else {
					if(format.length > newArgs.length && optional == false) {
						if(!format[i].substring(1, 2).equals("[") && !format[i].equals("...")) {
							UtilityFunctions.sendLegacyMessage(commandSender, ChatColor.RED + "Error, not enough arguments provided.");
							return;
						}
					} else if(format.length < newArgs.length && !(format[format.length - 1].equalsIgnoreCase("..."))) {
						UtilityFunctions.sendLegacyMessage(commandSender, ChatColor.RED + "Error, too many arguments provided.");
						return;
					} else {
						break;
					}
				}
			}
		}
		
		try {
			 executor.invoke(listener, new CommandEvent(commandSender, this, newArgs));
			 return;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			UtilityFunctions.sendLegacyMessage(commandSender, ChatColor.RED + "An exception occurred during execution of the command. You should let the server owner know about this.");
			return;
		}
	}
	
	public String getPermission() {
		return permission;
	}

	void setPermission(String permission) {
		this.permission = permission;
	}

	public String getCommand() {
		return command;
	}
	
	public String[] getSubcommand(){
		return subcommand;
	}

	public String getFormat() {
		return format;
	}

	public boolean isServerCommand() {
		return serverCommand;
	}

	Method getExecutor() {
		return executor;
	}
	
	public String getParsedFormat() {
		String formatted = "";
		for(String str : format.split(" "))
			if(!str.isEmpty() && !str.equalsIgnoreCase("..."))
				formatted = formatted + " " + str.substring(1, str.length());
			else 
				formatted = formatted + " " + str;
		return formatted;
	}
	
	public String getSubcommandString() {
		String formatted = "";
		for(String str : subcommand)
			if(formatted.isEmpty())
				formatted = str;
			else 
				formatted = formatted + " " + str;
		return formatted;
	}
}
