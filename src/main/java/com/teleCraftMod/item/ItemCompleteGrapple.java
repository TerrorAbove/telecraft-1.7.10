package com.teleCraftMod.item;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.teleCraftMod.TeleCraft;
import com.teleCraftMod.entity.EntityGrappleHook;
import com.teleCraftMod.packet.EnderBucketPacket;
import com.teleCraftMod.packet.GrapplePacket;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ItemCompleteGrapple extends Item
{
	public static final int MAX_GRAPPLE_DISTANCE = 32;
	
	public HashMap<EntityPlayer, EntityGrappleHook> hooks;
	
	private IIcon grappleIcon;
	private IIcon grappleLaunchedIcon;
	
	public ItemCompleteGrapple()
	{
		super();
		
		setMaxStackSize(1);
		setMaxDamage(1000);//previously 200, now with variable degrade based on distance
		hooks = new HashMap<EntityPlayer, EntityGrappleHook>();
	}
	
	public void registerIcons(IIconRegister reg)
	{
		grappleIcon = reg.registerIcon("telecraft:complete_grapple");
		grappleLaunchedIcon = reg.registerIcon("telecraft:complete_grapple_launched");
	}
	
	public IIcon getIcon(ItemStack stack, int a)
	{
		return getIconIndex(stack);
	}
	
	public IIcon getIconIndex(ItemStack stack)
	{
		//hooks empty check will prevent error on server side, if this is even called there
		if(!hooks.isEmpty() && hooks.containsKey(Minecraft.getMinecraft().thePlayer))
		{
			if(hooks.get(Minecraft.getMinecraft().thePlayer).isEntityAlive())
			{
				return grappleLaunchedIcon;
			}
		}
		
		return grappleIcon;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World w, EntityPlayer p)
	{
		if(!w.isRemote)
			return stack;
		
		MovingObjectPosition mopLiquids = getMovingObjectPositionFromPlayer(w, p, true);
		
		Vec3 playerPos = Vec3.createVectorHelper(p.posX, p.posY, p.posZ);
		
		if(stack != null && mopLiquids != null && mopLiquids.hitVec.distanceTo(playerPos) <= MAX_GRAPPLE_DISTANCE)
		{
			MovingObjectPosition targMop = TeleCraft.getTarget(1.0F, (double)MAX_GRAPPLE_DISTANCE);
			
			if(targMop != null)
			{
				EntityLivingBase target = (EntityLivingBase)targMop.entityHit;
				
				if(hooks.containsKey(p))
				{
					if(!hooks.get(p).isLaunchComplete())
						return stack;
					
					//hooks.get(p).setDead();
				}
				
				EntityGrappleHook hook = new EntityGrappleHook(w, targMop.hitVec.xCoord, targMop.hitVec.yCoord, targMop.hitVec.zCoord, p);
				hook.setVars(target, 10, 40, 0, 0, 0);//no need to do velocity here, server side handles it
				
				if(!(target instanceof EntityPlayer) || p.canAttackPlayer((EntityPlayer)target))
				{
					w.spawnEntityInWorld(hook);
					
					hooks.put(p, hook);
				}
				
				TeleCraft.wrapper.sendToServer(new GrapplePacket(target.getEntityId(), 0, 0, 0, 0, 0, (byte) 1));
			}
			else if(mopLiquids.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
			{
				if(hooks.containsKey(p))
				{
					if(!hooks.get(p).isLaunchComplete())
						return stack;
					
					//hooks.get(p).setDead();
				}
				
				Vec3 diff = playerPos.subtract(mopLiquids.hitVec);
				Vec3 blockDiff = Vec3.createVectorHelper((int)p.posX, (int)p.posY, (int)p.posZ).subtract(Vec3.createVectorHelper(mopLiquids.blockX, mopLiquids.blockY, mopLiquids.blockZ));
				
				int extraX = 0;
				int extraY = 0;
				int extraZ = 0;
				
				/*while(!w.isAirBlock(mopLiquids.blockX, mopLiquids.blockY+1, mopLiquids.blockZ))
				{
					extraY++;
				}
				
				mopLiquids.blockY += extraY;
				blockDiff.yCoord += extraY;
				diff.yCoord += extraY;
				
				extraY = 0;*/
				
				switch(mopLiquids.sideHit)
				{
				case 0:
					--extraY;
					break;
				case 1:
					++extraY;
					break;
				case 2:
					--extraZ;
					break;
				case 3:
					++extraZ;
					break;
				case 4:
					--extraX;
					break;
				case 5:
					++extraX;
					break;
				}
				
				blockDiff.yCoord += 2;
				diff.yCoord += 2;
				
				if(blockDiff.distanceTo(Vec3.createVectorHelper(0, 0, 0)) < 2)
				{
					return stack;//cancel a trivial jump
				}
				
				if(!w.isAirBlock(mopLiquids.blockX+extraX, mopLiquids.blockY+extraY, mopLiquids.blockZ+extraZ))
				{
					return stack;//can't place a hook on a non-air block
				}
				
				double horizontal_length = Vec3.createVectorHelper(diff.xCoord, 0, diff.zCoord).lengthVector();
				
				if(diff.yCoord < 1)
					diff.yCoord = 1 + horizontal_length / 16.0;
				else
					diff.yCoord += horizontal_length / 8.0;
				
				diff.yCoord = Math.sqrt(diff.yCoord);
				diff.yCoord /= 2;
				
				diff.xCoord /= 7;
				diff.zCoord /= 7;
				
				EntityGrappleHook hook = new EntityGrappleHook(w, mopLiquids.hitVec.xCoord, mopLiquids.hitVec.yCoord, mopLiquids.hitVec.zCoord, p);
				hook.setVars(p, 10, 40, diff.xCoord, diff.yCoord, diff.zCoord);
				
				w.spawnEntityInWorld(hook);
				
				hooks.put(p, hook);
				
				TeleCraft.wrapper.sendToServer(new GrapplePacket(diff.xCoord, diff.yCoord, diff.zCoord, mopLiquids.hitVec.xCoord, mopLiquids.hitVec.yCoord, mopLiquids.hitVec.zCoord, (byte) 0));//damage item, add temp resistance effect
			}
		}
		
		return stack;
	}
	
	protected MovingObjectPosition getMovingObjectPositionFromPlayer(World p_77621_1_, EntityPlayer p_77621_2_, boolean p_77621_3_)
    {
        float var4 = 1.0F;
        float var5 = p_77621_2_.prevRotationPitch + (p_77621_2_.rotationPitch - p_77621_2_.prevRotationPitch) * var4;
        float var6 = p_77621_2_.prevRotationYaw + (p_77621_2_.rotationYaw - p_77621_2_.prevRotationYaw) * var4;
        double var7 = p_77621_2_.prevPosX + (p_77621_2_.posX - p_77621_2_.prevPosX) * (double)var4;
        double var9 = p_77621_2_.prevPosY + (p_77621_2_.posY - p_77621_2_.prevPosY) * (double)var4 + 1.62D - (double)p_77621_2_.yOffset;
        double var11 = p_77621_2_.prevPosZ + (p_77621_2_.posZ - p_77621_2_.prevPosZ) * (double)var4;
        Vec3 var13 = Vec3.createVectorHelper(var7, var9, var11);
        float var14 = MathHelper.cos(-var6 * 0.017453292F - (float)Math.PI);
        float var15 = MathHelper.sin(-var6 * 0.017453292F - (float)Math.PI);
        float var16 = -MathHelper.cos(-var5 * 0.017453292F);
        float var17 = MathHelper.sin(-var5 * 0.017453292F);
        float var18 = var15 * var16;
        float var20 = var14 * var16;
        double var21 = (double)MAX_GRAPPLE_DISTANCE;
        Vec3 var23 = var13.addVector((double)var18 * var21, (double)var17 * var21, (double)var20 * var21);
        return p_77621_1_.func_147447_a(var13, var23, p_77621_3_, !p_77621_3_, false);
    }
}
