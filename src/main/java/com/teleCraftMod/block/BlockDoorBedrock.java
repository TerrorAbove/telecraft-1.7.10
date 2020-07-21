package com.teleCraftMod.block;

import com.teleCraftMod.TeleCraft;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class BlockDoorBedrock extends BlockDoor
{
	public BlockDoorBedrock()
	{
		this(Material.rock);
	}
	
	protected BlockDoorBedrock(Material mat)
	{
		super(mat);
		this.setBlockUnbreakable();
		this.setResistance(6000000.0F);
		this.disableStats();
	}
	
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int i, float f, float f2, float f3)
    {
		return true;
    }
	
	public Item getItem(World world, int x, int y, int z)
    {
		return TeleCraft.bedrock_door_item;
    }
	
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
	{
		int m = world.getBlockMetadata(x, y, z);

		if ((m & 8) == 0)
		{
			int facing = m & 3;
			
			int dx = 0, dz = 0;
			
			switch(facing)
			{
			case 0:
				dx = 1;
				dz = 0;
				break;
			case 1:
				dx = 0;
				dz = 1;
				break;
			case 2:
				dx = -1;
				dz = 0;
				break;
			case 3:
				dx = 0;
				dz = -1;
			}
			
			int newDir = (facing+2)%4;
			
			if(newDir%2 == 0)
				newDir = 1;
			
			int power = world.getIndirectPowerLevelTo(x+dx, y, z+dz, newDir);
			int power1 = world.getIndirectPowerLevelTo(x+dx, y+1, z+dz, newDir);
			
			int dirPower = world.isBlockProvidingPowerTo(x+dx, y, z+dz, newDir);
			int dirPower1 = world.isBlockProvidingPowerTo(x+dx, y+1, z+dz, newDir);
			
			boolean var7 = false;

			if (world.getBlock(x, y + 1, z) != this)
			{
				world.setBlockToAir(x, y, z);
				var7 = true;
			}

			if (!World.doesBlockHaveSolidTopSurface(world, x, y - 1, z))
			{
				world.setBlockToAir(x, y, z);
				var7 = true;
			}

			if (!var7)
			{
				boolean indirect = power > 0 || power1 > 0;
				boolean direct = dirPower > 0 || dirPower1 > 0;
				
				boolean powered = indirect || direct;

				this.func_150014_a(world, x, y, z, powered);
			}
		}
		else
		{
			if (world.getBlock(x, y - 1, z) != this)
			{
				world.setBlockToAir(x, y, z);
			}

			if (block != this)
			{
				this.onNeighborBlockChange(world, x, y - 1, z, block);
			}
		}
	}
	
	public void func_150014_a(World p_150014_1_, int p_150014_2_, int p_150014_3_, int p_150014_4_, boolean p_150014_5_)
	{
		int l = this.func_150012_g(p_150014_1_, p_150014_2_, p_150014_3_, p_150014_4_);
		boolean flag1 = (l & 4) != 0;

		if (flag1 != p_150014_5_)
		{
			int i1 = l & 7;
			i1 ^= 4;

			if ((l & 8) == 0)
			{
				p_150014_1_.setBlockMetadataWithNotify(p_150014_2_, p_150014_3_, p_150014_4_, i1, 2);
				p_150014_1_.markBlockRangeForRenderUpdate(p_150014_2_, p_150014_3_, p_150014_4_, p_150014_2_, p_150014_3_, p_150014_4_);
			}
			else
			{
				p_150014_1_.setBlockMetadataWithNotify(p_150014_2_, p_150014_3_ - 1, p_150014_4_, i1, 2);
				p_150014_1_.markBlockRangeForRenderUpdate(p_150014_2_, p_150014_3_ - 1, p_150014_4_, p_150014_2_, p_150014_3_, p_150014_4_);
			}

			p_150014_1_.playAuxSFXAtEntity((EntityPlayer)null, 1003, p_150014_2_, p_150014_3_, p_150014_4_, 0);
		}
	}
}
