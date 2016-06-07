/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sanjay900.TD;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * 
 * @author Sanjay
 */
public class TDConfig {
	public static String replaceColors(String message) {
		return message.replaceAll("(?i)&([a-fk-o0-9])", "§$1");
	}

	ArrayList<TDGame> games = new ArrayList<>();
	private final File file;
	private final FileConfiguration config;

	TDConfig(TDPlugin plugin) {
		file = new File(plugin.getDataFolder(), "config.yml");
		try {
			file.createNewFile();
		} catch (IOException ex) {
			Logger.getLogger(TDPlugin.class.getName()).log(Level.SEVERE, null,
					ex);
		}
		config = YamlConfiguration.loadConfiguration(file);

		ConfigurationSection gamessection = config
				.getConfigurationSection("games");
		Set<?> gameskey = gamessection.getKeys(false);
		for (int i = 0; i < gameskey.size(); i++) {

			String name = replaceColors(gameskey.toArray()[i].toString());
			String world = replaceColors(config.getString("games." + name
					+ ".world"));
			int maxhealth = config.getInt("games." + name + ".maxhealth");

			ConfigurationSection teamssection = config
					.getConfigurationSection("games." + name + ".teams");
			Set<?> setteams = teamssection.getKeys(false);
			TDTeam[] teams = new TDTeam[setteams.size()];
			for (int i2 = 0; i2 < setteams.size(); i2++) {
				String teamname = setteams.toArray()[i2].toString();
				ChatColor teamColour = ChatColor.getByChar(config
						.getString("games." + name + ".teams." + teamname
								+ ".colourcode"));
				String spawnlocation = replaceColors(config.getString("games."
						+ name + ".teams." + teamname + ".spawnlocation"));

				List<String> hpLocs = config.getStringList("games." + name
						+ ".teams." + teamname + ".hpLocations");
				String treeLocs[] = replaceColors(
						config.getString("games." + name + ".teams." + teamname
								+ ".treeLocation")).split(",");
				String spawnlocs[] = spawnlocation.split(",");
				String calcLocs[] = replaceColors(
						config.getString("games." + name + ".teams." + teamname
								+ ".calcLocation")).split(",");
				Location treeLoc = new Location(Bukkit.getWorld(world),
						Double.valueOf(treeLocs[0]),
						Double.valueOf(treeLocs[1]),
						Double.valueOf(treeLocs[2]));
				Location spawnLoc = new Location(Bukkit.getWorld(world),
						Double.valueOf(spawnlocs[0]),
						Double.valueOf(spawnlocs[1]),
						Double.valueOf(spawnlocs[2]));
				Location calcLoc = new Location(Bukkit.getWorld(world),
						Double.valueOf(calcLocs[0]),
						Double.valueOf(calcLocs[1]),
						Double.valueOf(calcLocs[2]));
				int maxTowerHealth = config.getInt("games." + name + ".teams."
						+ teamname + ".towerconfig.maxhealth");

				List<String> excludedMaterialsS = config.getStringList("games."
						+ name + ".teams." + teamname
						+ ".towerconfig.excludedMaterials");
				ArrayList<Material> excludedMaterials = new ArrayList<>();
				List<String> excludedMaterialsSs = config
						.getStringList("games." + name + ".teams." + teamname
								+ ".excludedMaterials");
				ArrayList<Material> excludedMaterialss = new ArrayList<>();
				int xs = config.getInt("games." + name + ".teams." + teamname
						+ ".x");
				int heights = config.getInt("games." + name + ".teams."
						+ teamname + ".height");
				int zs = config.getInt("games." + name + ".teams." + teamname
						+ ".z");
				int distances = config.getInt("games." + name + ".teams."
						+ teamname + ".distance");
				for (String material : excludedMaterialsS) {
					excludedMaterials.add(Material.valueOf(material
							.toUpperCase()));
				}
				for (String material : excludedMaterialsSs) {
					excludedMaterialss.add(Material.valueOf(material
							.toUpperCase()));
				}
				ArrayList<ArrayList<TDTower>> towerpaths = new ArrayList<>();
				ArrayList<Location> midpoints = new ArrayList<>();
				ConfigurationSection towerssection = config
						.getConfigurationSection("games." + name + ".teams."
								+ teamname + ".towerconfig.towers");
				Set<?> settowers = towerssection.getKeys(false);
				ConfigurationSection midpointssection = config
						.getConfigurationSection("games." + name + ".teams."
								+ teamname + ".towerconfig.towers");
				Set<?> setmidpoints = midpointssection.getKeys(false);
				for (int i5 = 0; i5 < setmidpoints.size(); i5++) {
				
					String[] midpointsa = config
							.getString("games." + name
									+ ".teams." + teamname
									+ ".towerconfig.midpoints."+setmidpoints.toArray()[i5].toString()).split(",");
					
					Location midpoint = new Location(Bukkit.getWorld(world),
							Double.valueOf(midpointsa[0]),
							Double.valueOf(midpointsa[1]),
							Double.valueOf(midpointsa[2]));
					midpoints.add(midpoint);
				}
				for (int i3 = 0; i3 < settowers.size(); i3++) {
					ArrayList<TDTower> towers = new ArrayList<>();
					String path = settowers.toArray()[i3].toString();
					ConfigurationSection pathsection = config
							.getConfigurationSection("games." + name
									+ ".teams." + teamname
									+ ".towerconfig.towers." + path);
					Set<?> setpaths = pathsection.getKeys(false);

					for (int i4 = 0; i4 < setpaths.size(); i4++) {
						String towerLocS = setpaths.toArray()[i4].toString();
						
						String attackLocationS = config.getString("games."
								+ name + ".teams." + teamname
								+ ".towerconfig.towers." + path + "."
								+ towerLocS + ".attackLocation");
						String[] attackLocationSA = attackLocationS.split(",");
						Location attackLocation = new Location(
								Bukkit.getWorld(world),
								Double.valueOf(attackLocationSA[0]),
								Double.valueOf(attackLocationSA[1]),
								Double.valueOf(attackLocationSA[2]));
						String[] towerLocSA = towerLocS.split(",");
						Location towerLoc = new Location(
								Bukkit.getWorld(world),
								Double.valueOf(towerLocSA[0]),
								Double.valueOf(towerLocSA[1]),
								Double.valueOf(towerLocSA[2]));
						int x = config.getInt("games." + name + ".teams."
								+ teamname + ".towerconfig.towers." + path
								+ "." + towerLocS + ".x");
						int height = config.getInt("games." + name + ".teams."
								+ teamname + ".towerconfig.towers." + path
								+ "." + towerLocS + ".height");
						int z = config.getInt("games." + name + ".teams."
								+ teamname + ".towerconfig.towers." + path
								+ "." + towerLocS + ".z");
						List<String> tower = config.getStringList("games."
								+ name + ".teams." + teamname
								+ ".towerconfig.towers." + path + "."
								+ towerLocS + ".dispenser");
						ArrayList<Location> dispenserLoc = new ArrayList<>();
						for (String dispenserLocS : tower) {
							String[] dispenserLocSA = dispenserLocS.split(",");
							dispenserLoc.add(new Location(Bukkit
									.getWorld(world), Double
									.valueOf(dispenserLocSA[0]), Double
									.valueOf(dispenserLocSA[1]), Double
									.valueOf(dispenserLocSA[2])));

						}
						if (config.contains("games." + name + ".teams."
								+ teamname + ".towerconfig.towers." + path
								+ "." + towerLocS + ".hpLocations")) {
							List<String> hpLocsTowerS = config
									.getStringList("games." + name + ".teams."
											+ teamname + ".towerconfig.towers."
											+ path + "." + towerLocS
											+ ".hpLocations");

							ArrayList<Location> hpLocsTower = new ArrayList<>();
							for (String hpLocTower : hpLocsTowerS) {
								String[] hpLocTowerSA = hpLocTower.split(",");
								hpLocsTower.add(new Location(Bukkit
										.getWorld(world), Double
										.valueOf(hpLocTowerSA[0]), Double
										.valueOf(hpLocTowerSA[1]), Double
										.valueOf(hpLocTowerSA[2])));
							}

							towers.add(new TDTower(maxTowerHealth, hpLocsTower,
									towerLoc, x, height, z, excludedMaterials,
									attackLocation));
						} else {
							towers.add(new TDTower(maxTowerHealth, towerLoc, x,
									height, z, excludedMaterials,
									attackLocation));
						}
					}
					towerpaths.add(towers);
				}

				teams[i2] = new TDTeam(teamname, teamColour, maxhealth,
						spawnLoc, hpLocs, towerpaths, treeLoc, xs, heights, zs,
						excludedMaterialss, calcLoc, distances, midpoints);

			}
			games.add(new TDGame(name, Bukkit.getWorld(world), teams));
		}

	}

}