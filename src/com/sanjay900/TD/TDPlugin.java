/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sanjay900.TD;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;



/**s==
 *
 * @author Sanjay
 */

public final class TDPlugin extends JavaPlugin {
    public static TDPlugin plugin;
    private TDConfig config;
    public HashMap<TDTower, LivingEntity> playersToTower = new HashMap<>();
	/**
     *
     */
    @Override
    public void onEnable(){
    	
    PluginManager pm = this.getServer().getPluginManager();
    
    TDPlugin.plugin = (TDPlugin)pm.getPlugin("TDPlugin");
    @SuppressWarnings("unused")
	EventListener playerListener = new EventListener(this);
    this.saveDefaultConfig();
    setTDConfig(new TDConfig(this));
    }

  
    @Override
 public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
     if(cmd.getName().equalsIgnoreCase("moba"))
       { if (args.length == 2) {
    	   switch(args[0]) {
    	   case "join":
    		   for (TDTeam t : getTDConfig().games.get(0).teams) {
    			   
    			   if (t.teamname.equalsIgnoreCase(args[1])){
    				   
    				   if (t.players.size() < 12) {
    					   t.players.add(sender.getName());
    					   sender.sendMessage("Joining Team "+t.teamColour+t.teamname);
    					   
    				   } else {
    					   sender.sendMessage("There are too many players in the Team "+t.teamColour+t.teamname);
    				   }
    				  
    				  
    				   
    			   }
    			   
    		   }
    		   return true;
    		   
    	   }
       } else
    	 if (args.length == 1) {
           
       
    	   switch(args[0]) {
    	   case "join":
    		   
    			   
    		   sender.sendMessage("Invalid Arguments");
    		   return true;
    		   
    		   
    		   
    	   case "start":  
    		   if (sender instanceof Player ) {
    			   if (!((Player)sender).hasPermission("moba.start")) {
    				   sender.sendMessage("You do not haver permission to run this command");
    				   return true;
    			   }
    		   }
    		   if(!getTDConfig().games.get(0).running) {
    	         getTDConfig().games.get(0).startgame();
    	         
    	   } else sender.sendMessage("The game has already started!");
    		   return true;
    	   case "leave":
    		   if (sender instanceof Player ) {
    			   Player p = (Player)sender;
    			   if (plugin.playersToTower.containsValue(p)) {
    					TDTower tower = null;
    					for (Entry<TDTower, LivingEntity> entry : plugin.playersToTower
    							.entrySet()) {
    						if (p.equals(entry.getValue())) {
    							tower = entry.getKey();
    						}
    					}

    					if (plugin.playersToTower.get(tower) == p) {
    						plugin.playersToTower.remove(tower);
    						tower.isShooting = false;
    						for (Entity entity : p.getNearbyEntities(
    								10, 10, 10)) {

    							TDTower tower2 = null;
    							if (entity.getType() == EntityType.ZOMBIE
    									&& entity instanceof customZombie) {
    								tower2 = TDUtil.getClosestTower(
    										entity.getLocation(),
    										((customZombie) entity).orig);
    							} else if (entity.getType() != EntityType.PLAYER) {
    								return true;
    							} else {
    								for (TDGame game : plugin.getTDConfig().games) {
    									for (TDTeam teamc : game.teams) {
    										Player p2 = (Player) entity;
    										if (teamc.players.contains(p2.getName())) {
    											tower2 = TDUtil.getClosestTower(
    													entity.getLocation(), teamc);
    										}
    									}
    								}
    							}
    							if (tower2 != null){
    								

    							tower2.isShooting = true;
    							if (!plugin.playersToTower.containsKey(tower2)) {
    								plugin.playersToTower.put(tower2,
    										(LivingEntity) entity);
    								break;
    							}
    							}

    						}
    					}
    				}
    					for (TDGame game : plugin.getTDConfig().games) {
    						for (TDTeam teamc : game.teams) {
    							if (teamc.players.contains(p.getName())) {
    								teamc.players.remove(p.getName());
    								p.sendMessage("You have left "+ game.name);
    								p.performCommand("spawn");
    								if (game.running) {
    								if (teamc.players.isEmpty()) {
    									teamc.setHealth(0);
    									TDPlugin plugin = (TDPlugin) Bukkit
    											.getPluginManager().getPlugin(
    													"TDPlugin");
    									for (Player p2 : teamc.spawnlocation.getWorld()
    											.getPlayers()) {
    										p2.sendMessage("The Team " + teamc.teamColour
    												+ teamc.teamname + " has fallen!");
    									}
    									for (customZombie z : teamc.zombies) {
    										z.setHealth(0);
    									}

    									int deadTeams = 0;
    									TDTeam lastAliveTeam = teamc;
    									for (TDTeam t : plugin.getGame(teamc).teams) {
    										if (t.health < 1)
    											deadTeams++;
    										else
    											lastAliveTeam = t;
    									}

    									if (deadTeams >= plugin.getGame(teamc).teams.length - 1) {

    										plugin.getGame(teamc).stopgame();
    										for (Player p2 : teamc.spawnlocation.getWorld()
    												.getPlayers()) {
    											p2.sendMessage("The Team "
    													+ lastAliveTeam.teamColour
    													+ lastAliveTeam.teamname
    													+ " is victorious!");
    										}
    									}
    								}
    							}
    							} else {sender.sendMessage("You are not currently playing a game");}
    						}
    					}
    				

    	
    		}

    		   
    		  
    	  
                    return true;//Create and show the menu
    	   case "stop":
    		   if (sender instanceof Player ) {
    			   if (!((Player)sender).hasPermission("moba.start")) {
    				   sender.sendMessage("You do not haver permission to run this command");
    				   return true;
    			   }
    		   }
    		   if(getTDConfig().games.get(0).running) {
      	         getTDConfig().games.get(0).stopgame();
      	         }
    		   else sender.sendMessage("There is no game running...");
    	  
                    return true;//Create and show the menu
    	   case "reload":
    		   if (sender instanceof Player ) {
    			   if (!((Player)sender).hasPermission("moba.start")) {
    				   sender.sendMessage("You do not haver permission to run this command");
    				   return true;
    			   }
    		   }
    		   setTDConfig(new TDConfig(this));
    		   
    		   for (TDGame game : getTDConfig().games) {
    			   if (game==null) continue;  
    		   sender.sendMessage("    Name:"+game.name);
    		   sender.sendMessage("    Max Tower Health:"+String.valueOf(game.teams[0].maxhealth));
    		   sender.sendMessage("    World:"+getTDConfig().games.get(0).world.getName());
    		    for(TDTeam team: game.teams) {
    		    	sender.sendMessage("    Teams:");
    		    	sender.sendMessage("        Name:"+team.teamname);
    		    	sender.sendMessage("           Team colour:"+team.teamColour+"colour");
    		    	sender.sendMessage("           spawnlocation:"+team.spawnlocation);
    		    for (String loc : team.hpLocs) {
    		    	sender.sendMessage(loc);
    		    
    		    }
    		    }
    		  
    		   }
    		   }}
                    
    	   } else {
    		   sender.sendMessage("Invalid Arguments");
    		   
    	   }
       
       

      
       
        return false;

}
 
        

  
    /**
     *
     */
    @Override
    public void onDisable() {
    	for (TDGame game : config.games) {
    		 if(game.running) {
    			 game.stopgame();
      	         }
    	}
    	
    }


	public TDConfig getTDConfig() {
		return config;
	}


	public void setTDConfig(TDConfig config) {
		this.config = config;
	}
	public TDGame getGame(TDTeam team) {
		for (TDGame game : config.games) {
			for (TDTeam team2 : game.teams) {
				if (team2 == team) return game;
			}
		}
		return null;	
	}

  
}
