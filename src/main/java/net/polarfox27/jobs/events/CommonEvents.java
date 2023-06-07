package net.polarfox27.jobs.events;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.data.ServerJobsData;
import net.polarfox27.jobs.data.capabilities.PlayerData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.polarfox27.jobs.data.capabilities.PlayerJobs;
import net.polarfox27.jobs.network.PacketAskClientUpdate;
import net.polarfox27.jobs.util.handlers.PacketHandler;

import java.util.Arrays;


@EventBusSubscriber
public class CommonEvents {

	/**
	 * Attaches the Jobs capability to a player if it is not already present
	 * @param event the Capability Event
	 */
	@SubscribeEvent
	public static void onEntityCreating(AttachCapabilitiesEvent<Entity> event) {
		if(event.getObject().world.isRemote)
			return;
		if(!(event.getObject() instanceof EntityPlayer))
			return;
		PlayerJobs capability = event.getObject()
				.getCapability(PlayerData.JOBS, null);
		if(capability == null || capability.getJobs().isEmpty()) {
			event.addCapability(new ResourceLocation(ModJobs.MOD_ID, "jobs"),
					new PlayerData.JobsDispatcher(ServerJobsData.JOBS_LEVELS));
		}
	}

	/**
	 * Fired when a player dies to attach his Jobs to him again, so that the progress is not lost.
	 * @param event Player Clone Event
	 */
	@SubscribeEvent
	public static void onEntityCloned(PlayerEvent.Clone event) {
		if(!event.isWasDeath())
			return;
		PlayerJobs capability = event.getOriginal()
				.getCapability(PlayerData.JOBS, null);
		if(capability != null) {
			PlayerJobs new_jobs = PlayerData.getPlayerJobs(event.getEntityPlayer());
			new_jobs.copy(capability);
		}
	}

	/**
	 * Fired when a player joins a world : send all the Jobs data to the Client Side.
	 * @param event the Join World Event
	 */
	@SubscribeEvent
	public static void onPlayerJoinedServer(EntityJoinWorldEvent event){
		if(!(event.getEntity() instanceof EntityPlayerMP))
			return;
		ServerJobsData.sendDataToClient((EntityPlayerMP) event.getEntity());
	}

	/**
	 * Fired when a player joins a world (client side) : asks the data to the server.
	 * @param event the Join World Event
	 */
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onPlayerJoinedClient(EntityJoinWorldEvent event) {
		if(!(event.getEntity() instanceof EntityPlayerSP))
			return;
		PacketHandler.INSTANCE.sendToServer(new PacketAskClientUpdate());
	}
}
