package com.sanjay900.TD;

import java.util.Collections;
import java.util.List;

import net.minecraft.server.v1_7_R4.DistanceComparator;
import net.minecraft.server.v1_7_R4.EntityCreature;
import net.minecraft.server.v1_7_R4.EntityLiving;
import net.minecraft.server.v1_7_R4.IEntitySelector;

public class PathfinderGoalNearestAttackableTargetCustom extends
		PathfinderGoalTarget {

	@SuppressWarnings("rawtypes")
	private final Class a;
	@SuppressWarnings("unused")
	private final int b;
	private final DistanceComparator e;
	private final IEntitySelector f;
	private EntityLiving g;
	private TDTeam orig;

	@SuppressWarnings("rawtypes")
	public PathfinderGoalNearestAttackableTargetCustom(
			EntityCreature entitycreature, Class oclass, int i, boolean flag,
			TDTeam orig) {
		this(entitycreature, oclass, i, flag, false, orig);
	}

	@SuppressWarnings("rawtypes")
	public PathfinderGoalNearestAttackableTargetCustom(
			EntityCreature entitycreature, Class oclass, int i, boolean flag,
			boolean flag1, TDTeam orig) {
		this(entitycreature, oclass, i, flag, flag1, (IEntitySelector) null,
				orig);
	}

	@SuppressWarnings("rawtypes")
	public PathfinderGoalNearestAttackableTargetCustom(
			EntityCreature entitycreature, Class oclass, int i, boolean flag,
			boolean flag1, IEntitySelector ientityselector, TDTeam orig) {
		super(entitycreature, flag, flag1);
		this.a = oclass;
		this.b = i;
		this.e = new DistanceComparator(entitycreature);
		this.a(1);
		f = null;
		this.orig = orig;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean a() {
			double d0 = this.f();
			List list = this.c.world.a(this.a,
					this.c.boundingBox.grow(d0, 4.0D, d0), this.f);
			Collections.sort(list, this.e);

			if (list.isEmpty()) {
				return false;
			} else {
				for (Object e : list) {

					EntityLiving entityliving = (EntityLiving) e;
					if (entityliving == null) {
						return false;
					} else if (!entityliving.isAlive()) {
						return false;
					} else {
				 return false;}
					
				}
			}
			return false;
		}
	

	public void c() {
		this.c.setGoalTarget(this.g);
		super.c();
	}
}
