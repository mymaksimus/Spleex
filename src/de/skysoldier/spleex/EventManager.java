package de.skysoldier.spleex;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventManager implements Listener {

	Main plugin;
	
	public EventManager(Main plugin){
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	void onPlayerJoin(PlayerJoinEvent e){
		plugin.addOnlinePlayer(e.getPlayer());
		e.setJoinMessage("");
	}
	
	@EventHandler
	void onPlayerLeave(PlayerQuitEvent e){
		if(plugin.getOnlinePlayer(e.getPlayer()).isIngame()){
			plugin.getOnlinePlayer(e.getPlayer()).getGame().removePlayer(plugin.getOnlinePlayer(e.getPlayer()), "left the game");
		}
		plugin.removeOnlinePlayer(e.getPlayer());
	}
	
	@EventHandler
	void onBlockBreak(BlockBreakEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	void onPlayerInteract(PlayerInteractEvent e){
		if(plugin.getOnlinePlayer(e.getPlayer()).isIngame()){
			if(plugin.getOnlinePlayer(e.getPlayer()).getGame().isRunning()){
				if(e.getClickedBlock() != null){
					if(plugin.getOnlinePlayer(e.getPlayer()).getGame().getRemoveableMaterials().contains(e.getClickedBlock().getType())){
						plugin.getOnlinePlayer(e.getPlayer()).getGame().removeBlock(e.getPlayer(), e.getClickedBlock());
					}
				}
			}
		}
	}
	
	@EventHandler
	void onEntityDamage(EntityDamageEvent e){
		if(e.getEntity() instanceof Player){
			e.setCancelled(true);
			OnlinePlayer player = plugin.getOnlinePlayer((Player) e.getEntity()); 
			if(player.isIngame()){
				player.getGame().removePlayer(player, "died");
			}
			player.getPlayer().teleport(Main.WORLD_SPAWN);
		}
	}
	
	@EventHandler
	void onPlayerFoodChange(FoodLevelChangeEvent e){
		e.setCancelled(true);
	}
}
