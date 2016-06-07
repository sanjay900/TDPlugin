/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sanjay900.TD;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.api.HologramFactory;

/**
 *
 * @author Sanjay
 */
//Create arraylist of towers from config, init them
public class TDTeam {
    int health = 0;
    Location spawnlocation = null;
    Location treeLoc;
    ArrayList<Hologram> hpBars = new ArrayList<Hologram>();
    List<String> hpLocs = new ArrayList<String>();
    int maxhealth = 0;
	ChatColor teamColour;
	TDGame game;
	ArrayList<ArrayList<TDTower>> towers;
	ArrayList<ArrayList<TDTower>> towers2;
	String teamname;
	World world;
	ArrayList<String> players = new ArrayList<String>();;
	ArrayList<customZombie> zombies = new ArrayList<customZombie>();
	Location calcLoc;
	int xSize;
	int zSize;
	int height;
	ArrayList<Material> excludedMaterials;
	public int distance;
	private ArrayList<Location> midpoints;
    TDTeam(String teamname, ChatColor teamColour, int maxhealth, Location spawnLoc,
			List<String> hpLocs, ArrayList<ArrayList<TDTower>> towers, Location treeLoc, int x, int height, int z, ArrayList<Material> excludedMaterials, Location calcLoc, int distance, ArrayList<Location> midpoints) {
        this.health = maxhealth;
        this.distance = distance;
        this.maxhealth = maxhealth;
        this.spawnlocation = spawnLoc;
        this.hpLocs = hpLocs;
        this.teamColour = teamColour;
        this.towers = towers;
        this.towers2 = towers;
        this.teamname = teamname;
        this.treeLoc = treeLoc;
    	this.calcLoc = calcLoc;
    	this.xSize = x;
    	this.height = height;
    	this.zSize = z;
    	this.excludedMaterials = excludedMaterials;
    	this.midpoints = midpoints;
	}
	public void startGame(World world) {
		this.towers = towers2;
		TDPlugin plugin = (TDPlugin) Bukkit.getPluginManager().getPlugin("TDPlugin");
		for (String hpLoc : hpLocs) {
			
			 String hpLocSplit[] = hpLoc.split(",");
			    Location hpLocation = new Location(world,Double.valueOf(hpLocSplit[0]),Double.valueOf(hpLocSplit[1]),Double.valueOf(hpLocSplit[2]));
			   
			    Hologram hologram = new HologramFactory(plugin)    // Replace "myPlugin" with your plugin instance
			    .withLocation(new Vector(hpLocation.getX(), hpLocation.getY(), hpLocation.getZ()), world.getName())
			    .withText("Starting Game")
			    .withText("Loading..")
			    .withSimplicity(true)
			    .withVisibility(new HoloVisible())
			    .build();
			    
	        hpBars.add(hologram);
	     
		}
        for (ArrayList<TDTower> tower : towers) {
        	for (TDTower to : tower){
        		to.startGame();	
        	}
        	
        }
        setHealth(maxhealth);
        spawnlocation.getWorld().loadChunk((spawnlocation.getBlock().getChunk()));
        
       
        this.world = world;
         //tower.startgame          			
        	for (String p : players ) {
        		Player pl = Bukkit.getPlayer(p);
        		pl.sendMessage(ChatColor.GOLD+"The Game "+plugin.getGame(this).name+" has "+ChatColor.GREEN+"started!");
        		pl.teleport(spawnlocation);
        		pl.getInventory().clear();
        		pl.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD, 1));
        		pl.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS, 1));
        		pl.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE, 1));
        		pl.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET, 1));
        		pl.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS, 1));
        	}
        
        

        
    }
    public void setHealth(int health) {
        this.health = health;
        int maxdisplayhealth =(maxhealth / (maxhealth / 10))+1;
        int displayhealth;
        if (!(health < 1)) {
			displayhealth = (health / (maxhealth / 10))+2;
		} else {
			displayhealth = 0;
		}
        String healthname = "HP:§c";
        if (displayhealth > maxdisplayhealth / 2) {
        	healthname = "HP:§2";
        }
        else if (displayhealth > maxdisplayhealth / 4) {
        	healthname = "HP:§e";
        }
        for (int i = 1; i < displayhealth; i++) {
            healthname = healthname+"❤";
        }
        
        healthname = healthname+"§7";
        for (int i = displayhealth; i < maxdisplayhealth; i++) {
            healthname = healthname+"❤";
        }
     
        for (Hologram h : hpBars) {
        	h.updateLine(0, healthname);
        	h.updateLine(1, String.valueOf(health)+" / "+String.valueOf(maxhealth));
        }
        
    }
    public void spawnZombie(int lane) {
    	 TDPlugin plugin = (TDPlugin) Bukkit.getPluginManager().getPlugin("TDPlugin");
         for (TDTeam t : plugin.getGame(this).teams) {
         	if (t != this) {
         		customZombie teamZombie = new customZombie(teamColour+teamname+ " Zombie", this, t, lane, midpoints.get(lane), spawnlocation);

                 zombies.add(teamZombie);
         	}
         }
         
     
    }
	public void stopGame() {
		TDPlugin plugin = (TDPlugin) Bukkit.getPluginManager().getPlugin("TDPlugin");
		for (String p : players) {
			Player pl = Bukkit.getPlayer(p);
			pl.sendMessage(ChatColor.GOLD+"The Game "+plugin.getGame(this).name+" has "+ChatColor.RED+"ended!");
		}
		 for (Hologram h : hpBars) {
			 HoloAPI.getManager().clearFromFile(h.getSaveId());
        	HoloAPI.getManager().stopTracking(h);
        }  
		 for (ArrayList<TDTower> towerl : towers) {
		 for (TDTower t: towerl) {
			 t.stopGame();
		 }}
		 for (customZombie z: zombies) {
			 z.npc.destroy();
		 }
	}
    
}
