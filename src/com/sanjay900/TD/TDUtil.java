package com.sanjay900.TD;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class TDUtil {
	public static Location midpoint (Location l1, Location l2) {
		
		return new Location (l1.getWorld(), (l1.getX()+l2.getX())/2,(l1.getY()+l2.getY())/2,(l1.getZ()+l2.getZ())/2);
	}
	public static boolean isTowerClose(Location l, TDTeam orig) {
		
		return getClosestTower(l, orig) != null;
		
	}
	public static boolean compareLocation(Location l, Location l2) {
		return (l.getX() == l2.getX())
				&& (l.getY() == l2.getY())
				&& (l.getZ() == l2.getZ());

	}
	public static TDTower getClosestTower(Location l, TDTeam orig) {
		TDPlugin plugin = (TDPlugin) Bukkit.getPluginManager().getPlugin("TDPlugin");
        double distance = 10;
        TDTower tower = null;
		for (TDTeam t : plugin.getGame(orig).teams) {
        	if (t != orig) {
        		for (ArrayList<TDTower> towerr : t.towers) {
        		for (TDTower tr : towerr) {
        			if (tr.referencePoint.distance(l) < distance) {
        				distance = tr.referencePoint.distance(l);
        				tower = tr;
        			}
        		}
        		}
        	}
        }
		return tower;
	}
	public static TDTeam getTeamFromTower(TDTower tower, TDTeam orig) {
		TDPlugin plugin = (TDPlugin) Bukkit.getPluginManager().getPlugin("TDPlugin");
		for (TDTeam t : plugin.getGame(orig).teams) {
        	if (t != orig) {
        		for (ArrayList<TDTower> towerr : t.towers) {
        		for (TDTower tr : towerr) {
        			if (tr == tower) {
        				return t;
        			}
        		}
        		}
        	}
        }
		return null;
	}
	public static int getPathFromTower(TDTower tower, TDTeam orig) {
		TDPlugin plugin = (TDPlugin) Bukkit.getPluginManager().getPlugin("TDPlugin");
		for (TDTeam t : plugin.getGame(orig).teams) {
        	if (t != orig) {
        		for (ArrayList<TDTower> towerr : t.towers) {
        		for (TDTower tr : towerr) {
        			if (tr == tower) {
        				return t.towers.indexOf(towerr);
        			}
        		}
        		}
        	}
        }
		return -1;
	}
        
		public static ArrayList<TDTower> getTowers(Location l, TDTeam orig) {
			TDPlugin plugin = (TDPlugin) Bukkit.getPluginManager().getPlugin("TDPlugin");
	        double distance = 10;
	        ArrayList<TDTower> tower = new ArrayList<TDTower>();
			for (TDTeam t : plugin.getGame(orig).teams) {
	        	if (t != orig) {
	        		for (ArrayList<TDTower> towerr : t.towers) {
	        		for (TDTower tr : towerr) {
	        			if (tr.referencePoint.distance(l) < distance) {
	        				tower.add(tr);
	        			}
	        		}
	        		}
	        	}
	        }
			return tower;
	}
		/**
	     * Gets players inside a cone.
	     *
	     * @param players - {@code List<Player>}, list of all players
	     * @param startpoint - {@code Location}, centerpoint
	     * @param radius - {@code int}, radius of the cone
	     * @param degrees - {@code int}, angle of the cone
	     * @param direction - {@code int}, the direction the cone should face
	     * @return {@code List<Player>} - players in the cone
	     */
	    public static List<Player> getPlayersInCone(List<Player> players, Location startpoint, int radius, int degrees, int direction)
	    {
	        List<Player> newPlayers = new ArrayList<Player>();

	        int[] startPos = new int[] { (int)startpoint.getX(), (int)startpoint.getZ() };

	        int[] endA = new int[] { (int)(radius * Math.cos(direction - (degrees / 2))), (int)(radius * Math.sin(direction - (degrees / 2))) };

	        for(Player p : players)
	        {
	           Location l = p.getPlayer().getLocation();
	            if(!isPointInCircle(startPos[0], startPos[1], radius, l.getBlockX(), l.getBlockY()))
	                continue;

	            int[] playerVector = getVectorForPoints(startPos[0], startPos[1], l.getBlockX(), l.getBlockY());

	            double angle = getAngleBetweenVectors(endA, playerVector);
	            if(Math.toDegrees(angle) < degrees && Math.toDegrees(angle) > 0)
	                newPlayers.add(p);
	        }
	        return newPlayers;
	    }

	    /**
	     * Created an integer vector in 2d between two points
	     *
	     * @param x1 - {@code int}, X pos 1
	     * @param y1 - {@code int}, Y pos 1
	     * @param x2 - {@code int}, X pos 2
	     * @param y2 - {@code int}, Y pos 2
	     * @return {@code int[]} - vector
	     */
	    public static int[] getVectorForPoints(int x1, int y1, int x2, int y2)
	    {
	        return new int[] { x2 - x1, y2 - y1 };
	    }

	    /**
	     * Checks if a point is inside a circle with given radius.
	     *
	     * @param cx - {@code int}, circle center X
	     * @param cy - {@code int}, circle center Y
	     * @param radius - {@code int}, radius of the circle
	     * @param px - {@code int}, point X
	     * @param py - {@code int}, point Y
	     * @return {@code true}, if inside the circle
	     */
	    public static boolean isPointInCircle(int cx, int cy, int radius, int px, int py)
	    {
	        double dist = (px - cx)^2 + (py - cy)^2;
	        return dist < (radius^2);
	    }

	    /**
	     * Get the angle between two vectors.
	     *
	     * @param vector1 - {@code int[]}, vector 1
	     * @param vector2 - {@code int[]}, vector 2
	     * @return {@code double} - angle
	     */
	    public static double getAngleBetweenVectors(int[] vector1, int[] vector2)
	    {
	        return Math.atan2(vector2[1], vector2[0]) - Math.atan2(vector1[1], vector1[0]);
	    }

	    /**
	     * Gets affected players in a circle.
	     *
	     * @param player - {@code List<Player>}, list of all players
	     * @param start - {@code Location}, center of the circle
	     * @param radius - {@code int}, radius of the circle
	     * @return {@code List<Player>} - players in the circle
	     */
	    public static List<Player> getPlayersInCircle(List<Player> player, Location start, int radius)
	    {
	        List<Player> newPlayers = new ArrayList<Player>();
	        for(Player p : player)
	        {
	            if(isPointInCircle((int)start.getX(), (int)start.getZ(), radius, p.getPlayer().getLocation().getBlockX(), p.getPlayer().getLocation().getBlockZ()))
	                newPlayers.add(p);
	        }
	        return newPlayers;
	    }

	    /**
	     * Gets entities inside a cone.
	     * @see Utilities#getPlayersInCone(List, Location, int, int, int)
	     *
	     * @param entities - {@code List<Entity>}, list of nearby entities
	     * @param startpoint - {@code Location}, center point
	     * @param radius - {@code int}, radius of the circle
	     * @param degrees - {@code int}, angle of the cone
	     * @param direction - {@code int}, direction of the cone
	     * @return {@code List<Entity>} - entities in the cone
	     */
	    public static List<Entity> getEntitiesInCone(List<Entity> entities, Location startpoint, int radius, int degrees, int direction)
	    {
	        List<Entity> newEntities = new ArrayList<Entity>();

	        int[] startPos = new int[] { (int)startpoint.getX(), (int)startpoint.getZ() };

	        int[] endA = new int[] { (int)(radius * Math.cos(direction - (degrees / 2))), (int)(radius * Math.sin(direction - (degrees / 2))) };

	        for(Entity e : entities)
	        {
	           Location l = e.getLocation();
	            if(!isPointInCircle(startPos[0], startPos[1], radius, l.getBlockX(), l.getBlockY()))
	                continue;

	            int[] playerVector = getVectorForPoints(startPos[0], startPos[1], l.getBlockX(), l.getBlockY());

	            double angle = getAngleBetweenVectors(endA, playerVector);
	            if(Math.toDegrees(angle) < degrees && Math.toDegrees(angle) > 0)
	                newEntities.add(e);
	        }
	        return newEntities;
	    }

	    /**
	     * Gets affected entities inside a circle.
	     *
	     * @param entities - {@code List<Entity>}, all nearby entities
	     * @param start - {@code Location}, center point
	     * @param radius - {@code int}, radius of circle
	     * @return {@code List<Entity>} - entities in the circle
	     */
	    public static List<Entity> getEntitiesInCircle(List<Entity> entities, Location start, int radius)
	    {
	        List<Entity> newEntities = new ArrayList<Entity>();
	        for(Entity e : entities)
	        {
	            if(isPointInCircle((int)start.getX(), (int)start.getZ(), radius, e.getLocation().getBlockX(), e.getLocation().getBlockZ()))
	                newEntities.add(e);
	        }
	        return newEntities;
	    }

	    /**
	     * Converts a list of LivingEntities into a list of Entities.
	     *
	     * @param old - {@code List<LivingEntity>}, old list
	     * @return {@code List<Entity>}, new list
	     */
	    public static List<Entity> toEntityList(List<LivingEntity> old)
	    {
	        List<Entity> newEntites = new ArrayList<Entity>();
	        for(LivingEntity e : old)
	        {
	            newEntites.add((Entity)e);
	        }
	        return newEntites;
	    }
}
