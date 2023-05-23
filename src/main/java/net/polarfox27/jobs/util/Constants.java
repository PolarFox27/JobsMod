package net.polarfox27.jobs.util;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityElderGuardian;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Constants {
	
	public static String SaveFolder = "";
	public static String DataFolder = "";
	
	public static final long[] XP_BY_LEVEL = new long[] {0, 100, 150, 250, 500, 800, //0 - 5
														 1150, 1550, 2000, 2500, 3100, //6 - 10
														 3750, 4500, 5500, 7000, 9000, //11 - 15
														 11500, 15000, 19000, 24000, 30000, //16 - 20
														 39250, 49500, 63100, 80000, 100000}; //21 - 25
	
	public static final long[] TOTAL_XP_BY_LEVEL = new long[] {0, 100, 250, 500, 1000, 1800, //0 - 5
															   2950, 4500, 6500, 9000, 12100, //6 - 10
															   15850, 20350, 25850, 32850, 41850, //11 - 15
															   53350, 68350, 87350, 111350, 141350, //16 - 20
															   180600, 230100, 293200, 373200, 473200}; //21 - 25

	@SideOnly(Side.CLIENT)
	public static EntityLivingBase getEntityInstance(Entities entity, World world)
	{
		EntityLivingBase e = null;
		Constructor constructor = null;
		if(world == null) return null;
		for(Constructor c : entity.entityClass.getConstructors())
			if(c.getGenericParameterTypes().length == 1)
			{
				constructor = c;
				break;
			}
		try
		{
			if(constructor != null)
				e = (EntityLivingBase) constructor.newInstance(world);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException exception)
		{
			exception.printStackTrace();
		}
		if(e == null)
		{
			e = Minecraft.getMinecraft().player;
		}
		return e;
	}


	public static enum Job
	{
		HUNTER(0, "hunter"),
		MAGICIAN(1, "magician"),
		FARMER(2, "farmer"),
		MINER(3, "miner"),
		NONE(-1, "none");
		
		public int index;
		public String name;
		
		Job(int index, String name) 
		{
			this.index = index;
			this.name = name;
		}
		
		public static Job byIndex(int index)
		{
			switch(index)
			{
			case 0:
				return HUNTER;
			case 1:
				return MAGICIAN;
			case 2:
				return FARMER;
			case 3:
				return MINER;
			default:
				return NONE;
			}
		}
		
	}

	public static enum XPCategories
	{
		CRAFTING(false, 0),
		SMELTING(false, 1),
		BREAKING(false, 2),
		KILLING(false, 3),
		HARVESTING(false, 4),

		XP(true, 0),
		UNLOCK(true, 1);


		public final boolean isCategory;
		public final int index;
		XPCategories(boolean isCategory, int in)
		{
			this.isCategory = isCategory;
			this.index = in;
		}
		
		public static XPCategories[] getXPValues()
		{
			return new XPCategories[] {CRAFTING, SMELTING, BREAKING, KILLING, HARVESTING};
		}

	}
	
	public static enum Entities
	{
		COW(0, true, 17, EntityCow.class),
		CHICKEN(1, true, 30, EntityChicken.class),
		ZOMBIE(2, false, 15, EntityZombie.class),
		SHEEP(3, true, 17, EntitySheep.class),
		RABBIT(4, true, 30, EntityRabbit.class),
		BLAZE(5, false, 16, EntityBlaze.class),
		CREEPER(6, false, 16, EntityCreeper.class),
		ENDERMAN(7, false, 12, EntityEnderman.class),
		GHAST(8, false, 5, EntityGhast.class),
		MAGMA_CUBE(9, false, 30, EntityMagmaCube.class),
		PIG(10, true, 18, EntityPig.class),
		SKELETON(11, false, 15, EntitySkeleton.class),
		SLIME(12, false, 30, EntitySlime.class),
		SPIDER(13, false, 18, EntitySpider.class),
		WITCH(14, false, 15, EntityWitch.class),
		WITHER_SKELETON(15, false, 12, EntityWitherSkeleton.class),
		ZOMBIE_PIGMAN(16, false, 15, EntityPigZombie.class),
		PLAYER(17, false, 15, EntityPlayer.class),
		WITHER(18, false, 10, EntityWither.class),
		ENDER_DRAGON(19, false, 5, EntityDragon.class),
		GUARDIAN(20, false, 15, EntityGuardian.class),
		ELDER_GUARDIAN(21, false, 10, EntityElderGuardian.class),
		HORSE(22, true, 15, EntityHorse.class),
		DONKEY(23, true, 15, EntityDonkey.class),
		CAVE_SPIDER(24, false, 20, EntityCaveSpider.class);
		
		public final int id;
		public final boolean isTameable;
		public final int scale;
		public final Class<? extends Entity> entityClass;
		private Entities(int id, boolean tameable, int scale, Class<? extends Entity> clazz) 
		{
			this.id = id;
			this.isTameable = tameable;
			this.scale = scale;
			this.entityClass = clazz;
		}
		
		public static Entities byID(int id)
		{
			for(Entities e : Entities.values())
				if(e.id == id)
					return e;
			return null;
		}
		
		public static Entities byName(String name)
		{
			for(Entities e : Entities.values())
				if(e.getName().equals(name))
					return e;
			return null;
		}
		
		public static Entities byClass(Class<? extends Entity> clazz)
		{
			for(Entities e : Entities.values())
				if(e.entityClass.equals(clazz))
					return e;
			return null;
		}
		
		public String getName()
		{
			String str = this.toString();
			if(!str.contains("_"))
				return str.substring(0, 1) + str.substring(1).toLowerCase();
			else
			{
				String part1 = str.substring(0, str.indexOf("_"));
				String part2 = str.substring(str.indexOf("_") + 1);
				
				return part1.substring(0, 1) + part1.substring(1).toLowerCase() + " " + 
						part2.substring(0, 1) + part2.substring(1).toLowerCase();
			}
		}
	}

}
