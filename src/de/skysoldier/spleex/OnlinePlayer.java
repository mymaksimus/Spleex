package de.skysoldier.spleex;

import org.bukkit.entity.Player;

public class OnlinePlayer {
	
	private Player player;
	private Game game;
	private int blocksDestroyedInRound;
	
	public OnlinePlayer(Player player){
		this.player = player;
	}
	
	public void setGame(Game game){
		this.game = game;
	}
	
	public void increaseBlocksDestroyedInRound(){
		blocksDestroyedInRound++;
	}
	
	public void resetBlocksDestroyedInRound(){
		blocksDestroyedInRound = 0;
	}
	
	public int getBlocksDestroyedInRound(){
		return blocksDestroyedInRound;
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public Game getGame(){
		return game;
	}
	
	public boolean isIngame(){
		return game != null;
	}
}
