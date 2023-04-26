package net.polarfox27.jobs.events;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
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

	/**
	 * Attaches the Jobs capability to a player if it is not already present
	 * @param event the Capability Event
	 */
	@SubscribeEvent
	public void onEntityCreating(AttachCapabilitiesEvent<Entity> event) {
		if(event.getObject().level.isClientSide)
			return;
		if(!(event.getObject() instanceof Player))
			return;
		Optional<PlayerJobs> capability = event.getObject()
											 .getCapability(PlayerData.JOBS, null)
											 .resolve();
		if(capability.isEmpty()) {
			event.addCapability(new ResourceLocation(ModJobs.MOD_ID, "jobs"),
								new PlayerData.JobsDispatcher(ServerJobsData.JOBS_LEVELS));
		}
	}

	@SubscribeEvent
	public void onRegisterCapabilities(RegisterCapabilitiesEvent event){
		event.register(PlayerJobs.class);
	}

	/**
	 * Fired when a player dies to attach his Jobs to him again, so that the progress is not lost.
	 * @param event Player Clone Event
	 */
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

	/**
	 * Fired when a player joins a world : send all the Jobs data to the Client Side.
	 * @param event the Join World Event
	 */
	@SubscribeEvent
	public void onPlayerJoinedServer(EntityJoinWorldEvent event){
		if(!(event.getEntity() instanceof ServerPlayer))
			return;
		ServerJobsData.sendDataToClient((ServerPlayer)event.getEntity());
	}

	/**
	 * Fired when a player joins a world (client side) : asks the data to the server.
	 * @param event the Join World Event
	 */
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onPlayerJoinedClient(EntityJoinWorldEvent event) {
		if(!(event.getEntity() instanceof AbstractClientPlayer))
			return;
		PacketHandler.INSTANCE.sendToServer(PacketAskClientUpdate.instance);
	}

}
