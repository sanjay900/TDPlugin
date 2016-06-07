package com.sanjay900.TD;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.server.v1_7_R4.EntityCreature;
import net.minecraft.server.v1_7_R4.EntityLiving;
import net.minecraft.server.v1_7_R4.PacketPlayOutAnimation;
import net.minecraft.server.v1_7_R4.PathfinderGoal;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class PathfinderGoalWalktoTile
  extends PathfinderGoal
{
  double speed;
  double x = 0.0D;
  double y = 0.0D;
  double z = 0.0D;
  Location spawnloc;
  ArrayList<Location> path = new ArrayList<Location>();
  private EntityCreature a;
  int stopped = 0;
  int current = 0;
  int delay = 0;
  private TDTeam dest;
  private TDTeam orig;
  private Location target;
  private int lane;
  private Location midpoint;
  
  public PathfinderGoalWalktoTile(EntityCreature entitycreature, float speed, TDTeam orig, TDTeam dest, int lane, Location midpoint)
  {
    this.speed = speed;
    this.midpoint = midpoint;
    this.a = entitycreature;
    this.dest = dest;
    this.orig = orig;
    this.spawnloc = orig.spawnlocation;
    this.lane = lane;
    Location closest;
    if (dest.towers.isEmpty()) {
      closest = dest.treeLoc;
    } else {
      closest = ((TDTower)((ArrayList<?>)dest.towers.get(lane)).get(0)).attackLocation;
    }
    this.target = closest;
    Location middle = TDUtil.midpoint(midpoint, this.spawnloc);
    Location quarter = TDUtil.midpoint(this.spawnloc, middle);
    Location quarterofquarter = TDUtil.midpoint(this.spawnloc, quarter);
    Location quartertomid = TDUtil.midpoint(middle, quarter);
    
    Location threequarters = TDUtil.midpoint(middle, midpoint);
    Location quarterofthree = TDUtil.midpoint(middle, threequarters);
    Location quarterofthreetotarget = TDUtil.midpoint(midpoint, 
      threequarters);
    
    this.path.add(quarterofquarter);
    this.path.add(quarter);
    this.path.add(quartertomid);
    this.path.add(middle);
    this.path.add(quarterofthree);
    this.path.add(threequarters);
    this.path.add(quarterofthreetotarget);
    this.path.add(midpoint);
    this.target = closest;
    Location middle2 = TDUtil.midpoint(this.target, midpoint);
    Location quarter2 = TDUtil.midpoint(midpoint, middle2);
    Location quarterofquarter2 = TDUtil.midpoint(midpoint, quarter2);
    Location quartertomid2 = TDUtil.midpoint(middle2, quarter2);
    
    Location threequarters2 = TDUtil.midpoint(middle2, this.target);
    Location quarterofthree2 = TDUtil.midpoint(middle2, threequarters2);
    Location quarterofthreetotarget2 = TDUtil.midpoint(this.target, 
      threequarters2);
    
    this.path.add(quarterofquarter2);
    this.path.add(quarter2);
    this.path.add(quartertomid2);
    this.path.add(middle2);
    this.path.add(quarterofthree2);
    this.path.add(threequarters2);
    this.path.add(quarterofthreetotarget2);
    this.path.add(this.target);
  }
  
  public boolean a()
  {
    EntityLiving entityliving = this.a.getGoalTarget();
    if ((entityliving == null) || (!entityliving.isAlive())) {
      return true;
    }
    return false;
  }
  
  @SuppressWarnings("unchecked")
public boolean b()
  {
    TDPlugin plugin = (TDPlugin)Bukkit.getPluginManager().getPlugin(
      "TDPlugin");
    if (TDUtil.isTowerClose(this.a.getBukkitEntity().getLocation(), this.orig))
    {
      TDTower tower = TDUtil.getClosestTower(this.a.getBukkitEntity()
        .getLocation(), this.orig);
      tower.isShooting = true;
      if (!plugin.playersToTower.containsKey(tower)) {
        plugin.playersToTower.put(tower, 
          (LivingEntity)this.a.getBukkitEntity());
      }
    }
    else if (plugin.playersToTower.containsValue((LivingEntity)this.a.getBukkitEntity()))
    {
      TDTower tower = null;
      
      Iterator<?> localIterator1 = plugin.playersToTower.entrySet().iterator();
      while (localIterator1.hasNext())
      {
        Map.Entry<TDTower, LivingEntity> entry = (Map.Entry<TDTower, LivingEntity>)localIterator1.next();
        if (((LivingEntity)this.a.getBukkitEntity()).equals(entry.getValue())) {
          tower = entry.getKey();
        }
      }
      if (plugin.playersToTower.get(tower) == 
        (LivingEntity)this.a.getBukkitEntity())
      {
        plugin.playersToTower.remove(tower);
        tower.isShooting = false;
        
        localIterator1 = this.a.getBukkitEntity().getNearbyEntities(10.0D, 10.0D, 10.0D).iterator();
        while (localIterator1.hasNext())
        {
          Entity entity = (Entity)localIterator1.next();
          TDTower tower2 = null;
          if ((entity.getType() == EntityType.ZOMBIE) && 
            ((entity instanceof customZombie)))
          {
            tower2 = TDUtil.getClosestTower(
              entity.getLocation(), 
              ((customZombie)entity).orig);
          }
          else
          {
            if (entity.getType() != EntityType.PLAYER) {
              return false;
            }
            int j = 0;
            int i = 0;
            for (Iterator<TDGame> localIterator2 = plugin.getTDConfig().games.iterator(); localIterator2.hasNext();)
            {
              TDGame game = localIterator2.next();
              TDTeam[] arrayOfTDTeam;
              j = (arrayOfTDTeam = game.teams).length;
              i = 0; 
              TDTeam teamc = arrayOfTDTeam[i];
              Player p = (Player)entity;
              if (teamc.players.contains(p.getName())) {
                tower2 = TDUtil.getClosestTower(
                  entity.getLocation(), teamc);
              }
              i++;
            }
          }
          if (tower2 == null) {
            return false;
          }
          tower2.isShooting = true;
          if (!plugin.playersToTower.containsKey(tower2))
          {
            plugin.playersToTower.put(tower2, 
              (LivingEntity)entity);
            break;
          }
        }
      }
    }
    EntityLiving entityliving = this.a.getGoalTarget();
    if ((entityliving == null) || (!entityliving.isAlive())) {
      if (this.a.getNavigation().g())
      {
        Location l = this.path.get(this.current);
        this.a.getNavigation().a(l.getX(), l.getY(), l.getZ(), this.speed);
      }
    }
    return !this.a.getNavigation().g();
  }
  
  public void c()
  {
    EntityLiving entityliving = this.a.getGoalTarget();
    if ((entityliving == null) || (!entityliving.isAlive()))
    {
      if (this.a.getNavigation().g())
      {
        this.stopped += 1;
        Location l = this.path.get(this.current);
        this.a.getNavigation().a(l.getX(), l.getY(), l.getZ(), this.speed);
      }
      if ((this.stopped > 2) && (this.current < this.path.size() - 1))
      {
        this.stopped = 0;
        this.current += 1;
        Location l = this.path.get(this.current);
        this.a.getNavigation().a(l.getX(), l.getY(), l.getZ(), this.speed);
      }
      else if (this.stopped > 2)
      {
        PacketPlayOutAnimation anim = new PacketPlayOutAnimation(
          this.a, 0);
        for (Player p : this.spawnloc.getWorld().getPlayers()) {
          ((CraftPlayer)p).getHandle().playerConnection.sendPacket(anim);
        }
        if (this.delay < 20)
        {
          this.delay += 1;
        }
        else
        {
          this.delay = 0;
          if (this.dest.towers.isEmpty())
          {
            if (this.a.getBukkitEntity().getLocation().distance(this.dest.calcLoc) < 10.0D)
            {
              this.a.getBukkitEntity().teleport(
                this.a.getBukkitEntity()
                .getLocation()
                .setDirection(
                this.a.getBukkitEntity()
                .getLocation()
                .toVector()
                .subtract(
                this.target.toVector())));
              this.dest.setHealth(this.dest.health - 10);
              if (this.dest.health < 1)
              {
                TDPlugin plugin = (TDPlugin)
                  Bukkit.getPluginManager().getPlugin(
                  "TDPlugin");
                
                Iterator<?> localIterator2 = this.spawnloc.getWorld().getPlayers().iterator();
                while (localIterator2.hasNext())
                {
                  Player p = (Player)localIterator2.next();
                  p.sendMessage("The Team " + this.dest.teamColour + 
                    this.dest.teamname + " has fallen!");
                }
                for (customZombie z : this.dest.zombies) {
                  z.setHealth(0.0F);
                }
                int deadTeams = 0;
                TDTeam lastAliveTeam = this.dest;
                for (TDTeam t : plugin.getGame(this.dest).teams) {
                  if (t.health < 1) {
                    deadTeams++;
                  } else {
                    lastAliveTeam = t;
                  }
                }
                if (deadTeams >= plugin.getGame(this.dest).teams.length - 1)
                {
                  plugin.getGame(this.dest).stopgame();
                  
                  Iterator<?> localIteratoR4 = this.spawnloc.getWorld().getPlayers().iterator();
                  while (localIteratoR4.hasNext())
                  {
                    Player p = (Player)localIteratoR4.next();
                    p.sendMessage("The Team " + 
                      lastAliveTeam.teamColour + 
                      lastAliveTeam.teamname + 
                      " is victorious!");
                  }
                }
              }
            }
            else
            {
              this.stopped = 0;
            }
          }
          else
          {
            TDTower tower = (TDTower)((ArrayList<?>)this.dest.towers.get(this.lane)).get(0);
            if (this.a.getBukkitEntity().getLocation().distance(tower.attackLocation) < 10.0D)
            {
              this.a.getBukkitEntity().teleport(
                this.a.getBukkitEntity()
                .getLocation()
                .setDirection(
                this.a.getBukkitEntity()
                .getLocation()
                .toVector()
                .subtract(
                this.target.toVector())));
              
              tower.setHealth(tower.health - 10);
              if (tower.health < 1)
              {
                tower.collapse();
                ((ArrayList<?>)this.dest.towers.get(this.lane)).remove(tower);
                Location closest;
                if (this.dest.towers.isEmpty()) {
                  closest = this.dest.treeLoc;
                } else {
                  closest = ((TDTower)((ArrayList<?>)this.dest.towers.get(this.lane)).get(0)).attackLocation;
                }
                Location currentloc = this.a.getBukkitEntity()
                  .getLocation();
                Location middle = TDUtil.midpoint(this.midpoint, 
                  currentloc);
                Location quarter = TDUtil.midpoint(currentloc, 
                  middle);
                Location quarterofquarter = TDUtil.midpoint(
                  currentloc, quarter);
                Location quartertomid = TDUtil.midpoint(middle, 
                  quarter);
                
                Location threequarters = TDUtil.midpoint(
                  middle, this.midpoint);
                Location quarterofthree = TDUtil.midpoint(
                  middle, threequarters);
                Location quarterofthreetotarget = 
                  TDUtil.midpoint(this.midpoint, threequarters);
                this.path.clear();
                this.path.add(quarterofquarter);
                this.path.add(quarter);
                this.path.add(quartertomid);
                this.path.add(middle);
                this.path.add(quarterofthree);
                this.path.add(threequarters);
                this.path.add(quarterofthreetotarget);
                this.path.add(this.midpoint);
                this.target = closest;
                Location middle2 = TDUtil.midpoint(this.target, 
                  this.midpoint);
                Location quarter2 = TDUtil.midpoint(this.midpoint, 
                  middle2);
                Location quarterofquarter2 = TDUtil.midpoint(
                  this.midpoint, quarter2);
                Location quartertomid2 = TDUtil.midpoint(
                  middle2, quarter2);
                
                Location threequarters2 = TDUtil.midpoint(
                  middle2, this.target);
                Location quarterofthree2 = TDUtil.midpoint(
                  middle2, threequarters2);
                Location quarterofthreetotarget2 = 
                  TDUtil.midpoint(this.target, threequarters2);
                
                this.path.add(quarterofquarter2);
                this.path.add(quarter2);
                this.path.add(quartertomid2);
                this.path.add(middle2);
                this.path.add(quarterofthree2);
                this.path.add(threequarters2);
                this.path.add(quarterofthreetotarget2);
                this.path.add(this.target);
                Location l = this.path.get(0);
                this.a.getNavigation().a(l.getX(), l.getY(), 
                  l.getZ(), this.speed);
                this.stopped = 0;
                this.current = 0;
              }
            }
            else
            {
              this.stopped = 0;
            }
          }
        }
        Location closest;
        if (this.dest.towers.isEmpty()) {
          closest = this.dest.treeLoc;
        } else {
          closest = ((TDTower)((ArrayList<?>)this.dest.towers.get(this.lane)).get(0)).attackLocation;
        }
        if (this.target != closest)
        {
          Location currentloc = this.a.getBukkitEntity()
            .getLocation();
          Location middle = TDUtil.midpoint(this.midpoint, currentloc);
          Location quarter = TDUtil.midpoint(currentloc, middle);
          Location quarterofquarter = TDUtil.midpoint(currentloc, 
            quarter);
          Location quartertomid = TDUtil.midpoint(middle, quarter);
          
          Location threequarters = TDUtil.midpoint(middle, this.midpoint);
          Location quarterofthree = TDUtil.midpoint(middle, 
            threequarters);
          Location quarterofthreetotarget = TDUtil.midpoint(this.midpoint, 
            threequarters);
          this.path.clear();
          this.path.add(quarterofquarter);
          this.path.add(quarter);
          this.path.add(quartertomid);
          this.path.add(middle);
          this.path.add(quarterofthree);
          this.path.add(threequarters);
          this.path.add(quarterofthreetotarget);
          this.path.add(this.midpoint);
          this.target = closest;
          Location middle2 = TDUtil.midpoint(this.target, this.midpoint);
          Location quarter2 = TDUtil.midpoint(this.midpoint, middle2);
          Location quarterofquarter2 = TDUtil.midpoint(this.midpoint, 
            quarter2);
          Location quartertomid2 = TDUtil.midpoint(middle2, quarter2);
          
          Location threequarters2 = TDUtil.midpoint(middle2, this.target);
          Location quarterofthree2 = TDUtil.midpoint(middle2, 
            threequarters2);
          Location quarterofthreetotarget2 = TDUtil.midpoint(this.target, 
            threequarters2);
          
          this.path.add(quarterofquarter2);
          this.path.add(quarter2);
          this.path.add(quartertomid2);
          this.path.add(middle2);
          this.path.add(quarterofthree2);
          this.path.add(threequarters2);
          this.path.add(quarterofthreetotarget2);
          this.path.add(this.target);
          Location l = this.path.get(0);
          this.a.getNavigation().a(l.getX(), l.getY(), l.getZ(), 
            this.speed);
          this.stopped = 0;
          this.current = 0;
        }
      }
    }
  }
}
