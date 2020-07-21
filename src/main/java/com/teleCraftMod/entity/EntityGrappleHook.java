package com.teleCraftMod.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityGrappleHook extends Entity
{
	private int tickDelayLaunch;
	private int stayAliveTicks;
	private double xVel, yVel, zVel;
	private EntityLivingBase toLaunch;
	private double xOffset, yOffset, zOffset;//used to get a more accurate location when pulling a distant target
	
	public EntityPlayer player;
	//private float damageAmount;
	
	private boolean launchComplete;
	
	public EntityGrappleHook(World world, double x, double y, double z, EntityPlayer player)
	{
		super(world);
		this.player = player;
		this.setPosition(x, y, z);
		xOffset = 0;
		yOffset = 0;
		zOffset = 0;
	}
	
	public void setVars(EntityLivingBase toLaunch, int tickDelayLaunch, int stayAliveTicks, double xVel, double yVel, double zVel)//, float damageAmount)
	{
		this.toLaunch = toLaunch;
		this.tickDelayLaunch = tickDelayLaunch;
		this.stayAliveTicks = stayAliveTicks;
		this.xVel = xVel;
		this.yVel = yVel;
		this.zVel = zVel;
		this.launchComplete = false;
		//this.damageAmount = damageAmount;
		
		if(toLaunch != player)
		{
			xOffset = this.posX - toLaunch.posX;
			yOffset = this.posY - toLaunch.posY;
			zOffset = this.posZ - toLaunch.posZ;
		}
	}
	
	public void onUpdate()
	{
		super.onUpdate();
		
		if(this.ticksExisted >= tickDelayLaunch)
		{
			if(!launchComplete)
			{
				toLaunch.addVelocity(xVel, yVel, zVel);
				launchComplete = true;
			}
		}
		
		if(toLaunch != player)
		{
			this.setPosition(xOffset + toLaunch.posX, yOffset + toLaunch.posY, zOffset + toLaunch.posZ);
		}
		
		if(this.ticksExisted >= stayAliveTicks)
			this.setDead();
	}
	
	public boolean isLaunchComplete()
	{
		return launchComplete;
	}

	@Override
	protected void entityInit() {}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag)
	{
		//super.readFromNBT(tag);
		/*this.tickDelayLaunch = tag.getInteger("tickDelayLaunch");
		this.stayAliveTicks = tag.getInteger("stayAliveTicks");
		this.xVel = tag.getDouble("xVel");
		this.yVel = tag.getDouble("yVel");
		this.zVel = tag.getDouble("zVel");
		*/
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag)
	{
		//super.writeToNBT(tag);
	}
}
