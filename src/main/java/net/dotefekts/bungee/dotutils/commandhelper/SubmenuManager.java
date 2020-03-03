package net.dotefekts.bungee.dotutils.commandhelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.dotefekts.bungee.dotutils.UtilityFunctions;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;


class SubmenuManager {
	private Plugin plugin;
	private HashMap<String, List<HelperCommand>> submenus;
	
	SubmenuManager(Plugin plugin){
		this.plugin = plugin;
		submenus = new HashMap<String, List<HelperCommand>>();
	}
	
	List<HelperCommand> getSubcommands(String command) {
		if(submenus.containsKey(command))
			return submenus.get(command);
		else return null;
	}
	
	void addSubcommand(HelperCommand command) {
		if(submenus.containsKey(command.getCommand())) {
			boolean allGood = true;
			for(HelperCommand cmd : submenus.get(command.getCommand())) {
				if(SubmenuManager.compareStringArr(cmd.getSubcommand(), command.getSubcommand()))
					allGood = false;
			}
			if(allGood)
				submenus.get(command.getCommand()).add(command);
			else
				plugin.getLogger().warning("[CommandHelper] Tried to register a duplicate submenu.");
		} else {
			ArrayList<HelperCommand> newList = new ArrayList<HelperCommand>();
			newList.add(command);
			submenus.put(command.getCommand(), newList);
		}
	}

	void addList(List<HelperCommand> newCommands) {
		for(HelperCommand cmd : newCommands)
			addSubcommand(cmd);
	}
	
	static boolean compareStringArr(String[] subOne, String[] subTwo){
		if(subOne.length == subTwo.length){
			boolean good = true;
			for(int i = 0; i < subOne.length; i++)
				if(!subOne[i].equalsIgnoreCase(subTwo[i]))
					good = false;
			return good;
		}
		return false;
	}
	
	static ArrayList<String> generateSubmenu(List<HelperCommand> commands, String[] submenu){
		if(commands.size() == 0)
			return null;
		String sub = UtilityFunctions.joinArray(submenu, " ");
		ArrayList<String> menu = new ArrayList<String>();
		ArrayList<String> menus = new ArrayList<String>();
		ArrayList<HelperCommand> menuCommands = new ArrayList<HelperCommand>();
		
		for(HelperCommand command : commands) {
			if(command.getSubcommand().length > 0)
				if(command.getSubcommandString().startsWith(sub) 
						&& command.getSubcommand().length > submenu.length)
					if(command.getSubcommand().length == submenu.length + 1)
						menuCommands.add(command);
					else if(!menus.contains(command.getSubcommand()[submenu.length]))
						menus.add(command.getSubcommand()[submenu.length]);
		}
		
		String header = ChatColor.DARK_AQUA + "-- " + ChatColor.BLUE + 
				(commands.get(0).isServerCommand() ? "" : "/") 	+ commands.get(0).getCommand()
				+ " " + ChatColor.YELLOW + sub + (sub.isEmpty() ? "" : " ")
				+ ChatColor.DARK_AQUA + " --";
		menu.add(header);
		
		String base = ChatColor.YELLOW + "";
		
		for(HelperCommand cmd : menuCommands) {
			String format = cmd.getParsedFormat();
			String highSub = cmd.getSubcommand()[cmd.getSubcommand().length - 1];
			
			menu.add(base + highSub + ChatColor.GOLD + format);
		}
		
		for(String str : menus) {		
			menu.add(base + str);
			menu.add("  " + ChatColor.GRAY + "Access the " + str + " submenu.");
		}
		return menu;
	}
}
