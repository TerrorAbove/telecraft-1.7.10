package com.teleCraftMod.item;

import com.teleCraftMod.TeleCraft;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BucketExchanger extends Item
{
	private byte type;
	private IIcon[] icons;
	
	public BucketExchanger(byte type)//0 = water, 1 = lava, 2 = milk
	{
		super();
		
		this.setMaxStackSize(1);
		this.setCreativeTab(CreativeTabs.tabMisc);
		
		this.type = type;
		this.icons = new IIcon[3];
	}
	
	@Override
	public void registerIcons(IIconRegister reg)
	{
		icons[0] = reg.registerIcon("telecraft:fluid_exchanger_water");
		icons[1] = reg.registerIcon("telecraft:fluid_exchanger_lava");
		//icons[2] = reg.registerIcon("telecraft:fluid_exchanger_milk");
	}
	
	public IIcon getIcon(ItemStack stack, int a)
	{
		return getIconIndex(stack);
	}
	
	public IIcon getIconIndex(ItemStack stack)
	{
		return icons[type];
	}
	
	public ItemStack onItemRightClick(ItemStack stack, World w, EntityPlayer p)
	{
		Item superBucket = null;
		Item regularBucket = null;
		
		switch(type)
		{
		case 0:
			superBucket = TeleCraft.superBucketWater;
			regularBucket = Items.water_bucket;
			break;
		case 1:
			superBucket = TeleCraft.superBucketLava;
			regularBucket = Items.lava_bucket;
			break;
		case 2:
			superBucket = TeleCraft.superBucketMilk;
			regularBucket = Items.milk_bucket;
			break;
		}
		
		if(p != null && p.inventory != null)
		{
			int numFilledBuckets = 0;
			int numEmptyBuckets = 0;
			int numFilledSuperBuckets = 0;
			int totalAmountSuperBucketLiquid = 0;
			int numFreeSpaces = 0;
			
			for(int i = 0; i < p.inventory.mainInventory.length; i++)
			{
				ItemStack curr = p.inventory.mainInventory[i];
				if(curr != null)
				{
					if(curr.getItem() == Items.bucket)
					{
						numEmptyBuckets += curr.stackSize;
					}
					else if(curr.getItem() == regularBucket)
					{
						numFilledBuckets++;
					}
					else if(curr.getItem() == superBucket)
					{
						numFilledSuperBuckets++;
						
						if(curr.getTagCompound() != null)
						{
							int liquidAmount = curr.getTagCompound().getInteger("liquidAmount");
							
							if(liquidAmount == 0)
								liquidAmount = 9;
							
							totalAmountSuperBucketLiquid += liquidAmount;
						}
						else
						{
							totalAmountSuperBucketLiquid += 9;
						}
					}
				}
				else
				{
					numFreeSpaces++;
				}
			}
			
			int numSuperBucketsToGive = (numFilledBuckets + numEmptyBuckets) / 9;
			
			if(numSuperBucketsToGive >= 1 && numFilledBuckets >= numSuperBucketsToGive)
			{
				int numStacks = numFilledBuckets / 9;
				int remainder = numFilledBuckets % 9;
				
				int i = numFilledBuckets;
				int i2 = remainder == 0 ? 0 : 9 - remainder;
				
				if(i2 > numEmptyBuckets)
					return stack;

				for(int j = 0; j < p.inventory.mainInventory.length; j++)
				{
					ItemStack curr = p.inventory.mainInventory[j];
					if(curr != null)
					{
						if(i > 0 && curr.getItem() == regularBucket)
						{
							p.inventory.mainInventory[j] = null;
							i--;
						}
						else if(i2 > 0 && curr.getItem() == Items.bucket)
						{
							p.inventory.mainInventory[j] = null;
							i2--;
						}
					}
					
					if(i == 0 && i2 == 0)
						break;
				}
				
				if(i == 0 && i2 == 0 && superBucket != null)
				{
					for(int k = 0; k < numStacks; k++)
					{
						p.inventory.addItemStackToInventory(new ItemStack(superBucket));
					}
					
					if(remainder > 0)
					{
						ItemStack r = new ItemStack(superBucket);
						
						NBTTagCompound c = r.getTagCompound();
						
						if(c == null)
							c = new NBTTagCompound();
						
						c.setInteger("liquidAmount", remainder);
						r.setTagCompound(c);
						
						p.inventory.addItemStackToInventory(r);
					}
				}
			}
			else//try to take super bucket and give 9 regular buckets of the liquid
			{
				if(numFilledSuperBuckets > 0)
				{
					int i = numFilledSuperBuckets;
					
					for(int j = 0; j < p.inventory.mainInventory.length; j++)
					{
						ItemStack curr = p.inventory.mainInventory[j];
						if(curr != null)
						{
							int liquidAmount = 0;
							
							NBTTagCompound c = curr.getTagCompound();
							
							if(c != null)
								liquidAmount = c.getInteger("liquidAmount");
							
							if(liquidAmount == 0)
								liquidAmount = 9;
							
							if(i > 0 && curr.getItem() == superBucket && numFreeSpaces >= 9)
							{
								p.inventory.mainInventory[j] = null;
								
								for(int k = 0; k < liquidAmount; k++)
								{
									if(p.inventory.addItemStackToInventory(new ItemStack(regularBucket)))
										numFreeSpaces--;
								}
								for(int k = 0; k < 9-liquidAmount; k++)
								{
									if(p.inventory.addItemStackToInventory(new ItemStack(Items.bucket)))
										numFreeSpaces--;
								}
								
								i--;
							}
						}
						
						if(i == 0)
							break;
					}
				}
			}
		}
		
		return stack;
	}
}
