package net.polarfox27.jobs;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.polarfox27.jobs.data.ServerJobsData;
import net.polarfox27.jobs.data.capabilities.PlayerData;
import net.polarfox27.jobs.events.CommonEvents;
import net.polarfox27.jobs.events.server.BlockInteractionEvents;
import net.polarfox27.jobs.events.server.EntityInteractionEvents;
import net.polarfox27.jobs.events.server.ItemInteractionEvents;
import net.polarfox27.jobs.util.config.ReadConfigManager;
import net.polarfox27.jobs.util.handlers.GuiHandler;
import net.polarfox27.jobs.util.handlers.PacketHandler;
import net.polarfox27.jobs.util.handlers.RegistryHandler;
import net.polarfox27.jobs.util.proxy.CommonProxy;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import scala.tools.nsc.transform.patmat.MatchOptimization;

@Mod(modid = ModJobs.MOD_ID, name = ModJobs.NAME, version = ModJobs.VERSION, acceptedMinecraftVersions = ModJobs.ACCEPTED_VERSIONS)
public class ModJobs {

    public static final String MOD_ID = "jobs";
    public static final String NAME = "Jobs";
    public static final String VERSION = "1.12.2-3.0.2";
    public static final String ACCEPTED_VERSIONS = "[1.12.2]";
    public static final String CLIENT_PROXY_CLASS = "net.polarfox27.jobs.util.proxy.ClientProxy";
    public static final String SERVER_PROXY_CLASS = "net.polarfox27.jobs.util.proxy.CommonProxy";
    public static final int ID_GUI_CRAFTING = 101;


    @Instance
    public static ModJobs instance;

    @SidedProxy(serverSide = ModJobs.SERVER_PROXY_CLASS, clientSide = ModJobs.CLIENT_PROXY_CLASS)
    public static CommonProxy proxy;

    /**
     * Mod pre-init
     * @param event the pre-init event
     */
    @EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        PacketHandler.registerPackets();
        info("Packets Registered", false);
        PlayerData.register();
        info("Capabilities Registered", false);
        ServerJobsData.registerCommonXPRegistries();
        info("Common XP Categories Registered", false);
        proxy.registerKeyBindings();
        ModJobs.info("KeyBindings Registered", false);
    }

    /**
     * Mod init event
     * @param event the init event
     */
    @EventHandler
    public static void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(ModJobs.instance, new GuiHandler());
        info("GUIs Registered", false);
    }

    /**
     * Mod post init
     * @param event the post-init event
     */
    @EventHandler
    public static void postInit(FMLPostInitializationEvent event) {
    }

    /**
     * Executed when the server is started
     * registers the commands and loads the Jobs data
     * @param event the starting event
     */
    @EventHandler
    public static void serverStart(FMLServerStartingEvent event) {
        RegistryHandler.registerCommands(event);
        info("Commands Registered", false);
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
