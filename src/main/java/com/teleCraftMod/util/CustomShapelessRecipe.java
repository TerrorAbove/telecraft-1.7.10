package com.teleCraftMod.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.World;

public class CustomShapelessRecipe extends ShapelessRecipes
{
	private int minDamage;
	
	public CustomShapelessRecipe(ItemStack output, List input)
	{
		this(output, input, 0);
	}
	
	public CustomShapelessRecipe(ItemStack output, List input, int minDamage)
	{
		super(output, input);
		this.minDamage = minDamage;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting c)
	{
		return CraftUtil.handleSpecialCrafting(c, super.getCraftingResult(c));
	}
	
	@Override
	public boolean matches(InventoryCrafting p_77569_1_, World p_77569_2_)
    {
        ArrayList var3 = new ArrayList(this.recipeItems);

        for (int var4 = 0; var4 < 3; ++var4)
        {
            for (int var5 = 0; var5 < 3; ++var5)
            {
                ItemStack var6 = p_77569_1_.getStackInRowAndColumn(var5, var4);

                if (var6 != null)
                {
                    boolean var7 = false;
                    Iterator var8 = var3.iterator();

                    while (var8.hasNext())
                    {
                        ItemStack var9 = (ItemStack)var8.next();
                        
                        boolean anyDamage = var9.getItemDamage() == 32767;
                        boolean minDamage = var6.getItemDamage() >= this.minDamage;
                        boolean damageEqual = var6.getItemDamage() == var9.getItemDamage();

                        if (var6.getItem() == var9.getItem() && ((anyDamage && minDamage) || damageEqual))
                        {
                            var7 = true;
                            var3.remove(var9);
                            break;
                        }
                    }

                    if (!var7)
                    {
                        return false;
                    }
                }
            }
        }

        return var3.isEmpty();
    }
	
	
	
	public static CustomShapelessRecipe getRecipeForItemDuplicates(ItemStack output, Item input, int numInputStacks)
	{
		List<ItemStack> list = new ArrayList<ItemStack>();
		
		for(int i = 0; i < numInputStacks; i++)
			list.add(new ItemStack(input));
		
		return new CustomShapelessRecipe(output, list);
	}
}
