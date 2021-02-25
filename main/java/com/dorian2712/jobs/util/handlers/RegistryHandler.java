package com.dorian2712.jobs.util.handlers;

import com.dorian2712.jobs.commands.CommandAdd;
import com.dorian2712.jobs.commands.CommandInfo;
import com.dorian2712.jobs.commands.CommandSet;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class RegistryHandler {

    public static void registerCommands(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandAdd());
        event.registerServerCommand(new CommandInfo());
        event.registerServerCommand(new CommandSet());
    }
}
