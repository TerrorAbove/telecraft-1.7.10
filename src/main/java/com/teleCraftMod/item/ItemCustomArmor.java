package com.teleCraftMod.item;

import java.util.List;

import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.command.IEntitySelector;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemCustomArmor extends ItemArmor {

    /** The EnumArmorMaterial used for this ItemArmor */
    private IIcon emptySlotIcon;
    
    private boolean floats;

    public ItemCustomArmor(ItemArmor.ArmorMaterial mat, int render, int slot, String texture)
    {
    	super(mat, render, slot);
    	floats = mat == ArmorMaterial.valueOf("WOOD");
    	this.setTextureName(texture);
    }
    
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, int layer)
    {
    	if (stack.toString().toLowerCase().contains("preserver")) 
    	{
    		return "TeleCraft:life_preserver_layer_1.png";
    	}
    	if (stack.toString().toLowerCase().contains("fortitude"))
    	{
    		return "TeleCraft:armor_of_fortitude_layer_1.png";
    	}
    	//more to be added
    	return null;
    }

    public int getColorFromItemStack(ItemStack p_82790_1_, int p_82790_2_)
    {
        if (p_82790_2_ > 0)
        {
            return 16777215;
        }
        else
        {
            int var3 = this.getColor(p_82790_1_);

            if (var3 < 0)
            {
                var3 = 16777215;
            }

            return var3;
        }
    }

    public boolean requiresMultipleRenderPasses()
    {
        return false;
    }
    
    public boolean floats()
    {
    	return floats;
    }

    /**
     * Return whether the specified armor ItemStack has a color.
     */
    public boolean hasColor(ItemStack p_82816_1_)
    {
        return false;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack p_77659_1_, World p_77659_2_, EntityPlayer p_77659_3_)
    {
    	ItemStack stack = super.onItemRightClick(p_77659_1_, p_77659_2_, p_77659_3_);
    	//stuff here
        return stack;
    }

    public static IIcon func_94602_b(int p_94602_0_)
    {
        return ItemArmor.func_94602_b(p_94602_0_);
    }
    
    public void onUpdate(ItemStack stack, World world, Entity entity, int someInt, boolean someBool)
    {
    	super.onUpdate(stack, world, entity, someInt, someBool);
    }
}
