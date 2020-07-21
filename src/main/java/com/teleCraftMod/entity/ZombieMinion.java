package com.teleCraftMod.entity;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class ZombieMinion extends EntityMob
{
	private boolean offensive;
	private long expireTime;
	private float scaleFactor;
	private EntityPlayer owner;
	private Entity target;
	
	public ZombieMinion(World world, EntityPlayer owner, Entity target, boolean offensive, long expire)
	{
		this(world);
		this.owner = owner;
		this.target = target;
		this.offensive = offensive;
		this.expireTime = expire;
		
		if(offensive)
			setPosition(target.posX + 3 - Math.random() * 6, target.posY, target.posZ + 3 - Math.random() * 6);
		else
			setPosition(owner.posX + owner.getLookVec().xCoord, owner.posY, owner.posZ + owner.getLookVec().zCoord);
		
		scaleFactor = 0.75F + (float)(Math.random() / 2.0);
		
		setSize(0.6F * scaleFactor, 1.8F * scaleFactor);
		
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).applyModifier(new AttributeModifier("random", 1 / scaleFactor, 2));
		getEntityAttribute(SharedMonsterAttributes.attackDamage).applyModifier(new AttributeModifier("random", scaleFactor, 2));
		getEntityAttribute(SharedMonsterAttributes.maxHealth).applyModifier(new AttributeModifier("random", scaleFactor, 2));
		getEntityAttribute(SharedMonsterAttributes.knockbackResistance).applyModifier(new AttributeModifier("random", scaleFactor, 2));
	}
	
	public ZombieMinion(World world, EntityPlayer owner, Entity target, boolean offensive)
	{
		this(world, owner, target, offensive, -1);
	}
	
	public ZombieMinion(World world)
	{
		super(world);
	}
	
	protected Entity findPlayerToAttack()
    {
		if((offensive && (target == null || target.isDead || target.getDistanceToEntity(this) > 10)) || owner == null)
		{
			this.setDead();
			return null;
		}
		
		if(offensive)
			return target;
		
		EntityLivingBase attacker = owner.getLastAttacker();
		List mobs = worldObj.getEntitiesWithinAABB(EntityMob.class, AxisAlignedBB.getBoundingBox(owner.posX - 3, owner.posY - 1, owner.posZ - 3, owner.posX + 3, owner.posY + 2, owner.posZ + 3));
		
		for(int i = 0; i < mobs.size(); i++)
			if(mobs.get(i) instanceof ZombieMinion)
				mobs.remove(i--);
		
		return (attacker != null && !attacker.isDead && attacker.getDistanceToEntity(this) < 7) ? attacker : mobs.size() > 0 ? (Entity)mobs.get(0) : null;
    }
	
	public void onLivingUpdate()
	{
		super.onLivingUpdate();
		if(!offensive && owner != null && owner.getDistanceToEntity(this) > 7)
		{
			this.setPositionAndUpdate(owner.posX - 3 + Math.random() * 6, owner.posY, owner.posZ - 3 + Math.random() * 6);
		}
		if(expireTime > 0 && System.currentTimeMillis() >= expireTime)
		{
			this.setDead();
		}
	}
	
	public boolean attackEntityFrom(DamageSource source, float f)
    {
		if(source.getSourceOfDamage() == owner)
			return false;
		return super.attackEntityFrom(source, f);
    }
	
	public boolean getCanSpawnHere()
    {
		return true;
    }
	
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(40.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.5D);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(3.0D);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1.0D);
    }
	
	public float getScaleFactor()
	{
		return scaleFactor;
	}
}
