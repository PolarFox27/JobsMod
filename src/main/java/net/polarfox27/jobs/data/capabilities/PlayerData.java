package net.polarfox27.jobs.data.capabilities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.polarfox27.jobs.data.registry.LevelData;

import javax.annotation.Nonnull;

public class PlayerData {
	
	@CapabilityInject(PlayerJobs.class)
	public static Capability<PlayerJobs> JOBS;
	
	
	public static PlayerJobs getPlayerJobs(EntityPlayer player)
	{
		return player.getCapability(JOBS, null);
	}
	
	public static void register()
	{
		CapabilityManager.INSTANCE.register(PlayerJobs.class, new Capability.IStorage<PlayerJobs>() 
		{

			@Override
			public NBTBase writeNBT(Capability<PlayerJobs> capability, PlayerJobs instance, EnumFacing side) 
			{
				throw new UnsupportedOperationException();
			}

			@Override
			public void readNBT(Capability<PlayerJobs> capability, PlayerJobs instance, EnumFacing side, NBTBase nbt) 
			{
				throw new UnsupportedOperationException();
			}
			
		}, () -> null);
	}
	
	public static class JobsDispatcher implements ICapabilitySerializable<NBTTagCompound>{

		private final PlayerJobs jobs;

		/**
		 * Initialise jobs with levels data
		 * @param levels the levels data
		 */
		public JobsDispatcher(LevelData levels){
			this.jobs = new PlayerJobs(levels);
		}
		
		@Override
		public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
			return capability == JOBS;
		}

		@Override
		public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
			if(capability == JOBS)
				return (T) jobs;
			return null;
		}

		@Override
		public NBTTagCompound serializeNBT() 
		{
			return this.jobs.toNBT();
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) 
		{
			this.jobs.fromNBT(nbt);
		}
		
	}

}
