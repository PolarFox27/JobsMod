package net.polarfox27.jobs.data.capabilities;


import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.polarfox27.jobs.data.registry.LevelData;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PlayerData {
	
	public static Capability<PlayerJobs> JOBS = CapabilityManager.get(new CapabilityToken<>(){});

	/**
	 * Gets the Jobs from a player
	 * @param player the player from whom we get the capability
	 * @return the jobs of the player
	 */
	public static PlayerJobs getPlayerJobs(Player player) {
		Optional<PlayerJobs> capability = player.getCapability(JOBS, null).resolve();
		return capability.orElse(null);
	}
	
	public static class JobsDispatcher implements ICapabilitySerializable<CompoundTag> {

		private final PlayerJobs jobs;

		/**
		 * Initialise jobs with levels data
		 * @param levels the levels data
		 */
		public JobsDispatcher(LevelData levels){
			this.jobs = new PlayerJobs(levels);
		}

		@Override
		public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
			return JOBS.orEmpty(cap, LazyOptional.of(() -> jobs));
		}

		/**
		 * Serialize the jobs to NBT
		 * @return the NBT containing the jobs
		 */
		@Override
		public CompoundTag serializeNBT() {
			return this.jobs.toNBT();
		}

		/**
		 * Deserialize the jobs from NBT
		 * @param nbt the nbt from which the jobs are deserialized
		 */
		@Override
		public void deserializeNBT(CompoundTag nbt) {
			this.jobs.fromNBT(nbt);
		}

		
		
	}

}
