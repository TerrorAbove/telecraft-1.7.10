package com.teleCraftMod.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.teleCraftMod.TeleCraft;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemSuperBucket extends ItemBucket
{
	private static final int MAX_LIQUID_AMOUNT = 9;
	
	private IIcon empty;
	private IIcon[] water_icons;
	private IIcon[] lava_icons;
	
	private IIcon ender_empty;
	private IIcon[] ender_water_icons;
	private IIcon[] ender_lava_icons;
	
	private Block isFull;
	
	private boolean isEnder;
	
	public ItemSuperBucket(Block b, boolean isEnder)
	{
		super(b);
		
		water_icons = new IIcon[MAX_LIQUID_AMOUNT];
		lava_icons = new IIcon[MAX_LIQUID_AMOUNT];
		
		ender_water_icons = new IIcon[MAX_LIQUID_AMOUNT];
		ender_lava_icons = new IIcon[MAX_LIQUID_AMOUNT];
		
		this.isFull = b;
		this.isEnder = isEnder;
	}
	
	@Override
	public void registerIcons(IIconRegister reg)
	{
		empty = reg.registerIcon("telecraft:super_bucket/super_bucket_empty");
		ender_empty = reg.registerIcon("telecraft:super_bucket/super_ender_bucket_empty");
		
		for(int i = 0; i < MAX_LIQUID_AMOUNT; i++)
		{
			water_icons[i] = reg.registerIcon("telecraft:super_bucket/super_bucket_water"+(i+1));
			lava_icons[i] = reg.registerIcon("telecraft:super_bucket/super_bucket_lava"+(i+1));
			
			ender_water_icons[i] = reg.registerIcon("telecraft:super_bucket/super_ender_bucket_water"+(i+1));
			ender_lava_icons[i] = reg.registerIcon("telecraft:super_bucket/super_ender_bucket_lava"+(i+1));
		}
	}
	
	public IIcon getIcon(ItemStack stack, int a)
	{
		return getIconIndex(stack);
	}
	
	public IIcon getIconIndex(ItemStack stack)
	{
		int liquidAmount = getLiquidAmount(stack);
		
		if(isFull == Blocks.flowing_water)
			return isEnder ? ender_water_icons[liquidAmount-1] : water_icons[liquidAmount-1];
		
		if(isFull == Blocks.flowing_lava)
			return isEnder ? ender_lava_icons[liquidAmount-1] : lava_icons[liquidAmount-1];
		
		//if neither water nor lava, we assume it's empty
		return isEnder ? ender_empty : empty;
	}
	
	/*public boolean doesContainerItemLeaveCraftingGrid(ItemStack stack)
    {
        return false;
    }*/
	
	protected int getLiquidAmount(ItemStack stack)
	{
		if(stack == null || !(stack.getItem() instanceof ItemSuperBucket))
			return 0;
		
		NBTTagCompound c = stack.getTagCompound();
		
		if(c == null)
			c = new NBTTagCompound();
		
		int liquidAmount = c.getInteger("liquidAmount");
		
		liquidAmount = Math.max(0, Math.min(MAX_LIQUID_AMOUNT, liquidAmount));
		
		if(liquidAmount == 0)
			liquidAmount = MAX_LIQUID_AMOUNT;
		
		return liquidAmount;
	}
	
	@Override
	public boolean onEntitySwing(EntityLivingBase elb, ItemStack stack)
	{
		//sneak left click to empty bucket
		if(elb instanceof EntityPlayer && elb.isSneaking() && getLiquidAmount(stack) > 0)
		{
			EntityPlayer p = (EntityPlayer)elb;
			p.setCurrentItemOrArmor(0, addNewBucket(stack, p, TeleCraft.superBucketEmpty, 0));
			return true;
		}
		
		return super.onEntitySwing(elb, stack);
	}
	
	/**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
	public ItemStack onItemRightClick(ItemStack stack, World w, EntityPlayer p)
	{
		boolean empty = this.isFull == Blocks.air;
		boolean hasWater = this.isFull == Blocks.flowing_water;
		boolean hasLava = this.isFull == Blocks.flowing_lava;
		
		NBTTagCompound c = stack.getTagCompound();
		
		if(c == null)
			c = new NBTTagCompound();
		
		int liquidAmount = c.getInteger("liquidAmount");
		
		if(liquidAmount == 0)
			liquidAmount = MAX_LIQUID_AMOUNT;

		MovingObjectPosition mopLiquids = this.getMovingObjectPositionFromPlayer(w, p, true);
		MovingObjectPosition mopSolids = this.getMovingObjectPositionFromPlayer(w, p, false);

		if (mopLiquids == null)
		{
			return stack;
		}
		else
		{
			if (mopLiquids.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
			{
				int var6 = mopLiquids.blockX;
				int var7 = mopLiquids.blockY;
				int var8 = mopLiquids.blockZ;

				if (!w.canMineBlock(p, var6, var7, var8))
				{
					return stack;
				}

				Material var9 = w.getBlock(var6, var7, var8).getMaterial();
				int var10 = w.getBlockMetadata(var6, var7, var8);

				boolean isWater = var9 == Material.water && var10 == 0;
				boolean isLava = var9 == Material.lava && var10 == 0;

				if (empty)
				{
					Block selectedBlock = w.getBlock(var6, var7, var8);
					
					p.setCurrentItemOrArmor(0, new ItemStack(Items.bucket));
					
					int max_dist = p.capabilities.isCreativeMode ? 8 : 6;
					
					//if the selected block does something special on activation...
					if(p.getDistance(var6, var7, var8) <= max_dist && selectedBlock.onBlockActivated(w, var6, var7, var8, p, var10, (float)(mopLiquids.hitVec.xCoord % 1.0), (float)(mopLiquids.hitVec.yCoord % 1.0), (float)(mopLiquids.hitVec.zCoord % 1.0)))//last 3 floats are block-relative coords where you clicked on the block
					{
						ItemStack curr = p.getHeldItem();
						
						if(curr != null)
						{
							//if(curr.getItem() == Items.milk_bucket)
								//toGive = new ItemStack(TeleCraft.superBucketMilk);
							if(curr.getItem() == Items.bucket)
								return this.addNewBucket(stack, p, TeleCraft.superBucketEmpty, 0);
							
							if(curr.getItem() == Items.water_bucket)
								return this.addNewBucket(stack, p, TeleCraft.superBucketWater, 1);
							
							if(curr.getItem() == Items.lava_bucket)
								return this.addNewBucket(stack, p, TeleCraft.superBucketLava, 1);
						}
						
						p.setCurrentItemOrArmor(0, stack);
						return stack;
					}
					
					p.setCurrentItemOrArmor(0, stack);
					
					if (!p.canPlayerEdit(var6, var7, var8, mopLiquids.sideHit, stack))
					{
						return stack;
					}

					if (isWater)
					{
						if(!p.isSneaking() && tryTakeThreeByThree(stack, w, p, var6, var7, var8, 0, mopLiquids.sideHit, var9))
							return this.addNewBucket(stack, p, TeleCraft.superBucketWater, 9);
						
						return this.addNewBucket(stack, p, TeleCraft.superBucketWater, takeLiquidRecursive(stack, w, p, var6, var7, var8, 0, mopLiquids.sideHit, var9, p.isSneaking()));
					}

					if (isLava)
					{
						if(!p.isSneaking() && tryTakeThreeByThree(stack, w, p, var6, var7, var8, 0, mopLiquids.sideHit, var9))
							return this.addNewBucket(stack, p, TeleCraft.superBucketLava, 9);
						
						return this.addNewBucket(stack, p, TeleCraft.superBucketLava, takeLiquidRecursive(stack, w, p, var6, var7, var8, 0, mopLiquids.sideHit, var9, p.isSneaking()));
					}
				}
				else if(hasWater && isWater && liquidAmount < MAX_LIQUID_AMOUNT)
				{
					return this.addNewBucket(stack, p, TeleCraft.superBucketWater, takeLiquidRecursive(stack, w, p, var6, var7, var8, liquidAmount, mopLiquids.sideHit, var9, p.isSneaking()));
				}
				else if(hasLava && isLava && liquidAmount < MAX_LIQUID_AMOUNT)
				{
					return this.addNewBucket(stack, p, TeleCraft.superBucketLava, takeLiquidRecursive(stack, w, p, var6, var7, var8, liquidAmount, mopLiquids.sideHit, var9, p.isSneaking()));
				}
				else
				{
					if(mopSolids == null || mopSolids.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)
					{
						return stack;
					}
					
					var6 = mopSolids.blockX;
					var7 = mopSolids.blockY;
					var8 = mopSolids.blockZ;
					
					if (this.isFull == Blocks.air)
					{
						return new ItemStack(isEnder ? TeleCraft.superEnderBucketEmpty : TeleCraft.superBucketEmpty);
					}
					
					//used for onBlockActivated, computed before we adjust for sideHit
					Block selectedBlock = w.getBlock(var6, var7, var8);
					int meta = w.getBlockMetadata(var6, var7, var8);
					
					//sneak to take liquids, otherwise we will assume the user wants to place liquids into the block
					boolean takingLiquids = p.isSneaking() && liquidAmount < MAX_LIQUID_AMOUNT;
					
					//if(stack.getItem() == )//TODO milk case..?
						//p.setCurrentItemOrArmor(0, new ItemStack(Items.milk_bucket));
					if(hasWater)
						p.setCurrentItemOrArmor(0, new ItemStack(takingLiquids ? Items.bucket : Items.water_bucket));
					else if(hasLava)
						p.setCurrentItemOrArmor(0, new ItemStack(takingLiquids ? Items.bucket : Items.lava_bucket));
					
					int max_dist = p.capabilities.isCreativeMode ? 8 : 6;
					
					//if the selected block does something special on activation...
					if(p.getDistance(var6, var7, var8) <= max_dist && selectedBlock.onBlockActivated(w, var6, var7, var8, p, meta, (float)(mopSolids.hitVec.xCoord % 1.0), (float)(mopSolids.hitVec.yCoord % 1.0), (float)(mopSolids.hitVec.zCoord % 1.0)))//last 3 floats are block-relative coords where you clicked on the block
					{
						ItemStack curr = p.getHeldItem();
						
						if(curr != null)
						{
							if(curr.getItem() == Items.bucket && !takingLiquids)//placed a block of water or lava into the block, so decrement liquidAmount
								return this.addNewBucket(stack, p, hasWater ? TeleCraft.superBucketWater : TeleCraft.superBucketLava, liquidAmount-1);
							
							if(curr.getItem() == Items.water_bucket && takingLiquids)//took a block of water
								return this.addNewBucket(stack, p, TeleCraft.superBucketWater, liquidAmount+1);
							
							if(curr.getItem() == Items.lava_bucket && takingLiquids)//took a block of lava
								return this.addNewBucket(stack, p, TeleCraft.superBucketLava, liquidAmount+1);
						}
						
						p.setCurrentItemOrArmor(0, stack);
						return stack;
					}
					
					p.setCurrentItemOrArmor(0, stack);
					
					if (mopSolids.sideHit == 0)
					{
						--var7;
					}

					if (mopSolids.sideHit == 1)
					{
						++var7;
					}

					if (mopSolids.sideHit == 2)
					{
						--var8;
					}

					if (mopSolids.sideHit == 3)
					{
						++var8;
					}

					if (mopSolids.sideHit == 4)
					{
						--var6;
					}

					if (mopSolids.sideHit == 5)
					{
						++var6;
					}

					if (!p.canPlayerEdit(var6, var7, var8, mopSolids.sideHit, stack))
					{
						return stack;
					}
					
					if(!p.isSneaking() && tryPlaceThreeByThree(w, p, var6, var7, var8, liquidAmount))
					{
						return this.addNewBucket(stack, p, TeleCraft.superBucketEmpty, 0);
					}
					
					if(hasWater)
						return this.addNewBucket(stack, p, TeleCraft.superBucketWater, placeLiquidRecursive(w, p, var6, var7, var8, liquidAmount, p.isSneaking()));

					if(hasLava)
						return this.addNewBucket(stack, p, TeleCraft.superBucketLava, placeLiquidRecursive(w, p, var6, var7, var8, liquidAmount, p.isSneaking()));
				}
			}

			return stack;
		}
	}
	
	private boolean tryPlaceThreeByThree(World w, EntityPlayer p, int x, int y, int z, int liquidAmount)
	{
		if(liquidAmount < MAX_LIQUID_AMOUNT)
			return false;
		
		for(int i = -1; i <= 1; i++)
		{
			for(int j = -1; j <= 1; j++)
			{
				if(!checkBlock(w, x+i, y, z+j) || !w.isSideSolid(x+i, y-1, z+j, ForgeDirection.UP))
				{
					return false;
				}
			}
		}
		
		for(int i = -1; i <= 1; i++)
		{
			for(int j = -1; j <= 1; j++)
			{
				tryPlaceContainedLiquid(w, x+i, y, z+j, false);
			}
		}
		
		return true;
	}
	
	private int placeLiquidRecursive(World w, EntityPlayer p, int x, int y, int z, int liquidAmount, boolean justPlaceOne)
	{
		ArrayList<Location> locs = new ArrayList<Location>();
		return placeLiquidRecursive(w, p, x, y, z, liquidAmount, justPlaceOne, locs);
	}
	
	private int placeLiquidRecursive(World w, EntityPlayer p, int x, int y, int z, int liquidAmount, boolean justPlaceOne, List<Location> alreadyPlaced)
	{
		if(liquidAmount <= 0)
			return 0;
		
		if(!justPlaceOne && !w.isSideSolid(x, y-1, z, ForgeDirection.UP))
			return liquidAmount;
		
		Location newLoc = new Location(x, y, z);
		
		if(!checkBlock(w, x, y, z) || alreadyPlaced.contains(newLoc))
			return liquidAmount;
		
		tryPlaceContainedLiquid(w, x, y, z, false);
		alreadyPlaced.add(newLoc);
		liquidAmount--;
		
		if(justPlaceOne)
			return liquidAmount;
		
		liquidAmount = placeLiquidRecursive(w, p, x+1, y, z, liquidAmount, false, alreadyPlaced);
		liquidAmount = placeLiquidRecursive(w, p, x, y, z+1, liquidAmount, false, alreadyPlaced);
		liquidAmount = placeLiquidRecursive(w, p, x-1, y, z, liquidAmount, false, alreadyPlaced);
		liquidAmount = placeLiquidRecursive(w, p, x, y, z-1, liquidAmount, false, alreadyPlaced);
		
		return liquidAmount;
	}
	
	private boolean tryTakeThreeByThree(ItemStack stack, World w, EntityPlayer p, int x, int y, int z, int liquidAmount, int sideHit, Material lookingFor)
	{
		if(liquidAmount > 0)
			return false;
		
		for(int i = -1; i <= 1; i++)
		{
			for(int j = -1; j <= 1; j++)
			{
				if (!p.canPlayerEdit(x+i, y, z+j, sideHit, stack))
				{
					return false;
				}
				
				Material var9 = w.getBlock(x+i, y, z+j).getMaterial();
				int var10 = w.getBlockMetadata(x+i, y, z+j);
				
				//fluid mismatch, stop here
				if(lookingFor != var9 || var10 != 0)
				{
					return false;
				}
			}
		}
		
		for(int i = -1; i <= 1; i++)
		{
			for(int j = -1; j <= 1; j++)
			{
				w.setBlockToAir(x+i, y, z+j);
			}
		}
		
		return true;
	}
	private int takeLiquidRecursive(ItemStack stack, World w, EntityPlayer p, int x, int y, int z, int liquidAmount, int sideHit, Material lookingFor, boolean justTakeOne)
	{
		if(liquidAmount >= MAX_LIQUID_AMOUNT)
			return MAX_LIQUID_AMOUNT;
		
		if (!p.canPlayerEdit(x, y, z, sideHit, stack))
		{
			return liquidAmount;
		}
		
		Material var9 = w.getBlock(x, y, z).getMaterial();
		int var10 = w.getBlockMetadata(x, y, z);
		
		//fluid mismatch, stop here
		if(lookingFor != var9 || var10 != 0)
		{
			return liquidAmount;
		}
		
		liquidAmount++;
		w.setBlockToAir(x, y, z);
		
		if(justTakeOne)
			return liquidAmount;
		
		liquidAmount = takeLiquidRecursive(stack, w, p, x+1, y, z, liquidAmount, sideHit, lookingFor, false);
		liquidAmount = takeLiquidRecursive(stack, w, p, x, y, z+1, liquidAmount, sideHit, lookingFor, false);
		liquidAmount = takeLiquidRecursive(stack, w, p, x-1, y, z, liquidAmount, sideHit, lookingFor, false);
		liquidAmount = takeLiquidRecursive(stack, w, p, x, y, z-1, liquidAmount, sideHit, lookingFor, false);
		
		return liquidAmount;
	}

	private ItemStack addNewBucket(ItemStack original, EntityPlayer p, Item newItem, int newFluidAmount)
	{
		if (p.capabilities.isCreativeMode)
			return original;
		
		ItemStack toGive = stackToGive(newItem, newFluidAmount);

		if (--original.stackSize <= 0)
			return toGive;

		if (!p.inventory.addItemStackToInventory(toGive))
			p.dropPlayerItemWithRandomChoice(toGive, false);

		return original;
	}
	
	private ItemStack stackToGive(Item newItem, int newFluidAmount)
	{
		if(newFluidAmount == 0)
			newItem = isEnder ? TeleCraft.superEnderBucketEmpty : TeleCraft.superBucketEmpty;
		else if(isEnder)//bucket has some liquid, replace non-ender with ender version
		{
			if(newItem == TeleCraft.superBucketWater)
				newItem = TeleCraft.superEnderBucketWater;
			else if(newItem == TeleCraft.superBucketLava)
				newItem = TeleCraft.superEnderBucketLava;
			else if(newItem == TeleCraft.superBucketMilk)
				newItem = TeleCraft.superEnderBucketMilk;
		}
		
		ItemStack newStack = new ItemStack(newItem);

		NBTTagCompound c = newStack.getTagCompound();

		if(c == null)
			c = new NBTTagCompound();

		c.setInteger("liquidAmount", newFluidAmount);
		
		if(newFluidAmount > 0)
			newStack.setTagCompound(c);
		
		return newStack;
	}

	/**
	 * Attempts to place the liquid contained inside the bucket.
	 */
	public boolean tryPlaceContainedLiquid(World w, int x, int y, int z)
	{
		return tryPlaceContainedLiquid(w, x, y, z, false);
	}
	public boolean tryPlaceContainedLiquid(World w, int x, int y, int z, boolean replaceAirOnly)
	{
		if (this.isFull == Blocks.air)
		{
			return false;
		}
		else
		{
			Material var5 = w.getBlock(x, y, z).getMaterial();
			boolean solid = var5.isSolid();

			if (solid || (replaceAirOnly && !w.isAirBlock(x, y, z)))
			{
				return false;
			}
			else
			{
				if (w.provider.isHellWorld && this.isFull == Blocks.flowing_water)
				{
					w.playSoundEffect((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), "random.fizz", 0.5F, 2.6F + (w.rand.nextFloat() - w.rand.nextFloat()) * 0.8F);

					for (int var7 = 0; var7 < 8; ++var7)
					{
						w.spawnParticle("largesmoke", (double)x + Math.random(), (double)y + Math.random(), (double)z + Math.random(), 0.0D, 0.0D, 0.0D);
					}
				}
				else
				{
					if (!w.isRemote && !var5.isLiquid())
					{
						w.func_147480_a(x, y, z, true);//if not air, set the block to air (plays sound effect)
					}

					w.setBlock(x, y, z, this.isFull, 0, 3);
				}

				return true;
			}
		}
	}
	
	private boolean checkBlock(World w, int x, int y, int z)
	{
		if (this.isFull == Blocks.air)
		{
			return false;
		}
		else
		{
			Material var5 = w.getBlock(x, y, z).getMaterial();
			boolean solid = var5.isSolid();

			if (solid)
			{
				return false;
			}
		}
		
		return true;
	}
    
    public String getItemStackDisplayName(ItemStack stack)
    {
    	String result = super.getItemStackDisplayName(stack);
    	
    	if(stack != null && this.isFull != Blocks.air)
    	{
    		NBTTagCompound c = stack.getTagCompound();
    		
    		if(c != null)
    		{
    			int liquidAmount = c.getInteger("liquidAmount");
    			
    			if(liquidAmount > 0)
    				return result.substring(0, result.length()-1) + " x"+liquidAmount+")";
    		}
    		return result.substring(0, result.length()-1) + " x9)";
    	}
    	return result;
    }
    
    @Override
    public boolean getShareTag()
    {
    	return true;
    }
    
    @Override
    protected MovingObjectPosition getMovingObjectPositionFromPlayer(World p_77621_1_, EntityPlayer p_77621_2_, boolean p_77621_3_)
    {
    	if(!isEnder)
    		return super.getMovingObjectPositionFromPlayer(p_77621_1_, p_77621_2_, p_77621_3_);
    	
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
        double var21 = (double)EnderBucket.MAX_ENDER_BUCKET_DISTANCE;
        Vec3 var23 = var13.addVector((double)var18 * var21, (double)var17 * var21, (double)var20 * var21);
        return p_77621_1_.func_147447_a(var13, var23, p_77621_3_, !p_77621_3_, false);
    }
    
    private static class Location
    {
    	public int x, y, z;
    	
    	public Location(int x, int y, int z)
    	{
    		this.x = x;
    		this.y = y;
    		this.z = z;
    	}
    	
    	public boolean equals(Object o)
    	{
    		if(o instanceof Location)
    		{
    			Location loc = (Location)o;
    			
    			return this.x == loc.x && this.y == loc.y && this.z == loc.z;
    		}
    		return false;
    	}
    }
}
