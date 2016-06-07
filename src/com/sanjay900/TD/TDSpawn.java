/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sanjay900.TD;

/**
 *
 * @author Sanjay
 */
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
 
public class TDSpawn extends BukkitRunnable {
     private TDTeam[] teams;
    private TDGame games;
    public TDSpawn(TDTeam[] teams, TDGame games) {
        this.teams = teams;
        this.games = games;
    }
 
   
    public void run() {
        // What you want to schedule goes here
    	
                    

        if (games.running) {
        	for (final TDTeam team: teams) {
        		for (int i = 0; i < team.towers.size(); i++) {
        			final int i2 = i;
        			Bukkit.getServer().getScheduler()
					.scheduleSyncDelayedTask((TDPlugin) Bukkit.getPluginManager().getPlugin(
							"TDPlugin"), new Runnable() {
						@Override
						public void run() {
							for (int amount = 0; amount < 10; amount ++) {
							team.spawnZombie(i2);
							}
						}
					},2L*i);
        			
        	
        		}
        	}
         @SuppressWarnings("unused")
		BukkitTask task = new TDSpawn(teams, games).runTaskLater(Bukkit.getPluginManager().getPlugin("TDPlugin"), 1200); 
           
        }
        
    }
 
}
