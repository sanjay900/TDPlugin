package com.sanjay900.TD;


import java.util.Iterator;
import java.util.Map.Entry;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.ai.TargetType;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.minecraft.server.v1_7_R4.PacketPlayOutAnimation;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftZombie;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class customZombie {
	public TDTeam orig;
	TDTeam dest;
	private int lane;
	private Location spawnLocation;
	private long countDown = 0;
	NPC npc;
	private boolean hasReachedMidpoint;
	private Location midpoint;
	private BukkitTask task;
	private TDTower targetTower = null;
	public customZombie(String name, TDTeam orig, TDTeam dest, int lane, Location midpoint, Location spawnlocation) {
		this.spawnLocation = spawnlocation;
		this.orig = orig;
		this.dest = dest;
		this.lane = lane;
		this.midpoint = midpoint;
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		npc = registry.createNPC(EntityType.ZOMBIE, name);
		npc.spawn(spawnlocation);
		npc.data().setPersistent(NPC.NAMEPLATE_VISIBLE_METADATA, true);
		npc.getNavigator().getDefaultParameters().speedModifier(1f);
		npc.getNavigator().getLocalParameters().range(100);
		npc.getNavigator().setTarget(midpoint);
		task = Bukkit.getScheduler().runTaskTimer(TDPlugin.plugin, new NPCLogic(), 1l ,1l);
	}

	public void die() {
		task.cancel();
		TDPlugin plugin = (TDPlugin) Bukkit.getPluginManager().getPlugin("TDPlugin");

		if (plugin.playersToTower.containsValue((LivingEntity)npc.getEntity())) {
			TDTower tower = null;

			for (Entry<TDTower, LivingEntity> entry : plugin.playersToTower
					.entrySet()) {
				if (((LivingEntity)npc.getEntity()).equals(entry.getValue())) {
					tower = entry.getKey();
				}
			}

			if (plugin.playersToTower.get(tower) == (LivingEntity)npc.getEntity()) {
				plugin.playersToTower.remove(tower);
				tower.isShooting = false;
				for (Entity entity : ((LivingEntity)npc.getEntity()).getNearbyEntities(10, 10, 10)) {
					TDTower tower2 = null;
					if (entity.getType() == EntityType.ZOMBIE && entity instanceof customZombie) {
						tower2 = TDUtil
								.getClosestTower(entity.getLocation(),((customZombie)entity).orig);

					}
					else if (entity.getType() != EntityType.PLAYER){
						return;
					} else {
						for (TDGame game : plugin.getTDConfig().games) {
							for (TDTeam teamc : game.teams) {
								Player p = (Player)entity;
								if (teamc.players.contains(p.getName())) {
									tower2 = TDUtil
											.getClosestTower(entity.getLocation(),teamc);
								}
							}
						}	
					}
					if (tower2 == null) return;




					if (!plugin.playersToTower.containsKey(tower2)) {

						tower2.isShooting = true;
						plugin.playersToTower.put(tower2, (LivingEntity)entity);
						break;
					}

				}
			}
		}}


	public void setHealth(float f) {
		((Damageable)npc.getEntity()).damage(f);

	}
	public void swingArm() {
		PacketPlayOutAnimation anim = new PacketPlayOutAnimation(
				((CraftZombie)npc.getEntity()).getHandle(), 0);
		for (Player p : this.spawnLocation.getWorld().getPlayers()) {
			((CraftPlayer)p).getHandle().playerConnection.sendPacket(anim);
		}
	}
	private class NPCLogic implements Runnable {

		@Override
		public void run() {
			if (npc.getEntity().isDead()) {
				npc.destroy();
				die();
			}
			if (npc.getNavigator().isPaused()) {
				if (!(npc.getNavigator().getTargetType() == TargetType.LOCATION)) {
					//i got to a mob
					if (!hasReachedMidpoint)
						npc.getNavigator().setTarget(midpoint);
					else {
						if (dest.towers.isEmpty()) {
							npc.getNavigator().setTarget(dest.treeLoc);
						} else
							npc.getNavigator().setTarget(targetTower.attackLocation);
					}


				} else if (!hasReachedMidpoint){
					//i havent gotten to the middle yet
					if (TDUtil.compareLocation(midpoint, npc.getNavigator().getTargetAsLocation())) {
						hasReachedMidpoint = true;
						targetTower = null;
						for (TDTower t: dest.towers.get(lane)) {
							if (!t.destroyed) {
								npc.getNavigator().setTarget(t.attackLocation);
								targetTower = t;
							}
						}
						if (targetTower == null) {

						}
					} 
				} else {
					if (!dest.towers.isEmpty()) {
						if (countDown <= 0) {
							swingArm();
							targetTower.setHealth(targetTower.health - 10);
							if (targetTower.health <= 0) {
								targetTower.collapse();
								dest.towers.get(lane).remove(targetTower);
							}
							targetTower = null;
							if (dest.towers.isEmpty()) {
								npc.getNavigator().setTarget(dest.treeLoc);
								targetTower = null;
							} else {
								for (TDTower t: dest.towers.get(lane)) {
									if (!t.destroyed) {
										npc.getNavigator().setTarget(t.attackLocation);
										targetTower = t;
									}
								}
							}

						}

					} else {
						if (countDown <= 0) {
							swingArm();
							dest.setHealth(dest.health - 10);
							if (dest.health <= 0) {
								 TDPlugin plugin = (TDPlugin)
						                  Bukkit.getPluginManager().getPlugin(
						                  "TDPlugin");
						                
						                Iterator<Player> localIterator2 = spawnLocation.getWorld().getPlayers().iterator();
						                while (localIterator2.hasNext())
						                {
						                  Player p = localIterator2.next();
						                  p.sendMessage("The Team " + dest.teamColour + 
						                    dest.teamname + " has fallen!");
						                }
						                for (customZombie z : dest.zombies) {
						                  z.setHealth(0.0F);
						                }
						                int deadTeams = 0;
						                TDTeam lastAliveTeam = dest;
						                for (TDTeam t : plugin.getGame(dest).teams) {
						                  if (t.health < 1) {
						                    deadTeams++;
						                  } else {
						                    lastAliveTeam = t;
						                  }
						                }
						                if (deadTeams >= plugin.getGame(dest).teams.length - 1)
						                {
						                  plugin.getGame(dest).stopgame();
						                  
						                  Iterator<Player> localIterator3 = spawnLocation.getWorld().getPlayers().iterator();
						                  while (localIterator3.hasNext())
						                  {
						                    Player p = localIterator3.next();
						                    p.sendMessage("The Team " + 
						                      lastAliveTeam.teamColour + 
						                      lastAliveTeam.teamname + 
						                      " is victorious!");
						                  }
						                }
							}
						}
					}
				}
			}
		}
	}
}

