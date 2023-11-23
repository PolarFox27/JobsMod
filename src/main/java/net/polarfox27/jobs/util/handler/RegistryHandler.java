package net.polarfox27.jobs.util.handler;

import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.EmptyArgumentSerializer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.commands.JobCommand;
import net.polarfox27.jobs.commands.arguments.JobArgumentType;
import net.polarfox27.jobs.data.capabilities.PlayerJobs;
import net.polarfox27.jobs.events.CommonEvents;
import net.polarfox27.jobs.events.client.GuiEvents;
import net.polarfox27.jobs.events.server.BlockInteractionEvents;
import net.polarfox27.jobs.events.server.ContainerEvents;
import net.polarfox27.jobs.events.server.EntityInteractionEvents;
import net.polarfox27.jobs.events.server.ItemInteractionEvents;

@EventBusSubscriber
public class RegistryHandler {

	/**
	 * Registers the Commands of the mod
	 * @param event the Register Event
	 */
	@SubscribeEvent
	public void onCommandsRegistered(RegisterCommandsEvent event) {
		JobCommand.register(event.getDispatcher());
		ModJobs.info("Commands Registered", false);
	}

	/**
	 * Registers the command argument types used by the mod's commands
	 */
	public static void registerCommandArguments(){
		ArgumentTypes.register("jobs:job_argument", JobArgumentType.class, new EmptyArgumentSerializer<>(JobArgumentType::job));
	}

	/**
	 * Registers the Event Listeners of the mod
	 */
	public static void registerListeners() {
		MinecraftForge.EVENT_BUS.register(new GuiEvents());
		MinecraftForge.EVENT_BUS.register(new RegistryHandler());
		MinecraftForge.EVENT_BUS.register(new CommonEvents());
		MinecraftForge.EVENT_BUS.register(new BlockInteractionEvents());
		MinecraftForge.EVENT_BUS.register(new EntityInteractionEvents());
		MinecraftForge.EVENT_BUS.register(new ItemInteractionEvents());
		MinecraftForge.EVENT_BUS.register(new ContainerEvents());
	}

	/**
	 * Registers the jobs capabilities
	 * @param event the register capabilities event
	 */
	@SubscribeEvent
	public void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.register(PlayerJobs.class);
	}
}
