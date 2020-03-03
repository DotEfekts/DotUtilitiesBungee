package net.dotefekts.bungee.dotutils.commandhelper;

import net.md_5.bungee.api.CommandSender;

public class CommandEvent {
	private CommandSender sender;
	private HelperCommand command;
	private String[] args;
	
	CommandEvent(CommandSender commandSender, HelperCommand command, String[] args){
		this.sender = commandSender;
		this.command = command;
		this.args = args;
	}
	
	public CommandSender getSender(){
		return sender;
	}
	
	public HelperCommand getCommand() {
		return command;
	}
	
	public String[] getArgs(){
		return args;
	}
}
