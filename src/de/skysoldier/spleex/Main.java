package de.skysoldier.spleex;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	public static String WORLD_NAME = "world";
	public static World WORLD;
	public static Location WORLD_SPAWN;
	private CommandHandler commandHandler;
	private HashMap<String, Game> games;
	private HashMap<Player, OnlinePlayer> onlinePlayer;
	private Mysql mysql;
	
	public void onEnable(){
		WORLD_NAME = "world";
		WORLD = getServer().getWorld(WORLD_NAME);
		WORLD_SPAWN = new Location(WORLD, 535, 90, 72);
		WORLD.setPVP(false);
		WORLD.setSpawnLocation(WORLD_SPAWN.getBlockX(), WORLD_SPAWN.getBlockY(), WORLD_SPAWN.getBlockZ());
		commandHandler = new CommandHandler(this);
		onlinePlayer = new HashMap<>();
		mysql = new Mysql("sql3.freesqldatabase.com", "sql331898", 3306, "sql331898", "eQ7*xC2*");
		initGames();
		new EventManager(this);
	}
	
	private void initGames(){
		games = new HashMap<>();
		Set<String> arenaSections = getConfigKeySet("arenas", false);
		for(String s : arenaSections){
			games.put(s, new Game(this, new Arena(s, getConfigHashMap("arenas." + s))));
		}
	}
	
	private HashMap<String, Object> getConfigHashMap(String path){
		HashMap<String, Object> map = new HashMap<>();
		Set<String> keys = getConfigKeySet(path, false);
		for(String s : keys){
			map.put(s, getConfig().get(path + "." + s));
		}
		return map;
	}
	
	private Set<String> getConfigKeySet(String path, boolean recursive){
		if(path == ""){
			return getConfig().getKeys(recursive);
		}
		else {
			return getConfig().getConfigurationSection(path).getKeys(recursive);
		}
	}
	
	public Game getGame(String name){
		return games.get(name);
	}
	
	public void addOnlinePlayer(Player player){
		ResultSet playerDB = mysql.executeQuery("SELECT * FROM SpleexStats WHERE playerName = '" + player.getDisplayName() + "'");
		if(!mysql.isEmpty(playerDB)){
			mysql.next(playerDB);
		}
		else {
			mysql.executeUpdate("INSERT INTO SpleexStats (playerName, wins, deaths, blocksDestroyed) VALUES('" + player.getDisplayName() + "', 0, 0, 0)");
		}
		onlinePlayer.put(player, new OnlinePlayer(player));
		player.teleport(WORLD_SPAWN);
	}
	
	public OnlinePlayer getOnlinePlayer(Player player){
		return onlinePlayer.get(player);
	}
	
	public void removeOnlinePlayer(Player player){
		onlinePlayer.remove(player);
	}
	
	public String getFreeGames(){
		String games = "";
		Set<String> keys = this.games.keySet();
		for(String s : keys){
			if(!this.games.get(s).isRunning()){
				games += this.games.get(s).getArenaName() + ",";
			}
		}
		return games.substring(0, games.length() - 1);
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = null;
		try{
			player = (Player) sender;
		}
		catch(ClassCastException e){
			sender.sendMessage(ChatColor.YELLOW + "Sorry, interaction with spleex is possible only ingame.");
			return true;
		}
		String cmd = command.getName();
		commandHandler.perform(player, cmd, args);
		return true;
	}
	
	public Mysql getMysql(){
		return mysql;
	}
	
	public void increaseStatValue(Player player, String statName, int value){
		mysql.executeUpdate("UPDATE SpleexStats SET " + statName + " = " + statName + " + " + value + " WHERE playerName = '" + player.getDisplayName() + "'");
	}
}	