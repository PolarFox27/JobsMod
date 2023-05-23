package com.dorian2712.jobs.data;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class PlayerData {
	
	@CapabilityInject(JobsInfo.class)
	public static Capability<JobsInfo> JOBS;
	
	
	public static JobsInfo getPlayerJobs(EntityPlayer player)
	{
		return player.getCapability(JOBS, null);
	}
	
	public static void register()
	{
		CapabilityManager.INSTANCE.register(JobsInfo.class, new Capability.IStorage<JobsInfo>() 
		{

			@Override
			public NBTBase writeNBT(Capability<JobsInfo> capability, JobsInfo instance, EnumFacing side) 
			{
				throw new UnsupportedOperationException();
			}

			@Override
			public void readNBT(Capability<JobsInfo> capability, JobsInfo instance, EnumFacing side, NBTBase nbt) 
			{
				throw new UnsupportedOperationException();
			}
			
		}, () -> null);
	}
	
	public static class JobsDispatcher implements ICapabilitySerializable<NBTTagCompound>{

		private JobsInfo jobs = new JobsInfo();
		
		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) 
		{
			return capability == JOBS;
		}

		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) 
		{
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
