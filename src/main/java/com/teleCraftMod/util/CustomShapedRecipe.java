package com.teleCraftMod.util;

import com.teleCraftMod.TeleCraft;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.world.World;

public class CustomShapedRecipe extends ShapedRecipes
{
	public CustomShapedRecipe(int width, int height, ItemStack[] input, ItemStack output)
	{
		super(width, height, input, output);
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting c)
	{
		return CraftUtil.handleSpecialCrafting(c, super.getCraftingResult(c));
	}
}
