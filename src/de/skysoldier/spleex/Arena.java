package de.skysoldier.spleex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class Arena implements ConfigurationSerializable {
	
	private Location spawnLocation;
	private ArrayList<Material> removeableMaterials;
	private String name;
	
	public Arena(Location spawnLocation, ArrayList<Material> removeableMaterials){
		this.spawnLocation = spawnLocation;
		this.removeableMaterials = removeableMaterials;
	}
	
	public Arena(String name, Map<String, Object> map){
		try{
			this.name = name;
			spawnLocation = new Location(Bukkit.getWorld((String) map.get("worldname")), (int) map.get("spawnX"), (int) map.get("spawnY") , (int) map.get("spawnZ"));
			String removeableMaterials[] = ((String) map.get("removeableMaterials")).split("\\|");
			this.removeableMaterials = new ArrayList<>();
			for(int i = 0; i < removeableMaterials.length; i++){
				this.removeableMaterials.add(Material.valueOf(removeableMaterials[i]));
			}
		}
		catch(Exception e){
			e.printStackTrace();
			throw new IllegalArgumentException("Wrong / broken map or invalid argument.");
		}
	}
	
	public Map<String, Object> serialize() {
		String removeableMaterials = "";
		for(Material m : this.removeableMaterials) removeableMaterials += m + "|";
		removeableMaterials = removeableMaterials.substring(0, removeableMaterials.length() - 1);
		HashMap<String, Object> obj = new HashMap<>();
		obj.put("worldname", spawnLocation.getWorld().getName());
		obj.put("spawnX", spawnLocation.getBlockX());
		obj.put("spawnY", spawnLocation.getBlockY());
		obj.put("spawnZ", spawnLocation.getBlockZ());
		obj.put("removeableMaterials", removeableMaterials);
		return obj;
	}
	
	public String getName(){
		return name;
	}
	
	public Location getSpawnLocation(){
		return spawnLocation;
	}
	
	public ArrayList<Material> getRemoveableMaterials(){
		return removeableMaterials;
	}
}
