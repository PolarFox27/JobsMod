package net.polarfox27.jobs.util.proxy;


import net.polarfox27.jobs.util.keybindings.KeyBindings;
import net.polarfox27.jobs.util.keybindings.Keys;
import org.lwjgl.input.Keyboard;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {

    public ClientProxy()
    {}

    public void registerKeyBindings()
    {
    	KeyBindings.open_gui = new KeyBinding(Keys.OPEN_GUI, Keyboard.KEY_J, Keys.CATEGORY);
		ClientRegistry.registerKeyBinding(KeyBindings.open_gui);
		System.out.println(TextFormatting.BLUE + "[Jobs] KeyBindings Registered");
    }
    
    public void registerItemRenderer(Item item, int meta, String id)
	{
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), id));
	}

}
