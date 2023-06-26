package net.polarfox27.jobs.events.client;


import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.polarfox27.jobs.gui.screens.MainJobsMenu;
import net.polarfox27.jobs.network.PacketAskClientUpdate;
import net.polarfox27.jobs.util.handler.PacketHandler;
import net.polarfox27.jobs.util.keybindings.KeyBindings;

@EventBusSubscriber
public class KeyBindingsEvent {


    /**
     * Opens the Jobs menu when the key is pressed
     * @param event the Key Pressed Event
     */
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void handleKeyBindings(TickEvent.ClientTickEvent event) {
        if(event.phase == TickEvent.Phase.END) {
            while (KeyBindings.open_gui.get().consumeClick()) {
                if (Minecraft.getInstance().screen == null && Minecraft.getInstance().player != null) {
                    PacketHandler.INSTANCE.sendToServer(PacketAskClientUpdate.instance);
                    Minecraft.getInstance().setScreen(new MainJobsMenu());
                }
            }
        }
    }
}
