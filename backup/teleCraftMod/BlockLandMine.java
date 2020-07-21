package teleCraftMod;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockBasePressurePlate;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLandMine extends BlockBasePressurePlate {
	
	private EntityLandMinePrimed instance;
	
	public BlockLandMine(String str, Material m) {
		super(str, m);
		setStepSound(soundTypePiston);
	}

	@Override
	public int func_150065_e(World world, int x, int y, int z)
	{
		List var5 = world.getEntitiesWithinAABB(EntityLivingBase.class, this.func_150061_a(x, y, z));
		
		if (var5 != null && !var5.isEmpty())
        {
            Iterator var6 = var5.iterator();
            
            while (var6.hasNext())
            {
                EntityLivingBase var7 = (EntityLivingBase)var6.next();

                if (!var7.doesEntityNotTriggerPressurePlate())
                {
                	world.setBlockToAir(x, y, z);
                	world.spawnEntityInWorld(instance);
                	if(var7 instanceof EntityPlayerMP)
                	{
                		EntityPlayerMP player = (EntityPlayerMP)var7;
                		player.addChatMessage(new ChatComponentText("You've hit a land mine! :("));
                	}
                    return 15;
                }
            }
        }
		
		return 0;
	}
	
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack)
	{
		super.onBlockPlacedBy(world, x, y, z, entity, stack);
		instance = new EntityLandMinePrimed(world, x, y, z, entity);
	}

	@Override
	public int func_150066_d(int p_150066_1_)
    {
        return p_150066_1_ > 0 ? 1 : 0;
    }

	@Override
    public int func_150060_c(int p_150060_1_)
    {
        return p_150060_1_ == 1 ? 15 : 0;
    }
	
	public boolean canDropFromExplosion(Explosion p_149659_1_)
    {
		return true;
    }
	
	public int getRenderBlockPass()
    {
        return 1;
    }
    
    public void setBlockBoundsBasedOnState(IBlockAccess p_149719_1_, int p_149719_2_, int p_149719_3_, int p_149719_4_)
    {
        this.func_150063_b(p_149719_1_.getBlockMetadata(p_149719_2_, p_149719_3_, p_149719_4_));
    }

    protected void func_150063_b(int p_150063_1_)
    {
        boolean var2 = this.func_150060_c(p_150063_1_) > 0;
        float var3 = 0.25F;

        if (var2)
        {
            this.setBlockBounds(var3, 0.0F, var3, 1.0F - var3, 0.015625F, 1.0F - var3);
        }
        else
        {
            this.setBlockBounds(var3, 0.0F, var3, 1.0F - var3, 0.03125F, 1.0F - var3);
        }
    }
    
    public int quantityDropped(Random p_149745_1_)
    {
    	return 0;
    }
}
