package net.polarfox27.jobs.data.capabilities;


import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.polarfox27.jobs.data.registry.LevelData;

import java.util.Optional;

public class PlayerData {
	
	@CapabilityInject(PlayerJobs.class)
	public static Capability<PlayerJobs> JOBS;
	
	public static PlayerJobs getPlayerJobs(PlayerEntity player) {
		Optional<PlayerJobs> capability = player.getCapability(JOBS, null).resolve();
		return capability.orElse(null);
	}
	
	public static void register() {
		CapabilityManager.INSTANCE.register(PlayerJobs.class, new Capability.IStorage<PlayerJobs>() {

			@Override
			public INBT writeNBT(Capability<PlayerJobs> capability, PlayerJobs instance, Direction side) {
				return instance.toNBT();
			}

			@Override
			public void readNBT(Capability<PlayerJobs> capability, PlayerJobs instance, Direction side, INBT nbt) {
				instance.fromNBT((CompoundNBT) nbt);
			}
			
		}, () -> null);
	}
	
	public static class JobsDispatcher implements ICapabilitySerializable<CompoundNBT>{

		private final PlayerJobs jobs;

		public JobsDispatcher(LevelData levels){
			this.jobs = new PlayerJobs(levels);
		}

		@Override
		public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
			return LazyOptional.of(() -> (T)jobs);
		}

		@Override
		public CompoundNBT serializeNBT() {
			return this.jobs.toNBT();
		}

		@Override
		public void deserializeNBT(CompoundNBT nbt) {
			this.jobs.fromNBT(nbt);
		}
		
		
	}

}
