package com.teleCraftMod.item;

import com.teleCraftMod.TeleCraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EnderBucketMilk extends ItemBucketMilk
{
	public EnderBucketMilk()
	{
		super();
	}
	
	public ItemStack onEaten(ItemStack p_77654_1_, World p_77654_2_, EntityPlayer p_77654_3_)
    {
        if (!p_77654_3_.capabilities.isCreativeMode)
        {
            --p_77654_1_.stackSize;
        }

        if (!p_77654_2_.isRemote)
        {
            p_77654_3_.clearActivePotions();
        }

        return p_77654_1_.stackSize <= 0 ? new ItemStack(TeleCraft.enderBucketEmpty) : p_77654_1_;
    }
}
