package net.polarfox27.jobs.util.proxy;


import net.polarfox27.jobs.util.keybindings.KeyBindings;
import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {

    public void registerKeyBindings() {
    	KeyBindings.open_gui = new KeyBinding(KeyBindings.OPEN_GUI, Keyboard.KEY_J, KeyBindings.CATEGORY);
		ClientRegistry.registerKeyBinding(KeyBindings.open_gui);
    }

}
