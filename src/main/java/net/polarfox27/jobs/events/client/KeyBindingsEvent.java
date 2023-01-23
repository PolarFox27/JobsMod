package net.polarfox27.jobs.events.client;


import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.polarfox27.jobs.gui.screens.MainJobsMenu;
import net.polarfox27.jobs.network.PacketAskClientUpdate;
import net.polarfox27.jobs.util.handler.PacketHandler;
import net.polarfox27.jobs.util.keybindings.KeyBindings;
import net.polarfox27.jobs.util.keybindings.KeyBindings.Key;

@EventBusSubscriber
public class KeyBindingsEvent {


    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void handleKeyBindings(KeyInputEvent event) {
        Key keyPressed = getPressedKey();
        if(keyPressed == Key.OPEN_GUI)
            if(Minecraft.getInstance().screen == null){
                PacketHandler.INSTANCE.sendToServer(PacketAskClientUpdate.instance);
                Minecraft.getInstance().setScreen(new MainJobsMenu());
            }

    }


    private static Key getPressedKey() {
        return KeyBindings.open_gui.isDown() ? Key.OPEN_GUI : Key.NONE;
    }
}
