package com.teleCraftMod.block;

import java.util.ArrayList;
import java.util.HashMap;

import com.teleCraftMod.tileentity.TileEntityRiggedChest;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEventData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;

public class BlockChestRigged extends BlockChest
{
	private IIcon singleChest;//TODO fix textures
	private IIcon doubleChest;
	
	private String blockChestRiggedTextureName;
	
	public BlockChestRigged(String textureName)
	{
		super(0);
		setHardness(2.5F);
		setStepSound(soundTypeWood);
		this.blockChestRiggedTextureName = textureName;
	}
	
	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
    {
        TileEntityRiggedChest tileentitychest = new TileEntityRiggedChest();
        return tileentitychest;
    }
	
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int int_6, float ft_7, float ft_8, float ft_9)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		
		if(te instanceof TileEntityRiggedChest)
		{
			TileEntityRiggedChest rc = (TileEntityRiggedChest)te;
			String owner = rc.getOwner();
			
			if(owner != null && player instanceof EntityPlayerMP && !rc.isUseableByPlayer(player))
			{
	        	EntityPlayerMP playerMP = (EntityPlayerMP)player;
	        	playerMP.addChatMessage(new ChatComponentText("A powerful magic prevents you from opening "+owner+"'s chest!"));
	        	return true;
			}
		}
		super.onBlockActivated(world, x, y, z, player, int_6, ft_7, ft_8, ft_9);
		return true;
	}
	
	/*public IIcon getIcon(int side, int meta)
    {
		return singleChest;
    }*/
	
	/*public IIcon getIcon(IBlockAccess access, int a, int b, int c, int d)
	{
		System.out.println(a);
		System.out.println(b);
		System.out.println(c);
		System.out.println(d);
		//access.getTileEntity(a, b, c)
		return doubleChest;
		
	}*/
	
	public void registerBlockIcons(IIconRegister r)
    {
        super.registerBlockIcons(r);
        
        //this.blockIcon = r.registerIcon(blockChestRiggedTextureName);
        //singleChest = r.registerIcon(blockChestRiggedTextureName);
        //doubleChest = r.registerIcon(blockChestRiggedTextureName + "_double");
    }
}
