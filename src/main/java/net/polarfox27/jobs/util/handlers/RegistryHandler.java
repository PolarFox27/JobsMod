package net.polarfox27.jobs.util.handlers;

import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.polarfox27.jobs.commands.CommandAdd;
import net.polarfox27.jobs.commands.CommandInfo;
import net.polarfox27.jobs.commands.CommandSet;

@EventBusSubscriber
public class RegistryHandler {

    public static void registerCommands(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandAdd());
        event.registerServerCommand(new CommandInfo());
        event.registerServerCommand(new CommandSet());
    }
}
