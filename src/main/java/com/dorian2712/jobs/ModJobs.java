package com.dorian2712.jobs;

import com.dorian2712.jobs.data.PlayerData;
import com.dorian2712.jobs.util.Reference;
import com.dorian2712.jobs.util.handlers.GuiHandler;
import com.dorian2712.jobs.util.handlers.PacketHandler;
import com.dorian2712.jobs.util.handlers.RegistryHandler;
import com.dorian2712.jobs.util.proxy.CommonProxy;

import com.dorian2712.jobs.util.save.LoadUtil;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
