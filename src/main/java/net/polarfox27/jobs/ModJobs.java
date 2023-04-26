package net.polarfox27.jobs;

import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.polarfox27.jobs.data.ServerJobsData;
import net.polarfox27.jobs.util.config.ReadConfigManager;
import net.polarfox27.jobs.util.handler.PacketHandler;
import net.polarfox27.jobs.util.handler.RegistryHandler;
import net.polarfox27.jobs.util.keybindings.KeyBindings;
import org.slf4j.Logger;

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ModJobs.MOD_ID)
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
    public void serverStarting(ServerStartingEvent event) {
        ReadConfigManager.readConfigFiles(event.getServer());
        info("Configuration Loaded", false);
    }

    /**
     * Prints a message to the console with [Jobs] appended in front, with color codes
     * @param message the message to be printed
     * @param isError if true, the message will be red
     */
    public static void info(String message, boolean isError) {
        String msg = (isError ? ChatFormatting.RED : ChatFormatting.BLUE) + "[Jobs] ";
        System.out.println(msg + message);
    }
}
