package net.polarfox27.jobs.util.keybindings;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class KeyBindings {


	public static final String CATEGORY = "keys.jobs.category";
	public static final String OPEN_GUI = "keys.jobs.open_gui";

	public static Lazy<KeyMapping> open_gui = Lazy.of(() -> new KeyMapping(OPEN_GUI,
			KeyConflictContext.IN_GAME,
			InputConstants.Type.KEYSYM,
			GLFW.GLFW_KEY_J,
			CATEGORY));
}
