package teleCraftMod;

import net.minecraft.client.particle.EntityFlameFX;
import net.minecraft.world.World;

public class EntityDarkFlameFX extends EntityFlameFX {

	public EntityDarkFlameFX(World world, double x, double y, double z, double vx, double vy, double vz) {
		super(world, x, y, z, vx, vy, vz);
		this.particleRed = 0.25F;
		this.particleGreen = 0.25F;
		this.particleBlue = 1.0F;
	}
	
}
