package net.polarfox27.jobs.util.keybindings;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class KeyBindings {


	public static final String CATEGORY = "keys.jobs.category";
	public static final String OPEN_GUI = "keys.jobs.open_gui";

	public static KeyBinding open_gui = new KeyBinding(OPEN_GUI,
													   KeyConflictContext.IN_GAME,
													   KeyModifier.NONE,
													   getKey(GLFW.GLFW_KEY_J),
													   CATEGORY);

	public enum Key{
		NONE, OPEN_GUI
	}


	/**
	 * Returns the Input corresponding to a key code
	 * @param key the key to map.
	 * @return the input corresponding to that key code
	 */
	private static InputMappings.Input getKey(int key) {
		return InputMappings.Type.KEYSYM.getOrCreate(key);
	}

	/**
	 * registers the Jobs key binding
	 */
	public static void register() {
		ClientRegistry.registerKeyBinding(KeyBindings.open_gui);
	}


}
