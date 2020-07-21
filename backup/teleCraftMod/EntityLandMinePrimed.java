package teleCraftMod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.world.World;

public class EntityLandMinePrimed extends EntityTNTPrimed
{

	public EntityLandMinePrimed(World world, double d0, double d1, double d2, EntityLivingBase e) {
		super(world, d0, d1, d2, e);
		this.fuse = 0;
	}

}
