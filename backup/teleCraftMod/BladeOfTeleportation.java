package teleCraftMod;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class BladeOfTeleportation extends ItemSword
{
	public BladeOfTeleportation(ToolMaterial mat) {
		super(mat);
		this.setUnlocalizedName("bladeOfTeleportation");
		this.setTextureName("telecraft:blade_of_teleportation");
	}
	
	public EnumAction getItemUseAction(ItemStack p_77661_1_)
    {
        return EnumAction.bow;
    }
	
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool)
	{
		list.add("Hold right-click to teleport to an entity! (min. 10 blocks away");
	}
	
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int someInt)
	{
		//if(player.getItemInUseDuration() > 31)
			//teleport(player);
	}
	
	public int getMaxItemUseDuration(ItemStack p_77626_1_)
    {
        return 34;
    }
	
	/*private void teleport(EntityPlayer player)
	{
		if(player.isClientWorld())
		{
			Entity target = Minecraft.getMinecraft().renderViewEntity.rayTrace(200, 1.0F).entityHit;
			if(target != null)
			{
				if(target instanceof EntityLivingBase)
				{
					EntityLivingBase elb = (EntityLivingBase)target;
					float dist = elb.getDistanceToEntity(player);
					
					if(dist > 10)
					{
						if(elb.attackEntityFrom(DamageSource.magic, 1+(float)Math.sqrt(dist)))
						{
							final double X = elb.posX;
							final double Z = elb.posZ;
							final double Y = elb.posY;
						
							if(player.posX < X)
							{
								if(player.posZ < Z)
								{
									player.setPositionAndUpdate(X-0.25, Y, Z-0.25);
								}
								else
								{
									player.setPositionAndUpdate(X-0.25, Y, Z+0.25);
								}
							}
							else
							{
								if(player.posZ < Z)
								{
									player.setPositionAndUpdate(X+0.25, Y, Z-0.25);
								}
								else
								{
									player.setPositionAndUpdate(X+0.25, Y, Z+0.25);
								}
							}
						}
					}
				}
			}
		}
	}*/
}
