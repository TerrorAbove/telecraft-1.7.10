package com.teleCraftMod.item;

import java.util.List;

import com.teleCraftMod.TeleCraft;
import com.teleCraftMod.packet.EnderBucketPacket;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServerMulti;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.FillBucketEvent;

public class EnderBucket extends ItemBucket
{
	public static final int MAX_ENDER_BUCKET_DISTANCE = 64;
	
	private Block isFull;

	public EnderBucket(Block block)
	{
		super(block);
		this.isFull = block;
	}
	
	//@SideOnly(Side.CLIENT)
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		boolean empty = this.isFull == Blocks.air;
		MovingObjectPosition mop = getMovingObjectPositionFromPlayer(world, player, empty);
		
		if(stack.getTagCompound() == null)
    		stack.setTagCompound(new NBTTagCompound());
		
		if(mop != null)
        {
			//handle bucket behavior from other mods
			
			/*ItemStack temp = stack.copy();//we need to pass in the vanilla version of each bucket
			
			if(temp.getItem() == TeleCraft.enderBucketEmpty)
				temp = new ItemStack(Items.bucket, temp.stackSize);
			else if(temp.getItem() == TeleCraft.enderBucketMilk)
				temp = new ItemStack(Items.milk_bucket);
			else if(temp.getItem() == TeleCraft.enderBucketWater)
				temp = new ItemStack(Items.water_bucket);
			else if(temp.getItem() == TeleCraft.enderBucketLava)
				temp = new ItemStack(Items.lava_bucket);
			
			FillBucketEvent event = new FillBucketEvent(player, temp, world, mop);
			
			if (MinecraftForge.EVENT_BUS.post(event))
			{
				System.out.println("DEBUG - 0");
				return stack;
			}

			if (event.getResult() == Event.Result.ALLOW)
			{
				System.out.println("DEBUG - 1");
				
				if (player.capabilities.isCreativeMode)
				{
					return stack;
				}
				
				if(event.result != null)
				{
					//sabotage other mods' result muwahaha
					if(event.result.getItem() == Items.bucket)
						event.result = new ItemStack(TeleCraft.enderBucketEmpty, event.result.stackSize);
					else if(event.result.getItem() == Items.milk_bucket)
						event.result = new ItemStack(TeleCraft.enderBucketMilk);
					else if(event.result.getItem() == Items.water_bucket)
						event.result = new ItemStack(TeleCraft.enderBucketWater);
					else if(event.result.getItem() == Items.lava_bucket)
						event.result = new ItemStack(TeleCraft.enderBucketLava);
				}

				if (--stack.stackSize <= 0)
				{
					return event.result;
				}

				if (!player.inventory.addItemStackToInventory(event.result))
				{
					player.dropPlayerItemWithRandomChoice(event.result, false);
				}

				return stack;
			}*/
			
			if(world.isRemote)
			{
				MovingObjectPosition targMop = TeleCraft.getTarget(1.0F, (double)MAX_ENDER_BUCKET_DISTANCE);
				if(targMop != null && targMop.entityHit instanceof EntityCow)
				{
					stack.getTagCompound().setBoolean("cow", true);
					TeleCraft.wrapper.sendToServer(new EnderBucketPacket(stack));
				}
				else if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && mop.hitVec.distanceTo(Vec3.createVectorHelper(player.posX, player.posY, player.posZ)) <= MAX_ENDER_BUCKET_DISTANCE)
	            {
					NBTTagCompound c = stack.getTagCompound();
					
	            	c.setInteger("x", mop.blockX);
	            	c.setInteger("y", mop.blockY);
	            	c.setInteger("z", mop.blockZ);
	            	
	            	Vec3 hitVec = mop.hitVec;
	            	
	            	c.setFloat("fx", (float)(hitVec.xCoord % 1.0));
	            	c.setFloat("fy", (float)(hitVec.yCoord % 1.0));
	            	c.setFloat("fz", (float)(hitVec.zCoord % 1.0));
	            	
	            	c.setInteger("side", empty ? -1 : mop.sideHit);
	            	
	            	TeleCraft.wrapper.sendToServer(new EnderBucketPacket(stack));
	            }
			}
        }

        return stack;
	}
	
	//@SideOnly(Side.CLIENT)
	/*public boolean onItemUse(ItemStack stack, EntityPlayer p, World w, int x, int y, int z, int a, float f0, float f1, float f2)
	{
		onItemRightClick(stack, w, p);
		return true;
	}*/
	
	//no longer need this
	public ItemStack func_150910_a(ItemStack stack, EntityPlayer player, Item item)
    {
        if (player.capabilities.isCreativeMode)
        {
            return stack;
        }
        else if (--stack.stackSize <= 0)
        {
            return new ItemStack(item);
        }
        else
        {
            if (!player.inventory.addItemStackToInventory(new ItemStack(item)))
            {
                player.dropPlayerItemWithRandomChoice(new ItemStack(item, 1, 0), false);
            }

            return stack;
        }
    }
	
	public boolean tryPlaceContainedLiquid(World world, int x, int y, int z)
    {
        if (this.isFull == Blocks.air)
        {
            return false;
        }
        else
        {
            Material mat = world.getBlock(x, y, z).getMaterial();
            boolean var6 = !mat.isSolid();

            if (!world.isAirBlock(x, y, z) && !var6)
            {
                return false;
            }
            else
            {
                if (world.provider.isHellWorld && this.isFull == Blocks.flowing_water)
                {
                    world.playSoundEffect((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

                    for (int var7 = 0; var7 < 8; ++var7)
                    {
                        world.spawnParticle("largesmoke", (double)x + Math.random(), (double)y + Math.random(), (double)z + Math.random(), 0.0D, 0.0D, 0.0D);
                    }
                }
                else
                {
                    if (!world.isRemote && var6 && !mat.isLiquid())
                    {
                        world.func_147480_a(x, y, z, true);
                    }

                    world.setBlock(x, y, z, this.isFull, 0, 3);
                }

                return true;
            }
        }
    }
	
	public Block getFluid()
	{
		return isFull;
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
        double var21 = (double)MAX_ENDER_BUCKET_DISTANCE;
        Vec3 var23 = var13.addVector((double)var18 * var21, (double)var17 * var21, (double)var20 * var21);
        return p_77621_1_.func_147447_a(var13, var23, p_77621_3_, !p_77621_3_, false);
    }
}