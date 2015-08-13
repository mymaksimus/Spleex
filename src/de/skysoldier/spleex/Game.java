package de.skysoldier.spleex;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Game {

	private Main plugin;
	private Arena arena;
	private ArrayList<OnlinePlayer> player;
	private ArrayList<Block> removedBlocks;
	private ArrayList<Material> removedBlocksMaterials;
	private boolean running;
	private int minPlayer = 2;
	
	public Game(Main plugin, Arena arena){
		this.plugin = plugin;
		this.arena = arena;
		player = new ArrayList<>();
		removedBlocks = new ArrayList<>();
		removedBlocksMaterials = new ArrayList<>();
		running = false;
	}
	
	public void removeBlock(Player player, Block block){
		removedBlocks.add(block);
		removedBlocksMaterials.add(block.getType());
		block.setType(Material.AIR);
		plugin.getOnlinePlayer(player).increaseBlocksDestroyedInRound();
	}
	
	private void rebuildArena(){
		for(int i = 0; i < removedBlocks.size(); i++){
			removedBlocks.get(i).setType(removedBlocksMaterials.get(i));
		}
		removedBlocks.clear();
		removedBlocksMaterials.clear();
	}
	
	public void removePlayer(OnlinePlayer player, String msg){
		plugin.increaseStatValue(player.getPlayer(), "blocksDestroyed", player.getBlocksDestroyedInRound());
		plugin.increaseStatValue(player.getPlayer(), "deaths", 1);
		player.resetBlocksDestroyedInRound();
		this.player.remove(player);
		player.setGame(null);
		if(this.player.size() > 1){
			broadcast(ChatColor.GRAY + player.getPlayer().getDisplayName() + ChatColor.YELLOW + msg + "." + ChatColor.GRAY + this.player.size() + " players left.");
		}
		else {
			this.player.get(0).getPlayer().teleport(Main.WORLD_SPAWN);
			plugin.getServer().broadcastMessage(ChatColor.GRAY + this.player.get(0).getPlayer().getDisplayName() + ChatColor.GREEN + " won spleex in arena: " + ChatColor.GRAY + arena.getName());
			plugin.increaseStatValue(this.player.get(0).getPlayer(), "wins", 1);
			plugin.increaseStatValue(this.player.get(0).getPlayer(), "blocksDestroyed", this.player.get(0).getBlocksDestroyedInRound());
			this.player.get(0).resetBlocksDestroyedInRound();
			this.player.clear();
			running = false;
			rebuildArena();
		}
	}
	
	public void addPlayer(Player player){
		if(!running){
			this.player.add(plugin.getOnlinePlayer(player));
			plugin.getOnlinePlayer(player).setGame(this);
			player.teleport(arena.getSpawnLocation());
			broadcast(ChatColor.GREEN + player.getDisplayName() + ChatColor.GRAY + " joined the game.");
			if(this.player.size() >= minPlayer){
				start();
			}
			else {
				broadcast(ChatColor.GRAY + "waiting for more players...   [" + ChatColor.RED + minPlayer + ChatColor.GRAY + "] to start");
			}
		}
		else {
			player.sendMessage(ChatColor.YELLOW + "Game is already running. Try another arena.");
		}
	}
	
	private void start(){
		new Thread(new BukkitRunnable() {
			public void run(){
				broadcast(ChatColor.AQUA + "game will start in: ");
				for(int i = 3; i > 0; i--){
					broadcast("[" + i + "]");
					try{Thread.sleep(1000);}catch(Exception e){}
				}
				running = true;
			}
		}).start();
	}
	
	public void broadcast(String message){
		for(OnlinePlayer player : this.player){
			player.getPlayer().sendMessage(message);
		}
	}
	
	public ArrayList<Material> getRemoveableMaterials(){
		return arena.getRemoveableMaterials();
	}
	
	public String getArenaName(){
		return arena.getName();
	}
	
	public boolean isRunning(){
		return running;
	}
}