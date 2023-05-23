package net.polarfox27.jobs.events.client;

import net.polarfox27.jobs.data.ClientInfos;
import net.polarfox27.jobs.gui.screens.MainJobsMenu;
import net.polarfox27.jobs.items.ModItems;
import net.polarfox27.jobs.util.keybindings.KeyBindings;
import net.polarfox27.jobs.util.keybindings.Keys.Key;
import net.polarfox27.jobs.network.PacketAskClientUpdate;
import net.polarfox27.jobs.util.handlers.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber
public class KeyBindingsEvent {

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void handleKeyBindings(KeyInputEvent event)
    {
        Key keyPressed = getPressedKey();

        if(keyPressed == Key.OPEN_GUI)
        {
            if(Minecraft.getMinecraft().currentScreen == null)
            {
            	if(!(ClientInfos.config.isHard()))
            	{
            		PacketHandler.INSTANCE.sendToServer(new PacketAskClientUpdate());
                    Minecraft.getMinecraft().displayGuiScreen(new MainJobsMenu());
            	}
            	else
            	{
            		String name = new ItemStack(ModItems.JOBS_BOOK).getDisplayName();
            		Minecraft.getMinecraft().player.sendMessage(new TextComponentString(TextFormatting.RED + 
            				I18n.translateToLocal("text.need_book") + " " + name + " !"));
            	}
            }
        }

    }


    private static Key getPressedKey()
    {
        if(KeyBindings.open_gui == null) return Key.NONE;
        if(KeyBindings.open_gui.isPressed())
            return Key.OPEN_GUI;

        else return Key.NONE;
    }
}
