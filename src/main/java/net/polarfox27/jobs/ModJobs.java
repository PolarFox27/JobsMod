package net.polarfox27.jobs;

import net.polarfox27.jobs.data.PlayerData;
import net.polarfox27.jobs.util.Reference;
import net.polarfox27.jobs.util.handlers.GuiHandler;
import net.polarfox27.jobs.util.handlers.PacketHandler;
import net.polarfox27.jobs.util.handlers.RegistryHandler;
import net.polarfox27.jobs.util.proxy.CommonProxy;
import net.polarfox27.jobs.util.save.LoadUtil;

import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION, acceptedMinecraftVersions = Reference.ACCEPTED_VERSIONS)
public class ModJobs {

    @Instance
    public static ModJobs instance;

    @SidedProxy(serverSide = Reference.SERVER_PROXY_CLASS, clientSide = Reference.CLIENT_PROXY_CLASS)
    public static CommonProxy proxy;

    @EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        PacketHandler.registerPackets();
        System.out.println(TextFormatting.BLUE + "[Jobs] Packets Registered");
        PlayerData.register();
        System.out.println(TextFormatting.BLUE + "[Jobs] Capabilities Registered");

        proxy.registerKeyBindings();
    }

    @EventHandler
    public static void init(FMLInitializationEvent event)
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(ModJobs.instance, new GuiHandler());
        System.out.println(TextFormatting.BLUE + "[Jobs] GUIs Registered");
    }

    @EventHandler
    public static void postInit(FMLPostInitializationEvent event)
    {

    }

    @EventHandler
    public static void serverStart(FMLServerStartingEvent e)
    {
        RegistryHandler.registerCommands(e);
        System.out.println(TextFormatting.BLUE + "[Jobs] Commands Registered");
        LoadUtil.loadData();
        System.out.println(TextFormatting.BLUE + "[Jobs] Data Loaded");

    }
}
