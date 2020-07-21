package com.teleCraftMod.block;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.teleCraftMod.TeleCraft;
import com.teleCraftMod.packet.TeleporterPlatePacket;
import com.teleCraftMod.tileentity.TileEntityTelePlate;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBasePressurePlate;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPressurePlateTeleporter extends BlockBasePressurePlate implements ITileEntityProvider
{
	private IIcon clockwise;
    private IIcon counter_clockwise;
    
    private long lastTeleport;
    
	private String teleporterTextureName;
	
	private int teleportDistance;

	public BlockPressurePlateTeleporter(String str, Material m, int teleDist, float hardness)
	{
		super(str+"_up", m);
		teleportDistance = teleDist;
		teleporterTextureName = str;
		setHardness(hardness);
		setStepSound(soundTypePiston);
		setCreativeTab(CreativeTabs.tabTransport);
	}
	
	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
    {
        return new TileEntityTelePlate(teleportDistance);
    }
	
	@Override
	public boolean hasTileEntity(int metadata)
	{
		return true;
	}
	
	@Override
	protected int func_150065_e(World p_150065_1_, int p_150065_2_, int p_150065_3_, int p_150065_4_)
	{
		return 0;
	}
	
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity triggerEntity)
    {
        if (!world.isRemote)
        {
			if(System.currentTimeMillis() - lastTeleport > 500)
			{
				List onPlate = world.getEntitiesWithinAABB(Entity.class, getBB(x, y, z));

				if (onPlate != null && !onPlate.isEmpty())
				{
					Iterator iter = onPlate.iterator();
					
					int tpDist = teleportDistance;
					
					TileEntity te = world.getTileEntity(x, y, z);
					if(te instanceof TileEntityTelePlate)
					{
						tpDist = ((TileEntityTelePlate)te).getTeleDistance();
					}

					while (iter.hasNext())
					{
						Entity entity = (Entity)iter.next();
						
						boolean isItem = entity instanceof EntityItem;
						boolean isMob = entity instanceof EntityMob;
						boolean isPassive = entity instanceof EntityAnimal;//TODO may need to cover more cases for this one
						boolean isPlayer = entity instanceof EntityPlayer;
						
						boolean entityOk = (TeleCraft.allowTelePlateItems && isItem) || 
								(TeleCraft.allowTelePlateMobs && isMob) || 
								(TeleCraft.allowTelePlatePassives && isPassive) || 
								(TeleCraft.allowTelePlatePlayers && isPlayer);

						if (!entity.doesEntityNotTriggerPressurePlate() && entityOk)
						{
							final double INIT_Y = entity.posY;

							boolean isTeleportingDown = world.getBlockMetadata(x, y, z) != 0;
							double teleY = isTeleportingDown ? entity.posY-tpDist : entity.posY+tpDist;

							if(isTeleportingDown)//going down, so check if the new location will trap the player in blocks and check fall safety
							{
								while(teleY < INIT_Y && !safeToTeleport(world, x, (int)teleY, z))
								{
									teleY++;
								}
							}
							else//going up, so check if the new location will trap the player in blocks and check fall safety
							{
								while(teleY > INIT_Y && !safeToTeleport(world, x, (int)teleY, z))
								{
									teleY--;
								}
							}
							//remove chance of pointless teleport
							if(Math.abs(INIT_Y - teleY) > 1)
							{
								if(entity instanceof EntityPlayer)
								{
									EntityPlayer p = (EntityPlayer)entity;
									p.mountEntity(null);
								}
								
								if(entity instanceof EntityLivingBase)
								{
									EntityLivingBase elb = (EntityLivingBase)entity;
									elb.setPositionAndUpdate(x+0.5, teleY+0.25, z+0.5);
								}
								else
								{
									entity.setLocationAndAngles(x+0.5, teleY+0.25, z+0.5, entity.rotationYaw, entity.rotationPitch);
								}
							}
						}
					}
				}
				
				lastTeleport = System.currentTimeMillis();
			}
        }
    }
	
	public IIcon getIcon(int side, int meta)
    {
        return meta == 0 ? clockwise : counter_clockwise;
    }
	
	public void registerBlockIcons(IIconRegister p_149651_1_)
    {
        clockwise = p_149651_1_.registerIcon(teleporterTextureName + "_up");
        counter_clockwise = p_149651_1_.registerIcon(teleporterTextureName + "_down");
    }
	
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int something, float f0, float f1, float f2)
	{
		if(player.isSneaking())
		{
			TileEntity te = world.getTileEntity(x, y, z);
			
			if(!world.isRemote && te instanceof TileEntityTelePlate)
			{
				TileEntityTelePlate tetp = (TileEntityTelePlate)te;
				
				if(tetp.getTeleDistance() <= teleportDistance/2 + 1)
				{
					tetp.setTeleDistance(teleportDistance);
				}
				else
				{
					tetp.setTeleDistance(tetp.getTeleDistance()-1);
				}
				
				player.addChatComponentMessage(new ChatComponentText("Set teleport distance to a max of "+tetp.getTeleDistance()+"."));
				
				tetp.markChunkModified();
			}
		}
		else
		{
			int meta = world.getBlockMetadata(x, y, z);
			boolean upAndClockwise = meta == 0;
			world.setBlockMetadataWithNotify(x, y, z, upAndClockwise ? 2 : 0, 3);
		}
		
        return true;
    }
	
	public void setBlockBoundsBasedOnState(IBlockAccess p_149719_1_, int p_149719_2_, int p_149719_3_, int p_149719_4_)
    {
		float var3 = 0.0625F;
		this.setBlockBounds(var3, 0.0F, var3, 1.0F - var3, 0.03125F, 1.0F - var3);
    }
	
	public void func_150062_a(World p_150062_1_, int p_150062_2_, int p_150062_3_, int p_150062_4_, int p_150062_5_)
    {
        this.func_150065_e(p_150062_1_, p_150062_2_, p_150062_3_, p_150062_4_);
    }

	public int func_150066_d(int p_150066_1_)
    {
        return p_150066_1_ > 0 ? 1 : 0;
    }

    public int func_150060_c(int p_150060_1_)
    {
        return p_150060_1_ == 1 ? 15 : 0;
    }
	
	@Override
	public boolean canProvidePower()
	{
		return false;
	}
	
	
	
	private static AxisAlignedBB getBB(int x, int y, int z)
    {
        float var4 = 0.125F;
        return AxisAlignedBB.getBoundingBox((double)((float)x + var4), (double)y, (double)((float)z + var4), (double)((float)(x + 1) - var4), (double)y + 0.25D, (double)((float)(z + 1) - var4));
    }
	
	private static boolean safeToTeleport(World w, int x, int y, int z)
	{
		Block lowerBody = w.getBlock(x, y, z);
		Block upperBody = w.getBlock(x, y+1, z);
		Block ground = w.getBlock(x, y-1, z);
		
		boolean checkAir = lowerBody.getMaterial().equals(Material.air) && upperBody.getMaterial().equals(Material.air);
		
		//should we allow the player to be teleported underwater...?
		
		return checkAir && !ground.isBurning(w, x, y, z) && ground.getMaterial().blocksMovement();
	}
}
