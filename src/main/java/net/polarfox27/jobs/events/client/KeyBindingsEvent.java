package net.polarfox27.jobs.events.client;


import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.polarfox27.jobs.data.ClientJobsData;
import net.polarfox27.jobs.gui.screens.MainJobsMenu;
import net.polarfox27.jobs.network.PacketAskClientUpdate;
import net.polarfox27.jobs.util.config.JobsIconUtil;
import net.polarfox27.jobs.util.handlers.PacketHandler;
import net.polarfox27.jobs.util.keybindings.KeyBindings;
import net.polarfox27.jobs.util.keybindings.KeyBindings.Key;

@EventBusSubscriber
public class KeyBindingsEvent {


    /**
     * Opens the Jobs menu when the key is pressed
     * @param event the Key Pressed Event
     */
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void handleKeyBindings(InputEvent.KeyInputEvent event) {
        Key keyPressed = getPressedKey();
        if(keyPressed == Key.OPEN_GUI)
            if(Minecraft.getMinecraft().currentScreen == null){
                JobsIconUtil.loadJobsIconTextures(ClientJobsData.JOBS_ICONS);
                PacketHandler.INSTANCE.sendToServer(new PacketAskClientUpdate());
                Minecraft.getMinecraft().displayGuiScreen(new MainJobsMenu());
            }

    }


    /**
     * Returns the key pressed
     * @return the pressed key
     */
    private static Key getPressedKey() {
        return KeyBindings.open_gui.isKeyDown() ? Key.OPEN_GUI : Key.NONE;
    }
}
