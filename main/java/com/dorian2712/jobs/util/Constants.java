package com.dorian2712.jobs.util;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.cookie.SM;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
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
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityHorse;
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
	
	public static final String[] ENTITIES = new String[]{"Cow", "Chicken", "Zombie", "Sheep", "Rabbit", "Blaze", "Creeper", "Enderman",
		"Ghast", "Magma Cube", "Pig", "Skeleton", "Slime", "Spider", "Witch", "Wither Skeleton", "Zombie Pigman", "Player", "Wither", 
		"Ender Dragon", "Guardian", "Elder Guardian", "Horse", "Donkey", "Cave Spider"};
	public static final int[] IDS = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24};

	public static Map<String, Integer> getEntitiesID()
	{
		Map<String, Integer> map = new HashMap<>();
		for(int i = 0; i < ENTITIES.length; i++)
		{
			map.put(ENTITIES[i], IDS[i]);
		}
		return map;
	}
	public static Map<Integer, String> getIDEntities()
	{
		Map<Integer, String> map = new HashMap<>();
		for(int i = 0; i < ENTITIES.length; i++)
		{
			map.put(IDS[i], ENTITIES[i]);
		}
		return map;
	}


	public static Map<String, Class> getClassesByName()
	{
		Map<String, Class> map = new HashMap<>();
		map.put("Cow", EntityCow.class);
		map.put("Chicken", EntityChicken.class);
		map.put("Zombie", EntityZombie.class);
		map.put("Sheep", EntitySheep.class);
		map.put("Rabbit", EntityRabbit.class);
		map.put("Blaze", EntityBlaze.class);
		map.put("Creeper", EntityCreeper.class);
		map.put("Enderman", EntityEnderman.class);
		map.put("Ghast", EntityGhast.class);
		map.put("Magma Cube", EntityMagmaCube.class);
		map.put("Pig", EntityPig.class);
		map.put("Skeleton", EntitySkeleton.class);
		map.put("Slime", EntitySlime.class);
		map.put("Spider", EntitySpider.class);
		map.put("Witch", EntityWitch.class);
		map.put("Wither Skeleton", EntityWitherSkeleton.class);
		map.put("Zombie Pigman", EntityPigZombie.class);
		map.put("Player", EntityPlayerSP.class);
		map.put("Wither", EntityWither.class);
		map.put("Ender Dragon", EntityDragon.class);
		map.put("Guardian", EntityGuardian.class);
		map.put("Elder Guardian", EntityElderGuardian.class);
		map.put("Horse", EntityHorse.class);
		map.put("Donkey", EntityDonkey.class);
		map.put("Cave Spider", EntityCaveSpider.class);
		return map;
	}

	public static Map<String, Integer> getClassScaleByName()
	{
		Map<String, Integer> map = new HashMap<>();
		map.put("Cow", 17);
		map.put("Chicken", 30);
		map.put("Zombie", 15);
		map.put("Sheep", 17);
		map.put("Rabbit", 30);
		map.put("Blaze", 16);
		map.put("Creeper", 16);
		map.put("Enderman", 12);
		map.put("Ghast", 5);
		map.put("Magma Cube", 30);
		map.put("Pig", 18);
		map.put("Skeleton", 15);
		map.put("Slime", 30);
		map.put("Spider", 18);
		map.put("Witch", 15);
		map.put("Wither Skeleton", 12);
		map.put("Zombie Pigman", 15);
		map.put("Player", 15);
		map.put("Wither", 10);
		map.put("Ender Dragon", 5);
		map.put("Guardian", 15);
		map.put("Elder Guardian", 10);
		map.put("Horse", 15);
		map.put("Donkey", 15);
		map.put("Cave Spider", 20);
		return map;
	}
	
	public static String getNameByClass(Class<? extends EntityLivingBase> clazz)
	{
		for(Entry<String, Class> e : getClassesByName().entrySet())
		{
			if(e.getValue().isAssignableFrom(clazz))
				return e.getKey();
		}
		return "";
	}

	@SideOnly(Side.CLIENT)
	public static EntityLivingBase getEntityByName(String name, World world)
	{
		EntityLivingBase e = null;
		Constructor constructor = null;
		if(world == null) return null;
		for(Constructor c : getClassesByName().get(name).getConstructors())
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

}
