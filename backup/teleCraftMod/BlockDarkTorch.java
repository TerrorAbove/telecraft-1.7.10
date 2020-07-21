package teleCraftMod;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDarkTorch extends BlockTorch
{
	public BlockDarkTorch()
	{
		super();
		setBlockTextureName("telecraft:dark_torch");
	}
	
	public void updateTick(World world, int x, int y, int z, Random rand)
    {
		super.updateTick(world, x, y, z, rand);
		Tessellator var6 = Tessellator.instance;
		var6.setBrightness(10);
		var6.setColorOpaque_F(0.0F, 0.0F, 0.0F);
    }
	
	public int onBlockPlaced(World world, int x, int y, int z, int other, float f1, float f2, float f3, int something)
    {
		int a = super.onBlockPlaced(world, x, y, z, other, f1, f2, f3, something);
		updateTick(world, x, y, z, new Random());
		return a;
    }
	
	public void randomDisplayTick(World p_149734_1_, int p_149734_2_, int p_149734_3_, int p_149734_4_, Random p_149734_5_)
    {
        int var6 = p_149734_1_.getBlockMetadata(p_149734_2_, p_149734_3_, p_149734_4_);
        double var7 = (double)((float)p_149734_2_ + 0.5F);
        double var9 = (double)((float)p_149734_3_ + 0.7F);
        double var11 = (double)((float)p_149734_4_ + 0.5F);
        double var13 = 0.2199999988079071D;
        double var15 = 0.27000001072883606D;

        if (var6 == 1)
        {
            p_149734_1_.spawnParticle("smoke", var7 - var15, var9 + var13, var11, 0.0D, 0.0D, 0.0D);
            p_149734_1_.spawnEntityInWorld(new EntityDarkFlameFX(p_149734_1_, var7 - var15, var9 + var13, var11, 0, 0, 0));
        }
        else if (var6 == 2)
        {
            p_149734_1_.spawnParticle("smoke", var7 + var15, var9 + var13, var11, 0.0D, 0.0D, 0.0D);
            p_149734_1_.spawnEntityInWorld(new EntityDarkFlameFX(p_149734_1_, var7 + var15, var9 + var13, var11, 0, 0, 0));
        }
        else if (var6 == 3)
        {
            p_149734_1_.spawnParticle("smoke", var7, var9 + var13, var11 - var15, 0.0D, 0.0D, 0.0D);
            p_149734_1_.spawnEntityInWorld(new EntityDarkFlameFX(p_149734_1_, var7, var9 + var13, var11 - var15, 0, 0, 0));
        }
        else if (var6 == 4)
        {
            p_149734_1_.spawnParticle("smoke", var7, var9 + var13, var11 + var15, 0.0D, 0.0D, 0.0D);
            p_149734_1_.spawnEntityInWorld(new EntityDarkFlameFX(p_149734_1_, var7, var9 + var13, var11 + var15, 0, 0, 0));
        }
        else
        {
            p_149734_1_.spawnParticle("smoke", var7, var9, var11, 0.0D, 0.0D, 0.0D);
            p_149734_1_.spawnEntityInWorld(new EntityDarkFlameFX(p_149734_1_, var7, var9, var11, 0, 0, 0));
        }
    }
}
