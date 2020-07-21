package teleCraftMod;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBasePressurePlate;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPressurePlateTeleporter extends BlockBasePressurePlate {
	
	private IIcon clockwise;
    private IIcon counter_clockwise;
    
    private HashMap<String, Long> lastUsed;
    
	private String teleporterTextureName;
	private int teleportDistance;

	public BlockPressurePlateTeleporter(String str, Material m, int teleDist, float hardness)
	{
		super(str+"_up", m);
		teleportDistance = teleDist;
		teleporterTextureName = str;
		setHardness(hardness);
		lastUsed = new HashMap<String, Long>();
		setStepSound(soundTypePiston);
	}

	@Override
	public int func_150065_e(World p_150065_1_, int p_150065_2_, int p_150065_3_, int p_150065_4_)
    {	
		List var5 = p_150065_1_.getEntitiesWithinAABB(EntityPlayer.class, this.func_150061_a(p_150065_2_, p_150065_3_, p_150065_4_));

        if (var5 != null && !var5.isEmpty())
        {
            Iterator var6 = var5.iterator();

            while (var6.hasNext())
            {
                Entity var7 = (Entity)var6.next();

                if (!var7.doesEntityNotTriggerPressurePlate())
                {	
                	if(var7 instanceof EntityPlayer)
                	{
                		EntityPlayer p = (EntityPlayer)var7;
                		
                		String name = p.getCommandSenderName();
            			long time = System.currentTimeMillis();
            			
            			if(lastUsed.containsKey(name) && lastUsed.get(name) + 2000 > time)
            				return 15;
            			
            			lastUsed.put(name, time);
            			
                		p.mountEntity((Entity)null);
                		
                		final double INIT_Y = var7.posY;
                		
                		boolean isTeleportingDown = p_150065_1_.getBlockMetadata(p_150065_2_, p_150065_3_, p_150065_4_) != 0;
                		
                		double x = var7.posX;
                		double y = isTeleportingDown ? var7.posY-teleportDistance : var7.posY+teleportDistance;
                		double z = var7.posZ;
                		
                		if(isTeleportingDown)//going down, so check if the new location will trap the player in blocks and check fall safety
                    	{
                    		Block a = p.worldObj.getBlock((int)x, (int)y-1, (int)z);
                    		Block b = p.worldObj.getBlock((int)x, (int)y, (int)z);
                   			Block c = p.worldObj.getBlock((int)x, (int)y-2, (int)z);
                   			while(y < INIT_Y && (a.getMaterial().isSolid() || b.getMaterial().isSolid() || !c.getMaterial().isSolid()))
                   			{
                  				a = p.worldObj.getBlock((int)x, (int)++y, (int)z);
                       			b = p.worldObj.getBlock((int)x, (int)y+1, (int)z);
                       			c = p.worldObj.getBlock((int)x, (int)y-1, (int)z);
                   			}
                   		}
                   		else//going up, so check if the new location will trap the player in blocks and check fall safety
                   		{
                   			Block a = p.worldObj.getBlock((int)x, (int)y, (int)z);
                   			Block b = p.worldObj.getBlock((int)x, (int)y+1, (int)z);
                    		Block c = p.worldObj.getBlock((int)x, (int)y-1, (int)z);
                   			while(y > INIT_Y && (a.getMaterial().isSolid() || b.getMaterial().isSolid() || !c.getMaterial().isSolid()))
                   			{
                   				a = p.worldObj.getBlock((int)x, (int)y-1, (int)z);
                       			b = p.worldObj.getBlock((int)x, (int)y--, (int)z);
                       			c = p.worldObj.getBlock((int)x, (int)y-1, (int)z);
                   			}
                   		}
                		//remove chance of pointless teleport
                		if(Math.abs(INIT_Y - y) > 1)
                			p.setPositionAndUpdate(x, y+0.5, z);
                	}
                    return 15;
                }
            }
        }

        return 0;
    }
	
	public IIcon getIcon(int side, int meta)
    {
        return meta == 0 ? clockwise : counter_clockwise;
    }
	
	public void registerBlockIcons(IIconRegister p_149651_1_)
    {
        super.registerBlockIcons(p_149651_1_);
        clockwise = p_149651_1_.registerIcon(teleporterTextureName + "_up");
        counter_clockwise = p_149651_1_.registerIcon(teleporterTextureName + "_down");
    }
	
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int something, float f0, float f1, float f2)
    {
		int meta = world.getBlockMetadata(x, y, z);
		boolean upAndClockwise = meta == 0;
		world.setBlockMetadataWithNotify(x, y, z, upAndClockwise ? 2 : 0, 3);
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
}
