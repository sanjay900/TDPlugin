/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sanjay900.TD;


import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author Sanjay
 */
public class TDGame {
    String name = "";
    TDTeam[] teams = null;
    World world = null;
    Boolean running = false;
    BukkitTask task;
    TDGame(String gamename, World world, TDTeam[] teams) {
        this.name = gamename;
        this.teams = teams;
        this.world = world;
        this.running = false;
    }
   
    public void startgame() {
        this.running = true;
        for (int i = 0; i<teams.length;i++) {
            teams[i].startGame(world);
            
        }
        task = new TDSpawn(teams, this).runTaskLater(Bukkit.getPluginManager().getPlugin("TDPlugin"), 10); 
             
        
        
    }
    public void stopgame() {
        this.running = false;
        for (int i = 0; i<teams.length;i++) {
            teams[i].stopGame();
            teams[i].players.clear();
            teams[i].towers = teams[i].towers2;
            task.cancel();
        }
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mva reset " +this.world.getName());
    }
   
  
    
}


