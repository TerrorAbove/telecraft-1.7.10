package com.teleCraftMod.entity;

import com.teleCraftMod.TeleCraft;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.world.World;

public class EntityLandMinePrimed extends EntityTNTPrimed
{
	public EntityLandMinePrimed(World world, double d0, double d1, double d2, EntityLivingBase e)
	{
		super(world, d0, d1, d2, e);
		this.fuse = 0;
	}
	
	public void onUpdate()
	{
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.motionY -= 0.03999999910593033D;
		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		this.motionX *= 0.9800000190734863D;
		this.motionY *= 0.9800000190734863D;
		this.motionZ *= 0.9800000190734863D;

		if (this.onGround)
		{
			this.motionX *= 0.699999988079071D;
			this.motionZ *= 0.699999988079071D;
			this.motionY *= -0.5D;
		}

		this.setDead();

		if (!this.worldObj.isRemote)
		{
			explode(4.0F, TeleCraft.shouldLandMineDestroyTerrain);
		}
	}
	
	private void explode(float f, boolean b)
	{
		this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, f, b);
	}
}
