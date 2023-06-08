package net.polarfox27.jobs.util.handler;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.commands.CommandAdd;
import net.polarfox27.jobs.commands.CommandInfo;
import net.polarfox27.jobs.commands.CommandSet;
import net.polarfox27.jobs.data.capabilities.PlayerJobs;
import net.polarfox27.jobs.events.CommonEvents;
import net.polarfox27.jobs.events.client.GuiEvents;
import net.polarfox27.jobs.events.server.BlockInteractionEvents;
import net.polarfox27.jobs.events.server.EntityInteractionEvents;
import net.polarfox27.jobs.events.server.ItemInteractionEvents;
import net.polarfox27.jobs.gui.containers.JobsCraftingMenu;

@EventBusSubscriber
public class RegistryHandler {

	public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, ModJobs.MOD_ID);
	public static final RegistryObject<MenuType<JobsCraftingMenu>> JOBS_CRAFT = CONTAINERS.register("jobs_crafting", () -> new MenuType<>(JobsCraftingMenu::new));

	/**
	 * Registers the Commands of the mod
	 * @param event the Register Event
	 */
	@SubscribeEvent
	public void onCommandsRegistered(RegisterCommandsEvent event) {
		CommandInfo.register(event.getDispatcher());
		CommandSet.register(event.getDispatcher());
		CommandAdd.register(event.getDispatcher());
		ModJobs.info("Commands Registered", false);
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
	 * Registers the Jobs crafting container
	 */
	public static void registerContainers(){
		CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}

}
