package com.dorian2712.jobs.util.proxy;


import com.dorian2712.jobs.util.keybindings.KeyBindings;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {

    public ClientProxy()
    {
        registerKeyBindings();
        System.out.println(TextFormatting.BLUE + "[Jobs] KeyBindings Registered");
    }

    public static void registerKeyBindings()
    {
        ClientRegistry.registerKeyBinding(KeyBindings.open_gui);
    }

}
