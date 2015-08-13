package de.skysoldier.spleex;

import java.sql.ResultSet;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class CommandHandler {

	private Main plugin;
	
	public CommandHandler(Main plugin){
		this.plugin = plugin;
	}

	
	public void perform(Player player, String command, String args[]){
		if(command.equalsIgnoreCase("spleex")){
			if(args.length > 0){
				if(args[0].equalsIgnoreCase("remove")){
					String result = spleexRemove(subArgs(1, args));
					if(result != "") player.sendMessage(result);
					return;
				}
				if(args[0].equalsIgnoreCase("add")){
					String result = spleexAdd(player, subArgs(1, args));
					if(result != "") player.sendMessage(result);
					return;
				}
				if(args[0].equalsIgnoreCase("join")){
					String result = spleexJoin(player, subArgs(1, args));
					if(result != "") player.sendMessage(result);
					return;
				}
				if(args[0].equalsIgnoreCase("list")){
					String result = spleexList(subArgs(1, args));
					if(result != "") player.sendMessage(result);
					return;
				}
				if(args[0].equalsIgnoreCase("stats")){
					String result = spleexStats(player, subArgs(1, args));
					if(result != "") player.sendMessage(result);
					return;
				}
				if(args[0].equalsIgnoreCase("help")){
					String result = spleexHelp(subArgs(1, args));
					if(result != "") player.sendMessage(result);
					return;
				}
				if(args[0].equalsIgnoreCase("register")){
					plugin.addOnlinePlayer(player);
					return;
				}
			}
			player.sendMessage(ChatColor.YELLOW + "Available commands: " + ChatColor.GRAY + "remove, add, join, list, stats, help");
		}
	}
	
	private String[] subArgs(int level, String args[]){
		if(level > args.length) throw new IllegalArgumentException("level cannot be greater than args.length.");
		String subArgs[] = new String[args.length - level];
		for(int i = 0; i < subArgs.length; i++){
			subArgs[i] = args[i + level];
		}
		return subArgs;
	}
	
	private String spleexAdd(Player player, String args[]){
		if(args.length == 2){
			if(!plugin.getConfig().contains("arenas." + args[0])){
				ArrayList<Material> removeableMaterials = new ArrayList<>();
				String rawRemovableMaterials[] = args[1].toUpperCase().split(",");
				try{
					for(int i = 0; i < rawRemovableMaterials.length; i++){
						removeableMaterials.add(Material.valueOf(rawRemovableMaterials[i]));
					}
				}
				catch(Exception e){
					return ChatColor.YELLOW + "One or more Material doesnt exist.";
				}
				plugin.getConfig().set("arenas." + args[0], new Arena(player.getLocation(), removeableMaterials).serialize());
				plugin.saveConfig();
				return ChatColor.GREEN + "Succesfully created arena: " + ChatColor.GRAY + args[0]; 
			}
			else {
				return ChatColor.YELLOW + "Arena already exists: " + ChatColor.GRAY + args[0];
			}
		}
		else {
			return sayInvalidArguments("nameOfArena, List[removeable materials]");
		}
	}
	
	private String spleexRemove(String args[]){
		if(args.length == 1){
			if(plugin.getConfig().contains("arenas." + args[0])){
				plugin.getConfig().set("arenas." + args[0], null);
				plugin.saveConfig();
				return ChatColor.GREEN + "Succesfully removed arena: " + ChatColor.GRAY + args[0];
			}
			else {
				return ChatColor.YELLOW + "Arena not found: " + ChatColor.GRAY + args[0];
			}
		}
		else {
			return sayInvalidArguments("nameOfArena");
		}
	}
	
	private String spleexJoin(Player player, String args[]){
		if(args.length == 1){
			Game game = plugin.getGame(args[0]);
			if(plugin != null){
				game.addPlayer(player);
				return "";
			}
			else {
				return "Game doesnt exist.";
			}
		}
		else {
			return sayInvalidArguments("nameOfGame");
		}
	}
	
	private String spleexList(String args[]){
		if(args.length == 0){
			return ChatColor.YELLOW + "Free games: " + plugin.getFreeGames();
		}
		else {
			return sayInvalidArguments("[no]");
		}
	}
	
	private String spleexStats(Player player, String args[]){
		if(args.length == 0){
			ResultSet set = plugin.getMysql().executeQuery("SELECT * FROM SpleexStats WHERE playerName = '" + player.getDisplayName() + "'");
			plugin.getMysql().next(set);
			String stats = ChatColor.YELLOW + "------------------\n" + ChatColor.AQUA + "Your stats:\n";
			stats += ChatColor.DARK_PURPLE + "  wins: " + ChatColor.GRAY + plugin.getMysql().getString(set, "wins");
			stats += ChatColor.DARK_PURPLE + "\n  deaths: " + ChatColor.GRAY + plugin.getMysql().getString(set, "deaths");
			stats += ChatColor.DARK_PURPLE + "\n  blocks destroyed: " + ChatColor.GRAY + plugin.getMysql().getString(set, "blocksDestroyed");
			stats += ChatColor.YELLOW + "\n------------------";
			return stats;
		}
		else {
			return sayInvalidArguments("[no]");
		}
	}
	
	private String spleexHelp(String args[]){
		if(args.length == 0){
			String help = ChatColor.AQUA + "~*~.~_~.~*~" + ChatColor.YELLOW + " Spleex Help " + ChatColor.AQUA + "~*~.~_~.~*~\n";
			help += ChatColor.LIGHT_PURPLE + "!! " + ChatColor.GRAY + "Base command: /spleex" + ChatColor.LIGHT_PURPLE + "  !!\n";
			help += ChatColor.AQUA + "-->  " + ChatColor.YELLOW + "add:  " + ChatColor.GRAY + "Adds an arena at your place.\n";
			help += ChatColor.AQUA + "-->  " + ChatColor.YELLOW + "remove:  " + ChatColor.GRAY + "Removes an arena by its name.\n";
			help += ChatColor.AQUA + "-->  " + ChatColor.YELLOW + "join:  " + ChatColor.GRAY + "Puts you in the Game if its not full.\n";
			help += ChatColor.AQUA + "-->  " + ChatColor.YELLOW + "list:  " + ChatColor.GRAY + "Shows the games you can join.\n";
			help += ChatColor.AQUA + "-->  " + ChatColor.YELLOW + "stats:  " + ChatColor.GRAY + "Lists your spleex stats.\n";
			help += ChatColor.LIGHT_PURPLE + "!!  " + ChatColor.GRAY + "add / remove needs a server restart" + ChatColor.LIGHT_PURPLE + "  !!\n";
			help += ChatColor.AQUA + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~";
			//remove, add, join, list, stats, help
			return help;
		}
		else {
			return sayInvalidArguments("[no]");
		}
	}
	
	private String sayInvalidArguments(String expectedArguments){
		return ChatColor.YELLOW + "Invalid number of arguments. Required: " + ChatColor.GRAY + expectedArguments;
	}
}
