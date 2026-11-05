package xpncvr.fov360;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("fov360");

	@Override
	public void onInitializeClient() {
		KeyBindingHelper.registerKeyBinding(Fov360Keys.OPEN_SETTINGS);
		KeyBindingHelper.registerKeyBinding(Fov360Keys.TOGGLE_REAR_VIEW);
		LOGGER.info("360 FOV initialised");
	}
}
