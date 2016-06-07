/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sanjay900.TD;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.UUID;

import net.minecraft.server.v1_7_R4.EntityArrow;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.Hologram;
import com.sanjay900.TD.arrow.ArrowHitBlockEvent;


/**
 * 
 * @author Sanjay
 */
public class EventListener implements Listener {
	TDPlugin plugin;
	private ArrayList<UUID> deadPlayers = new ArrayList<>();;

	public EventListener(final TDPlugin plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		plugin.getServer().getScheduler()
				.scheduleSyncRepeatingTask(plugin, new Runnable() {
					@Override
					public void run() {
						if (!plugin.playersToTower.isEmpty()) {
							for (int i = 0; i < plugin.playersToTower.size(); i++) {
								TDTower tower = (TDTower) plugin.playersToTower
										.keySet().toArray()[i];
								tower.shootAtLocation(plugin.playersToTower
										.get(tower).getLocation());
							}
						}

					}
				}, 0L, 5L);

	}
	@EventHandler
    private void onProjectileHit(final ProjectileHitEvent e) {
        if (e.getEntityType() == EntityType.ARROW) {
            // Must be run in a delayed task otherwise it won't be able to find the block
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                public void run() {
                    try {
                        EntityArrow entityArrow = ((CraftArrow) e
                                .getEntity()).getHandle();

                        Field fieldX = EntityArrow.class
                                .getDeclaredField("d");
                        Field fieldY = EntityArrow.class
                                .getDeclaredField("e");
                        Field fieldZ = EntityArrow.class
                                .getDeclaredField("f");

                        fieldX.setAccessible(true);
                        fieldY.setAccessible(true);
                        fieldZ.setAccessible(true);

                        int x = fieldX.getInt(entityArrow);
                        int y = fieldY.getInt(entityArrow);
                        int z = fieldZ.getInt(entityArrow);

                        if (isValidBlock(x, y, z)) {
                            Block block = e.getEntity().getWorld().getBlockAt(x, y, z);
                            Bukkit.getServer()
                                    .getPluginManager()
                                    .callEvent(
                                            new ArrowHitBlockEvent((Arrow) e
                                                    .getEntity(), block));
                        }

                    } catch (NoSuchFieldException e1) {
                        e1.printStackTrace();
                    } catch (SecurityException e1) {
                        e1.printStackTrace();
                    } catch (IllegalArgumentException e1) {
                        e1.printStackTrace();
                    } catch (IllegalAccessException e1) {
                        e1.printStackTrace();
                    }
                }
            });

        }
    }

    // If the arrow hits a mob or player the coords will be -1
    private boolean isValidBlock(int x, int y, int z) {
        return x != -1 && y != -1 && z != -1;
    }
	@EventHandler
	public void ProjectileHit(final ProjectileHitEvent event) {
		if (event.getEntity().getLocation().getWorld().getName().equals("MOBA")) {
			plugin.getServer().getScheduler()
					.scheduleSyncDelayedTask(plugin, new Runnable() {
						@Override
						public void run() {
							event.getEntity().remove();
						}
					}, 5L);

		}
	}

	@EventHandler
	public void playerLeave(final PlayerQuitEvent event) {
		if (event.getPlayer().getWorld().getName().equals("MOBA")) {
			for (TDGame game : plugin.getTDConfig().games) {
				for (TDTeam teamc : game.teams) {
					Player p = event.getPlayer();
					if (teamc.players.contains(p.getName())) {
						teamc.players.remove(p.getName());
						if (teamc.players.isEmpty()) {
							teamc.setHealth(0);

							TDPlugin plugin = (TDPlugin) Bukkit
									.getPluginManager().getPlugin("TDPlugin");
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
				}
			}
			if (plugin.playersToTower.containsValue(event.getPlayer())) {
				TDTower tower = null;
				for (Entry<TDTower, LivingEntity> entry : plugin.playersToTower
						.entrySet()) {
					if (event.getPlayer().equals(entry.getValue())) {
						tower = entry.getKey();
					}
				}

				if (plugin.playersToTower.get(tower) == event.getPlayer()) {
					plugin.playersToTower.remove(tower);
					tower.isShooting = false;
					for (Entity entity : event.getPlayer().getNearbyEntities(
							10, 10, 10)) {

						TDTower tower2 = null;
						if (entity.getType() == EntityType.ZOMBIE
								&& entity instanceof customZombie) {
							tower2 = TDUtil.getClosestTower(
									entity.getLocation(),
									((customZombie) entity).orig);
						} else if (entity.getType() != EntityType.PLAYER) {
							return;
						} else {
							for (TDGame game : plugin.getTDConfig().games) {
								for (TDTeam teamc : game.teams) {
									Player p = (Player) entity;
									if (teamc.players.contains(p.getName())) {
										tower2 = TDUtil.getClosestTower(
												entity.getLocation(), teamc);
									}
								}
							}
						}
						if (tower2 == null)
							return;

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

	}

	@EventHandler
	public void WorldTeleport(final PlayerChangedWorldEvent event) {
		if (event.getFrom().getName().equals("MOBA")) {
			if (plugin.playersToTower.containsValue(event.getPlayer())) {
				TDTower tower = null;
				for (Entry<TDTower, LivingEntity> entry : plugin.playersToTower
						.entrySet()) {
					if (event.getPlayer().equals(entry.getValue())) {
						tower = entry.getKey();
					}
				}

				if (plugin.playersToTower.get(tower) == event.getPlayer()) {
					plugin.playersToTower.remove(tower);
					tower.isShooting = false;
					for (Entity entity : event.getPlayer().getNearbyEntities(
							10, 10, 10)) {

						TDTower tower2 = null;
						if (entity.getType() == EntityType.ZOMBIE
								&& entity instanceof customZombie) {
							tower2 = TDUtil.getClosestTower(
									entity.getLocation(),
									((customZombie) entity).orig);
						} else if (entity.getType() != EntityType.PLAYER) {
							return;
						} else {
							for (TDGame game : plugin.getTDConfig().games) {
								for (TDTeam teamc : game.teams) {
									Player p = (Player) entity;
									if (teamc.players.contains(p.getName())) {
										tower2 = TDUtil.getClosestTower(
												entity.getLocation(), teamc);
									}
								}
							}
						}
						if (tower2 == null)
							return;

						tower2.isShooting = true;
						if (!plugin.playersToTower.containsKey(tower2)) {
							plugin.playersToTower.put(tower2,
									(LivingEntity) entity);
							break;
						}

					}
				}
			}
			if (event.getPlayer().getWorld().getName().equals("MOBA")
					&& !deadPlayers.contains(event.getPlayer().getUniqueId())) {

			} else {
				for (TDGame game : plugin.getTDConfig().games) {
					for (TDTeam teamc : game.teams) {
						Player p = event.getPlayer();
						if (teamc.players.contains(p.getName())) {
							teamc.players.remove(p.getName());
							event.getPlayer().sendMessage(
									"You have left " + game.name);
							if (teamc.players.isEmpty()) {
								teamc.setHealth(0);
								TDPlugin plugin = (TDPlugin) Bukkit
										.getPluginManager().getPlugin(
												"TDPlugin");
								for (Player p2 : teamc.spawnlocation.getWorld()
										.getPlayers()) {
									p2.sendMessage("The Team "
											+ teamc.teamColour + teamc.teamname
											+ " has fallen!");
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
									for (Player p2 : teamc.spawnlocation
											.getWorld().getPlayers()) {
										p2.sendMessage("The Team "
												+ lastAliveTeam.teamColour
												+ lastAliveTeam.teamname
												+ " is victorious!");
									}
								}
							}
						}
					}
				}
			}

		}
	}

	@EventHandler
	public void PlayerDeath(final PlayerDeathEvent event) {
		if (event.getEntity().getWorld().getName().equals("MOBA")) {
			// if (plugin.getTDConfig().games.get(0).running) {
			deadPlayers.add(event.getEntity().getUniqueId());
			if (plugin.playersToTower.containsValue(event.getEntity())) {
				TDTower tower = null;
				for (Entry<TDTower, LivingEntity> entry : plugin.playersToTower
						.entrySet()) {
					if (event.getEntity().equals(entry.getValue())) {
						tower = entry.getKey();
					}
				}
				if (tower == null)
					return;
				if (plugin.playersToTower.get(tower) == event.getEntity()) {
					plugin.playersToTower.remove(tower);
					tower.isShooting = false;
					for (Entity entity : event.getEntity().getNearbyEntities(
							10, 10, 10)) {

						TDTower tower2 = null;
						if (entity.getType() == EntityType.ZOMBIE
								&& entity instanceof customZombie) {
							tower2 = TDUtil.getClosestTower(
									entity.getLocation(),
									((customZombie) entity).orig);
						} else if (entity.getType() != EntityType.PLAYER) {
							return;
						} else {
							for (TDGame game : plugin.getTDConfig().games) {
								for (TDTeam teamc : game.teams) {
									Player p = (Player) entity;
									if (teamc.players.contains(p.getName())) {

										tower2 = TDUtil.getClosestTower(
												entity.getLocation(), teamc);
									}
								}
							}
						}
						if (tower2 == null)
							return;

						tower2.isShooting = true;
						if (!plugin.playersToTower.containsKey(tower2)) {
							plugin.playersToTower.put(tower2,
									(LivingEntity) entity);
							return;
						}

					}
				}
			}
		}
	}

	@EventHandler
	public void respawn(final PlayerRespawnEvent event) {
		for (TDGame game : plugin.getTDConfig().games) {
			for (final TDTeam teamc : game.teams) {

				if (teamc.players.contains(event.getPlayer().getName())) {
					plugin.getServer().getScheduler()
							.scheduleSyncDelayedTask(plugin, new Runnable() {
								@Override
								public void run() {
									if (deadPlayers.contains(event.getPlayer().getUniqueId()))
										deadPlayers.remove(event.getPlayer().getUniqueId());
									event.getPlayer().teleport(
											teamc.spawnlocation);

									event.getPlayer()
											.getInventory()
											.addItem(
													new ItemStack(
															Material.DIAMOND_SWORD,
															1));
									event.getPlayer()
											.getInventory()
											.setBoots(
													new ItemStack(
															Material.DIAMOND_BOOTS,
															1));
									event.getPlayer()
											.getInventory()
											.setChestplate(
													new ItemStack(
															Material.IRON_CHESTPLATE,
															1));
									event.getPlayer()
											.getInventory()
											.setHelmet(
													new ItemStack(
															Material.DIAMOND_HELMET,
															1));
									event.getPlayer()
											.getInventory()
											.setLeggings(
													new ItemStack(
															Material.IRON_LEGGINGS,
															1));
								}
							}, 2L);

				}
			}
		}

	}

	@EventHandler
	public void entityDamage(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Monster) {
			Player player = (Player) event.getDamager();
			Entity damaged = event.getEntity();
			boolean hasTeam = false;
			TDTeam playerTeam = null;
			for (TDGame game : plugin.getTDConfig().games) {
				if (!hasTeam) {
					for (TDTeam t : game.teams) {
						if (t.players.contains(player.getName())) {
							playerTeam = t;
							hasTeam = true;
							break;
						}
					}
				}
			}
			if (!hasTeam)
				return;
			if (damaged instanceof customZombie) {
				customZombie cz = (customZombie) damaged;
				if (cz.orig == playerTeam) return;
			}
			if (damaged instanceof customSkeleton) {
				customSkeleton cz = (customSkeleton) damaged;
				if (cz.orig == playerTeam) return;
			}
		}
		if (event.getEntity() instanceof Player
				&& event.getDamager() instanceof Player) {
			Player player = (Player) event.getEntity();
			Player damager = (Player) event.getDamager();
			boolean hasTeam = false;
			TDTeam playerTeam = null;
			for (TDGame game : plugin.getTDConfig().games) {
				if (!hasTeam) {
					for (TDTeam t : game.teams) {
						if (t.players.contains(player.getName())) {
							playerTeam = t;
							hasTeam = true;
							break;
						}
					}
				}
			}
			if (!hasTeam)
				return;
			hasTeam = false;
			for (TDGame game : plugin.getTDConfig().games) {
				if (!hasTeam) {
					for (TDTeam t : game.teams) {
						if (t.players.contains(damager.getName())) {
							if (playerTeam == t) {
								event.setCancelled(true);
							}
							hasTeam = true;
							break;
						}
					}
				}
			}

		}
	}

	@EventHandler
	public void PlayerMove(final PlayerMoveEvent event) {
		
			for (Hologram h : HoloAPI.getManager().getHologramsFor(plugin)) {
				

					if (event.getTo().distance(h.getDefaultLocation()) < 10) {
						
						if (!h.getPlayerViews().containsKey(event.getPlayer().getUniqueId().toString()))
						h.show(event.getPlayer());
					} else {
					h.clear(event.getPlayer());
					
					}
					
					
			}
			if (event.getPlayer().getWorld().getName().equals("MOBA")) {
			// if (plugin.getTDConfig().games.get(0).running) {
			TDTeam team = null;
			boolean hasTeam = false;
			for (TDGame game : plugin.getTDConfig().games) {
				if (!hasTeam) {
					for (TDTeam t : game.teams) {
						if (t.players.contains(event.getPlayer().getName())) {
							team = t;
							hasTeam = true;
							break;
						}
					}
				}
			}
			if (!hasTeam)
				return;
			if (TDUtil.isTowerClose(event.getTo(), team)) {
				TDTower tower = TDUtil.getClosestTower(event.getTo(), team);
				tower.isShooting = true;
				if (!plugin.playersToTower.containsKey(tower)) {
					plugin.playersToTower.put(tower, event.getPlayer());
				}

			} else {
				if (plugin.playersToTower.containsValue(event.getPlayer())) {
					TDTower tower = null;
					for (Entry<TDTower, LivingEntity> entry : plugin.playersToTower
							.entrySet()) {
						if (event.getPlayer().equals(entry.getValue())) {
							tower = entry.getKey();
						}
					}

					if (plugin.playersToTower.get(tower) == event.getPlayer()) {
						plugin.playersToTower.remove(tower);
						tower.isShooting = false;
						for (Entity entity : event.getPlayer()
								.getNearbyEntities(10, 10, 10)) {

							TDTower tower2 = null;
							if (entity.getType() == EntityType.ZOMBIE
									&& entity instanceof customZombie) {
								tower2 = TDUtil.getClosestTower(
										entity.getLocation(),
										((customZombie) entity).orig);
							} else if (entity.getType() != EntityType.PLAYER) {
								return;
							} else {
								for (TDGame game : plugin.getTDConfig().games) {
									for (TDTeam teamc : game.teams) {
										Player p = (Player) entity;
										if (teamc.players.contains(p.getName())) {
											tower2 = TDUtil
													.getClosestTower(entity
															.getLocation(),
															teamc);
										}
									}
								}
							}
							if (tower2 == null)
								return;

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
		}
	}

	// }
	@EventHandler
	public void arrowshoot(EntityShootBowEvent event) {
		if (event.getEntityType() == EntityType.PLAYER) {
			TDTeam team = null;
			boolean hasTeam = false;
			for (TDGame game : plugin.getTDConfig().games) {
				if (!hasTeam) {
					for (TDTeam t : game.teams) {
						if (t.players.contains(((Player) event.getEntity())
								.getName())) {
							team = t;
							hasTeam = true;
							break;
						}
					}
				}
			}
			if (hasTeam) {
				event.getProjectile().setMetadata("Team",
						new FixedMetadataValue(plugin, team));
				;
			}

		}
	}

	@EventHandler
	public void onEntityDeath(PlayerDeathEvent event) {
		if (event.getEntityType() == EntityType.PLAYER) {
			if (event.getEntity().getType() == EntityType.PLAYER) {
				Player p = (Player) event.getEntity();
				if (p.isDead()) {

					if (p.getKiller().hasMetadata("Team")) {
						TDTeam team = (TDTeam) p.getKiller()
								.getMetadata("Team").get(0).value();
						event.setDeathMessage(p.getDisplayName()
								+ " was killed by " + p.getKiller().getType()
								+ " on team " + team.teamColour + team.teamname);

					}
				}
			}

		}
	}

	@EventHandler
	public void ArrowHit(ArrowHitBlockEvent event) {
		if (event.getBlock().getLocation().getWorld().getName().equals("MOBA")) {
			if (event.getArrow().hasMetadata("Team")) {
				TDTeam team = (TDTeam) event.getArrow().getMetadata("Team")
						.get(0).value();

				Location loc = event.getBlock().getLocation();
				if (TDUtil.isTowerClose(event.getBlock().getLocation(), team)) {
					ArrayList<TDTower> towers = TDUtil.getTowers(event
							.getBlock().getLocation(), team);
					boolean isTower = false;
					for (TDTower tower : towers) {
						if (!tower.excludedMaterials.contains(event.getBlock()
								.getType())) {
							for (int x = tower.referencePoint.getBlockX(); x > tower.referencePoint
									.getBlockX() - tower.xSize - 1; x--) {
								for (int y = tower.referencePoint.getBlockY()
										+ tower.height; y > tower.referencePoint
										.getBlockY() - 1; y--) {
									for (int z = tower.referencePoint
											.getBlockZ(); z < tower.referencePoint
											.getBlockZ() + tower.zSize + 1; z++) {

										if (loc.getBlockX() == x
												&& loc.getBlockY() == y
												&& loc.getBlockZ() == z) {

											isTower = true;

										}
									}
								}
							}
							if (isTower) {

								tower.setHealth(tower.health - 20);
								if (tower.health < 1) {
									tower.collapse();
									TDTeam orig = TDUtil.getTeamFromTower(
											tower, team);
									orig.towers
											.get(TDUtil.getPathFromTower(tower,
													team)).remove(tower);
								}
							}

						}
					}
				} else {
					TDTeam team2 = null;

					for (TDTeam t : plugin.getGame(team).teams) {
						if (t != team) {
							if (loc.distance(t.calcLoc) < t.distance) {
								team2 = t;
							}
						}
					}
					if (team2 != null) {
						Boolean isSpawn = false;
						if (!team2.excludedMaterials.contains(event.getBlock()
								.getType())) {
							for (int x = team2.calcLoc.getBlockX(); x > team2.calcLoc
									.getBlockX() - team2.xSize - 1; x--) {
								for (int y = team2.calcLoc.getBlockY()
										+ team2.height; y > team2.calcLoc
										.getBlockY() - 1; y--) {
									for (int z = team2.calcLoc.getBlockZ(); z < team2.calcLoc
											.getBlockZ() + team2.zSize + 1; z++) {

										if (loc.getBlockX() == x
												&& loc.getBlockY() == y
												&& loc.getBlockZ() == z) {

											isSpawn = true;

										}
									}
								}
							}
							if (isSpawn) {

								team2.setHealth(team2.health - 20);
								if (team2.health < 1) {
									TDPlugin plugin = (TDPlugin) Bukkit
											.getPluginManager().getPlugin(
													"TDPlugin");
									for (Player p : loc.getWorld().getPlayers()) {
										p.sendMessage("The Team "
												+ team2.teamColour
												+ team2.teamname
												+ " has fallen!");
									}
									for (customZombie z : team2.zombies) {
										z.setHealth(0);
									}

									int deadTeams = 0;
									TDTeam lastAliveTeam = team2;
									for (TDTeam t : plugin.getGame(team2).teams) {
										if (t.health < 1)
											deadTeams++;
										else
											lastAliveTeam = t;
									}

									if (deadTeams >= plugin.getGame(team2).teams.length - 1) {

										plugin.getGame(team2).stopgame();
										for (Player p : loc.getWorld()
												.getPlayers()) {
											p.sendMessage("The Team "
													+ lastAliveTeam.teamColour
													+ lastAliveTeam.teamname
													+ " is victorious!");
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

	@EventHandler
	public void BlockBreak(PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_BLOCK
				&& event.getPlayer().getWorld().getName().equals("MOBA")) {
			TDTeam team = null;
			boolean hasTeam = false;
			for (TDGame game : plugin.getTDConfig().games) {
				if (!hasTeam) {
					for (TDTeam t : game.teams) {
						if (t.players.contains(event.getPlayer().getName())) {
							team = t;
							hasTeam = true;
							break;
						}
					}
				}
			}
			if (!hasTeam)
				return;
			Location loc = event.getClickedBlock().getLocation();
			if (TDUtil
					.isTowerClose(event.getClickedBlock().getLocation(), team)) {
				ArrayList<TDTower> towers = TDUtil.getTowers(event
						.getClickedBlock().getLocation(), team);
				boolean isTower = false;
				for (TDTower tower : towers) {
					if (!tower.excludedMaterials.contains(event
							.getClickedBlock().getType())) {
						for (int x = tower.referencePoint.getBlockX(); x > tower.referencePoint
								.getBlockX() - tower.xSize - 1; x--) {
							for (int y = tower.referencePoint.getBlockY()
									+ tower.height; y > tower.referencePoint
									.getBlockY() - 1; y--) {
								for (int z = tower.referencePoint.getBlockZ(); z < tower.referencePoint
										.getBlockZ() + tower.zSize + 1; z++) {

									if (loc.getBlockX() == x
											&& loc.getBlockY() == y
											&& loc.getBlockZ() == z) {

										isTower = true;

									}
								}
							}
						}
						if (isTower) {

							tower.setHealth(tower.health - 20);
							if (tower.health < 1) {
								tower.collapse();
								TDTeam orig = TDUtil.getTeamFromTower(tower,
										team);
								orig.towers.get(
										TDUtil.getPathFromTower(tower, team))
										.remove(tower);
							}
						}

					}
				}
			} else {
				TDTeam team2 = null;

				for (TDTeam t : plugin.getGame(team).teams) {
					if (t != team) {
						if (loc.distance(t.calcLoc) < t.distance) {
							team2 = t;
						}
					}
				}
				if (team2 != null) {
					Boolean isSpawn = false;
					if (!team2.excludedMaterials.contains(event
							.getClickedBlock().getType())) {
						for (int x = team2.calcLoc.getBlockX(); x > team2.calcLoc
								.getBlockX() - team2.xSize - 1; x--) {
							for (int y = team2.calcLoc.getBlockY()
									+ team2.height; y > team2.calcLoc
									.getBlockY() - 1; y--) {
								for (int z = team2.calcLoc.getBlockZ(); z < team2.calcLoc
										.getBlockZ() + team2.zSize + 1; z++) {

									if (loc.getBlockX() == x
											&& loc.getBlockY() == y
											&& loc.getBlockZ() == z) {

										isSpawn = true;

									}
								}
							}
						}
						if (isSpawn) {

							team2.setHealth(team2.health - 20);
							if (team2.health < 1) {
								TDPlugin plugin = (TDPlugin) Bukkit
										.getPluginManager().getPlugin(
												"TDPlugin");
								for (Player p : loc.getWorld().getPlayers()) {
									p.sendMessage("The Team "
											+ team2.teamColour + team2.teamname
											+ " has fallen!");
								}
								for (customZombie z : team2.zombies) {
									z.setHealth(0);
								}

								int deadTeams = 0;
								TDTeam lastAliveTeam = team2;
								for (TDTeam t : plugin.getGame(team2).teams) {
									if (t.health < 1)
										deadTeams++;
									else
										lastAliveTeam = t;
								}

								if (deadTeams >= plugin.getGame(team2).teams.length - 1) {

									plugin.getGame(team2).stopgame();
									for (Player p : loc.getWorld().getPlayers()) {
										p.sendMessage("The Team "
												+ lastAliveTeam.teamColour
												+ lastAliveTeam.teamname
												+ " is victorious!");
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
