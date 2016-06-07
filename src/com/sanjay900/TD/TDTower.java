package com.sanjay900.TD;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.FallingBlock;
import org.bukkit.material.Dispenser;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.api.HologramFactory;



public class TDTower {
	int health = 0;
	int maxHealth = 0;
	ArrayList<Location> hpLocations = new ArrayList<>();
	Location referencePoint; //The location the detection algorithm starts from (bottom NorthEast corner)
	int xSize = 0;
	int zSize = 0;
	int height = 0;
	Location attackLocation;
	ArrayList<Location> shoot = new ArrayList<>();// dispenser location
	List<Material> excludedMaterials = new ArrayList<>();
	boolean destroyed = false;
	boolean isShooting = false;
	private ArrayList<Hologram> hpBars = new ArrayList<>();
	public TDTower (int maxhealth, Location referencePoint, int x, int y, int z, ArrayList<Material> excludedMaterials, Location attackLocation)
	{
		this.attackLocation = attackLocation;
		this.maxHealth = maxhealth;
		this.referencePoint = referencePoint;
		
		if (z % 2 == 0) { //even
			hpLocations.add(
					new Location(referencePoint.getWorld(),
							referencePoint.getBlockX()+0.5,
							referencePoint.getBlockY(),
							referencePoint.getBlockZ() +(z/2))
							);
			
			hpLocations.add(
					new Location(referencePoint.getWorld(),
							referencePoint.getBlockX()+0.5-x+1,
							referencePoint.getBlockY(),
							referencePoint.getBlockZ() +(z/2))
							);
			
		} else { //odd
			hpLocations.add(
					new Location(referencePoint.getWorld(),
							referencePoint.getBlockX()+0.5,
							referencePoint.getBlockY(),
							referencePoint.getBlockZ() +(z/2))
							);
			hpLocations.add(
					new Location(referencePoint.getWorld(),
							referencePoint.getBlockX()+0.5-x,
							referencePoint.getBlockY(),
							referencePoint.getBlockZ() +(z/2))
							);
		}
		
		if (x % 2 == 0) { //even
			
			hpLocations.add(
					new Location(referencePoint.getWorld(),
							referencePoint.getBlockX() -(x/2)+1,
							referencePoint.getBlockY(),
							referencePoint.getBlockZ()+0.5)
							);
							
			hpLocations.add(
					new Location(referencePoint.getWorld(),
							referencePoint.getBlockX() -(x/2)+1,
							referencePoint.getBlockY(),
							referencePoint.getBlockZ()+0.5+z-1)
							);
			
		} else { //odd
			hpLocations.add(
					new Location(referencePoint.getWorld(),
							referencePoint.getBlockX() -(x/2),
							referencePoint.getBlockY(),
							referencePoint.getBlockZ()+0.5)
							);
			hpLocations.add(
					new Location(referencePoint.getWorld(),
							referencePoint.getBlockX() -(x/2),
							referencePoint.getBlockY(),
							referencePoint.getBlockZ()+0.5+z)
							);
		}
		
		this.xSize = x;
		this.zSize = z;
		this.height = y;
		
		for (int xv = referencePoint.getBlockX(); xv > referencePoint.getBlockX() - xSize -1; xv--) {
			for (int yv = referencePoint.getBlockY() + height; yv > referencePoint.getBlockY() -1; yv--) {
				for (int zv = referencePoint.getBlockZ(); zv < referencePoint.getBlockZ()+zSize+1; zv++) {
					Location loc = new Location(referencePoint.getWorld(),xv, yv, zv);
					if (loc.getBlock().getType() == Material.DISPENSER) {
							shoot.add(loc);
					}
				}
			}
		}
		this.excludedMaterials = excludedMaterials;
				
		
		
	}
	public TDTower (int maxhealth, ArrayList<Location> hpLocations, Location referencePoint, int x, int y, int z, ArrayList<Material> excludedMaterials, Location attackLocation)
	{
		this.attackLocation = attackLocation;
		this.maxHealth = maxhealth;
		this.referencePoint = referencePoint;
		this.hpLocations = hpLocations;
		this.xSize = x;
		this.zSize = z;
		this.height = y;
		for (int xv = referencePoint.getBlockX(); x > referencePoint.getBlockX() - xSize -1; x--) {
			for (int yv = referencePoint.getBlockY() + height; y > referencePoint.getBlockY() -1; y--) {
				for (int zv = referencePoint.getBlockZ(); z < referencePoint.getBlockZ()+zSize+1; z++) {
					Location loc = new Location(referencePoint.getWorld(),xv, yv, zv);
					if (loc.getBlock().getType() == Material.DISPENSER) {
							shoot.add(loc);
					}
				}
			}
		}
		this.excludedMaterials = excludedMaterials;
				
		
		
	}
	public void startGame() {
		TDPlugin plugin = (TDPlugin) Bukkit.getPluginManager().getPlugin("TDPlugin");
		for (Location hpLocation : hpLocations) {
			    Hologram hologram = new HologramFactory(plugin)    // Replace "myPlugin" with your plugin instance
			    .withLocation(new Vector(hpLocation.getX(), hpLocation.getY(), hpLocation.getZ()), hpLocation.getWorld().getName())
			    .withText("Starting Game")
			    .withText("Loading..")
			    .withSimplicity(true)
			    .withVisibility(new HoloVisible())
			    .build();
	        hpBars.add(hologram);
	     
		}
        
        setHealth(maxHealth);
	}
	public void stopGame() {
		if (!this.destroyed) {
		 for (Hologram h : hpBars) {
			 HoloAPI.getManager().clearFromFile(h.getSaveId());
       	HoloAPI.getManager().stopTracking(h);
       }}  
	}
	public void setHealth (int health) { //setHealth (tower.health-1)
		this.health = health;
		int maxdisplayhealth =(maxHealth / (maxHealth / 10))+1;
		int displayhealth;
		if (!(health < 1)) {
			displayhealth = (health / (maxHealth / 10))+2;
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
        	h.updateLine(1, String.valueOf(health)+" / "+String.valueOf(maxHealth));
        }
	}
	public void collapse() {
		this.destroyed = true;
		
		for (Hologram h : hpBars) {
			 HoloAPI.getManager().clearFromFile(h.getSaveId());
       	HoloAPI.getManager().stopTracking(h);
       }  
		final Vector direction = new Vector(0, 0, 1);
		direction.normalize();
		int fallingDistance;
		for (fallingDistance = 1; fallingDistance < 50; fallingDistance++) {
			Block newBlock = referencePoint.getWorld().getBlockAt(referencePoint.getBlockX(), referencePoint.getBlockY() - fallingDistance, referencePoint.getBlockZ());
			Material newBlockType = newBlock.getType();
			if (newBlockType.equals(Material.GRASS)||newBlockType.equals(Material.DIRT))
				break;
		}
		int i = 1;
		int blockt = 0;
		for (int x = referencePoint.getBlockX(); x > referencePoint.getBlockX() - this.xSize; x--) {
			for (int y = referencePoint.getBlockY() + this.height; y > referencePoint.getBlockY(); y--) {
				for (int z = referencePoint.getBlockZ(); z < referencePoint.getBlockZ()+this.zSize; z++) {
					Location newBlockLocation = referencePoint.getWorld().getBlockAt(x,y,z).getLocation();
					int horisontalDistance = newBlockLocation.getBlockY() - referencePoint.getBlockY() - 1;
					if (horisontalDistance < 0)
						horisontalDistance = 0;
					int verticalDistance = horisontalDistance + fallingDistance;
					
					int horisontalOffset = (int) Math.floor((horisontalDistance) / 1.5);
					float horisontalSpeed = calcSpeed(horisontalDistance, verticalDistance, fallingDistance, horisontalOffset);
					
					if (fallingDistance == 1) {
						switch (horisontalDistance) {
							case 1:
								horisontalOffset = 1;
								horisontalSpeed = 0;
								break;
							case 2:
								horisontalOffset = 1;
								horisontalSpeed = 0.1191f;
								break;
							case 3:
								horisontalOffset = 1;
								horisontalSpeed = 0.185f;
								break;
							case 4:
								horisontalOffset = 2;
								horisontalSpeed = 0.17f;
								break;
							case 5:
								horisontalOffset = 2;
								horisontalSpeed = 0.22f;
								break;
							case 6:
								horisontalOffset = 3;
								horisontalSpeed = 0.21f;
								break;
							case 7:
								horisontalOffset = 3;
								horisontalSpeed = 0.26f;
								break;
							case 8:
								horisontalOffset = 4;
								horisontalSpeed = 0.241f;
								break;
							case 9:
								horisontalOffset = 4;
								horisontalSpeed = 0.28f;
								break;

						}
					}
					if (fallingDistance == 2) {
						switch (horisontalDistance) {
							case 1:
								horisontalOffset = 1;
								horisontalSpeed = 0;
								break;
							case 2:
								horisontalOffset = 1;
								horisontalSpeed = 0.1f;
								break;
							case 5:
								horisontalOffset = 2;
								horisontalSpeed = 0.2f;
								break;

						}
					}
					final float horizontal = horisontalSpeed;
					final Location newblock = newBlockLocation;
					final Material m = newBlockLocation.getBlock().getType();
					@SuppressWarnings("deprecation")
					final int data = (0x3 & newBlockLocation.getBlock().getData()) | 0x8;
					newBlockLocation.getBlock().setType(Material.AIR);
					Bukkit.getServer().getScheduler()
							.scheduleSyncDelayedTask((TDPlugin) Bukkit.getPluginManager().getPlugin(
									"TDPlugin"), new Runnable() {
								@Override
								public void run() {
					@SuppressWarnings("deprecation")
					FallingBlock blockFalling = referencePoint.getWorld().spawnFallingBlock(newblock,m, (byte) data);
					blockFalling.setDropItem(false);
					blockFalling.setVelocity(direction.clone().multiply(horizontal));
								}
							},(long) (0.1*i));
					blockt++;
					if (blockt < 100) {
					i++;
					blockt = 0;
					}
								
								}
				
	
			}
		}

		
	}
	float calcSpeed(float horisontalDistance, float verticalDistance, float fallingDistance, int horisontalOffset) {
		float speed = 0;
		if (verticalDistance > 0) {
			speed = (horisontalDistance - horisontalOffset) / (float) Math.sqrt(2 * (verticalDistance) / 0.064814);
		}
		return speed;

	}
	public void shootAtLocation(Location l) {
		
		//create arrow entity and shoot towards location from shoot
		Location loc1 = l.clone();
		if (!shoot.isEmpty()){
		Location loc2 = shoot.get(0);
		try {
		Dispenser d2 = (Dispenser)loc2.getBlock().getState().getData();
		Location loc3 = loc2.getBlock().getRelative(d2.getFacing()).getLocation();
		double distance = loc1.distanceSquared(loc3);
		for (Location loc : shoot) {
			Dispenser d = (Dispenser)loc.getBlock().getState().getData();
			Location loc4 = loc.getBlock().getRelative(d.getFacing()).getLocation();
			if (loc1.distanceSquared(loc4) < distance) {
				loc2 = loc;
				distance = loc1.distanceSquared(loc4);
			}
				
				
		}
		
		Dispenser d = (Dispenser)loc2.getBlock().getState().getData();
		loc2 = loc2.getBlock().getRelative(d.getFacing()).getLocation();
		loc2.setY(loc2.getBlockY());
		switch (d.getFacing()) {
		case EAST:
			loc2.setX(loc2.getBlockX());
			loc2.setZ(loc2.getBlockZ()+0.5);
			break;
		case WEST:
			loc2.setX(loc2.getBlockX()+0.9);
			loc2.setZ(loc2.getBlockZ()+0.5);
			break;
		case NORTH:
			loc2.setX(loc2.getBlockX()+0.5);
			loc2.setZ(loc2.getBlockZ()+0.9);
			break;
		case SOUTH:
			loc2.setX(loc2.getBlockX()+0.5);
			loc2.setZ(loc2.getBlockZ());
			break;
		default:
			break;
		
		}
		
		loc2.setY(loc2.getBlockY()+0.5);
		Arrow a = l.getWorld().spawnArrow(loc2, new Vector(loc1.getX()-loc2.getX(), loc1.getY()-loc2.getY(), loc1.getZ()-loc2.getZ()), 1, 12);
		TDPlugin plugin = (TDPlugin)Bukkit.getPluginManager().getPlugin("TDPlugin");
		for (TDGame game: plugin.getTDConfig().games) {
			for (TDTeam team : game.teams) {
				if (team.towers2.contains(this)) {
					a.setMetadata("Team", new FixedMetadataValue(plugin, team));
				}
			}
		}
		
		} catch (Exception ex) {
			shoot.remove(0);
		}
		}}}

