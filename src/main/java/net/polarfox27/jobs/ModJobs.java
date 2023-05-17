package net.polarfox27.jobs;


import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.polarfox27.jobs.data.ServerJobsData;
import net.polarfox27.jobs.util.config.ReadConfigManager;
import net.polarfox27.jobs.util.handler.PacketHandler;
import net.polarfox27.jobs.util.handler.RegistryHandler;
import net.polarfox27.jobs.util.keybindings.KeyBindings;

@Mod(ModJobs.MOD_ID)
@Mod.EventBusSubscriber
public class ModJobs {

	public static final String MOD_ID = "jobs";

	/**
	 * Constructor of the Mod. All Event Handlers, Packet Handler and XP Registries are registered.
	 */
	public ModJobs() {
		RegistryHandler.registerListeners();
		info("Event Handlers Registered", false);
		PacketHandler.registerPackets();
		info("Packets Registered", false);
		ServerJobsData.registerCommonXPRegistries();
		info("Common XP Categories Registered", false);
		MinecraftForge.EVENT_BUS.register(this);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
	}

	@SubscribeEvent
	public void setup(final FMLCommonSetupEvent event) {
	}

	@SubscribeEvent
	public void clientSetup(final FMLClientSetupEvent event) {
		KeyBindings.register();
		info("Keybindings Registered", false);
	}

	@SubscribeEvent
	public void serverStarting(FMLServerStartingEvent event) {
		ReadConfigManager.readConfigFiles(event.getServer());
		info("Configuration Loaded", false);
	}

	/**
	 * Prints a message to the console with [Jobs] appended in front, with color codes
	 * @param message the message to be printed
	 * @param isError if true, the message will be red
	 */
	public static void info(String message, boolean isError) {
		String msg = (isError ? "\u001B[31m" : "\u001B[34m") + "[Jobs] ";
		System.out.println(msg + message + "\u001B[0m");
	}

	/**
	 * Prints a warning to the console with [Jobs] appended in front, with yellow color
	 * @param message the message to be printed
	 */
	public static void warning(String message) {
		String msg = "\u001B[33m[Jobs] ";
		System.out.println(msg + message + "\u001B[0m");
	}
}
