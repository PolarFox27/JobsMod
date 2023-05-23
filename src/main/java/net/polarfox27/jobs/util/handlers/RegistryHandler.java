package com.dorian2712.jobs.util.handlers;

import com.dorian2712.jobs.commands.CommandAdd;
import com.dorian2712.jobs.commands.CommandInfo;
import com.dorian2712.jobs.commands.CommandSet;
import com.dorian2712.jobs.items.ModItems;
import com.dorian2712.jobs.items.ModItems.IHasModel;

import net.minecraft.item.Item;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class RegistryHandler {

    public static void registerCommands(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandAdd());
        event.registerServerCommand(new CommandInfo());
        event.registerServerCommand(new CommandSet());
    }
    
    @SubscribeEvent
	public static void onItemRegister(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().registerAll(ModItems.ITEMS.toArray(new Item[0]));
		System.out.println(TextFormatting.GREEN + "[WK] Items Registered");
	}
    
    @SubscribeEvent
	public static void onModelRegister(ModelRegistryEvent event)
	{
		for(Item item : ModItems.ITEMS)
		{
			if(item instanceof IHasModel)
			{
				((IHasModel)item).registerModel();
			}
		}
		System.out.println(TextFormatting.GREEN + "[WK] Models Registered");
	}
}
