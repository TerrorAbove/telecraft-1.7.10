package com.teleCraftMod.block;

import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.material.Material;

public class BlockPressurePlateBedrock extends BlockPressurePlate
{
	public BlockPressurePlateBedrock()
	{
		this("bedrock", Material.rock, BlockPressurePlate.Sensitivity.players);
	}
	
	protected BlockPressurePlateBedrock(String textureName, Material mat, Sensitivity sens)
	{
		super(textureName, mat, sens);
		this.setBlockUnbreakable();
		this.setResistance(6000000.0F);
		this.disableStats();
	}
}
