package net.polarfox27.jobs.events;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.data.ServerJobsData;
import net.polarfox27.jobs.data.capabilities.PlayerData;
import net.polarfox27.jobs.data.capabilities.PlayerJobs;
import net.polarfox27.jobs.events.server.ContainerEvents;
import net.polarfox27.jobs.network.PacketAskClientUpdate;
import net.polarfox27.jobs.util.config.FileUtil;
import net.polarfox27.jobs.util.handler.PacketHandler;

import java.io.File;
import java.io.IOException;
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
	 * Stores capabilities when the player dies to restore them in the Clone event
	 * @param event the death event
	 */
	@SubscribeEvent
	public void onPlayerDeath(LivingDeathEvent event) {
		if(event.getEntityLiving().level.isClientSide())
			return;
		if(event.getEntityLiving() instanceof Player p){
			PlayerJobs capability = PlayerData.getPlayerJobs(p);
			if (capability != null) {
				String folder = FileUtil.getCapsFolder(event.getEntityLiving().level.getServer());
				if(!new File(folder).exists())
					if(!new File(folder).mkdirs())
						return;
				try {
					NbtIo.writeCompressed(capability.toNBT(), FileUtil.join(folder, p.getGameProfile().getId().toString() + ".dat"));
				} catch (IOException e) {
					ModJobs.info("Error when storing jobs of player " + p.getName().getString() + "(" + p.getGameProfile().getId()
							+ ") -> " + e.getMessage(), true);
				}
			}
		}
	}


	/**
	 * Fired when a player dies to attach his Jobs to him again, so that the progress is not lost.
	 * @param event Player Clone Event
	 */
	@SubscribeEvent
	public void onEntityCloned(PlayerEvent.Clone event) {
		if(!event.isWasDeath() || event.getPlayer().level.isClientSide())
			return;
		Player p = event.getPlayer();
		String folder = FileUtil.getCapsFolder(p.level.getServer());
		try {
			File data = FileUtil.join(folder, p.getGameProfile().getId().toString() + ".dat");
			CompoundTag tag = NbtIo.readCompressed(data);
			if(!data.delete())
				ModJobs.warning("Failed to delete temporary jobs file : <" + data.getAbsolutePath() + "> !");

			PlayerData.getPlayerJobs(p).fromNBT(tag);
		} catch (IOException e) {
			ModJobs.info("Error when storing jobs of player " + p.getName().getString() + "(" + p.getGameProfile().getId()
					+ ") -> " + e.getMessage(), true);
		}
	}

	/**
	 * Fired when a player joins a world : send all the Jobs data to the Client Side.
	 * @param event the Join World Event
	 */
	@SubscribeEvent
	public void onPlayerJoinWorld(EntityJoinWorldEvent event){
		if(!(event.getEntity() instanceof ServerPlayer player))
			return;
		ServerJobsData.sendDataToClient(player);
		if(player.gameMode.isSurvival())
			ContainerEvents.changePlayerInventoryCraftingSlot(player);
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
