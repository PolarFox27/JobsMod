package net.polarfox27.jobs.events;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.data.ServerJobsData;
import net.polarfox27.jobs.data.capabilities.PlayerData;
import net.polarfox27.jobs.data.capabilities.PlayerJobs;
import net.polarfox27.jobs.network.PacketAskClientUpdate;
import net.polarfox27.jobs.util.handler.PacketHandler;

import java.util.Optional;

@EventBusSubscriber
public class CommonEvents {
	
	@SubscribeEvent
	public void onEntityCreating(AttachCapabilitiesEvent<Entity> event) {
		if(event.getObject().level.isClientSide)
			return;
		if(!(event.getObject() instanceof PlayerEntity))
			return;
		Optional<PlayerJobs> capability = event.getObject()
											 .getCapability(PlayerData.JOBS, null)
											 .resolve();
		if(!capability.isPresent()) {
			event.addCapability(new ResourceLocation(ModJobs.MOD_ID, "jobs"),
								new PlayerData.JobsDispatcher(ServerJobsData.JOBS_LEVELS));
		}
	}
	
	@SubscribeEvent
	public void onEntityCloned(PlayerEvent.Clone event) {
		if(!event.isWasDeath())
			return;
		Optional<PlayerJobs> capability = event.getOriginal()
											 .getCapability(PlayerData.JOBS, null)
											 .resolve();
		if(capability.isPresent()) {
			PlayerJobs old_jobs = capability.get();
			PlayerJobs new_jobs = PlayerData.getPlayerJobs(event.getPlayer());
			new_jobs.copy(old_jobs);
		}
	}
	
	@SubscribeEvent
	public void onPlayerJoinedServer(EntityJoinWorldEvent event){
		if(!(event.getEntity() instanceof ServerPlayerEntity))
			return;
		ServerJobsData.sendDataToClient((ServerPlayerEntity)event.getEntity());
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onPlayerJoinedClient(EntityJoinWorldEvent event) {
		if(!(event.getEntity() instanceof ClientPlayerEntity))
			return;
		PacketHandler.INSTANCE.sendToServer(PacketAskClientUpdate.instance);
	}

}
