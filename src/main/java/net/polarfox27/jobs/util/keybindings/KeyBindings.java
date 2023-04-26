package net.polarfox27.jobs.util.keybindings;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.settings.KeyBindingMap;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

import javax.swing.text.JTextComponent;

@OnlyIn(Dist.CLIENT)
public class KeyBindings {


	public static final String CATEGORY = "keys.jobs.category";
	public static final String OPEN_GUI = "keys.jobs.open_gui";

	public static KeyMapping open_gui = new KeyMapping(OPEN_GUI,
													   KeyConflictContext.IN_GAME,
													   getKey(GLFW.GLFW_KEY_J),
													   CATEGORY);

	public enum Key{
		NONE, OPEN_GUI;
	}


	/**
	 * Returns the Input corresponding to a key code
	 * @param key the key to map.
	 * @return the input corresponding to that key code
	 */
	private static InputConstants.Key getKey(int key) {
		return InputConstants.Type.KEYSYM.getOrCreate(key);
	}

	/**
	 * registers the Jobs key binding
	 */
	public static void register() {
		ClientRegistry.registerKeyBinding(KeyBindings.open_gui);
	}


}
