package net.polarfox27.jobs.util.handler;

import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.commands.JobCommand;
import net.polarfox27.jobs.commands.arguments.JobArgumentInfo;
import net.polarfox27.jobs.commands.arguments.JobArgumentType;
import net.polarfox27.jobs.data.capabilities.PlayerJobs;
import net.polarfox27.jobs.events.CommonEvents;
import net.polarfox27.jobs.events.client.ClientBlockEvents;
import net.polarfox27.jobs.events.client.GuiEvents;
import net.polarfox27.jobs.events.client.KeyBindingsEvent;
import net.polarfox27.jobs.events.server.BlockInteractionEvents;
import net.polarfox27.jobs.events.server.ContainerEvents;
import net.polarfox27.jobs.events.server.EntityInteractionEvents;
import net.polarfox27.jobs.events.server.ItemInteractionEvents;
import net.polarfox27.jobs.util.keybindings.KeyBindings;

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
		ArgumentTypeInfos.registerByClass(JobArgumentType.class, new JobArgumentInfo());
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
		MinecraftForge.EVENT_BUS.register(new KeyBindingsEvent());
		MinecraftForge.EVENT_BUS.register(new ContainerEvents());
		MinecraftForge.EVENT_BUS.register(new ClientBlockEvents());
	}

	/**
	 * Registers the jobs capabilities
	 * @param event the register capabilities event
	 */
	@SubscribeEvent
	public void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.register(PlayerJobs.class);
	}

	/**
	 * Registers the key bindings
	 * @param event the registry event
	 */
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void registerKeyBindings(RegisterKeyMappingsEvent event){
		event.register(KeyBindings.open_gui.get());
		ModJobs.info("Keybindings Registered", false);
	}

}
