package com.sanjay900.TD;

import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_7_R4.AxisAlignedBB;
import net.minecraft.server.v1_7_R4.EntityCreature;
import net.minecraft.server.v1_7_R4.EntityLiving;

public class PathfinderGoalHurtByOtherTeam extends PathfinderGoalTarget {

	boolean a;
	private int b;
	private TDTeam orig;

	public PathfinderGoalHurtByOtherTeam(EntityCreature entitycreature,
			boolean flag, TDTeam orig) {
		super(entitycreature, false);
		this.a = flag;
		this.orig = orig;
		this.a(1);
	}

	public boolean a() {
		int i = this.c.a_;

		return i != this.b && this.a(this.c.getLastDamager(), false);
	}

	public void c() {
		TDPlugin plugin = (TDPlugin) Bukkit.getPluginManager().getPlugin(
				"TDPlugin");
		EntityLiving lastDamager = this.c.getLastDamager();
		TDTeam team = null;
		if (lastDamager.getBukkitEntity().getType() == EntityType.PLAYER) {
			Player p = (Player) lastDamager.getBukkitEntity();
			for (TDGame game : plugin.getTDConfig().games) {
				for (TDTeam teamc : game.teams) {

					if (teamc.players.contains(p.getName())) {
						team = teamc;
					}
				}
			}
		} 

		if (team != null && team != orig) {
			this.c.setGoalTarget(this.c.getLastDamager());
			this.b = this.c.ah;
			if (this.a) {
				double d0 = this.f();
				List list = this.c.world.a(
						this.c.getClass(),
						AxisAlignedBB.a(this.c.locX, this.c.locY, this.c.locZ,
								this.c.locX + 1.0D, this.c.locY + 1.0D,
								this.c.locZ + 1.0D).grow(d0, 10.0D, d0));
				Iterator iterator = list.iterator();

				while (iterator.hasNext()) {
					EntityCreature entitycreature = (EntityCreature) iterator
							.next();
					TDTeam zteam = null;

					if (zteam != null && zteam == orig
							&& this.c != entitycreature
							&& entitycreature.getGoalTarget() == null
							&& !entitycreature.c(this.c.getLastDamager())) {
						entitycreature.setGoalTarget(this.c.getLastDamager());
					}
				}
			}

			super.c();
		}
	}
}