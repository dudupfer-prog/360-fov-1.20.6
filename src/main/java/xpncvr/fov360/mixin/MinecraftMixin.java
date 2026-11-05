package xpncvr.fov360.mixin;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xpncvr.fov360.Fov360Config;
import xpncvr.fov360.Fov360Keys;
import xpncvr.fov360.Fov360OptionsScreen;
import xpncvr.fov360.Fov360Renderer;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
	@Shadow
	@Final
	public Options options;

	@Inject(method = "handleKeybinds", at = @At("HEAD"))
	private void fov360$openSettings(CallbackInfo ci) {
		Minecraft client = (Minecraft) (Object) this;
		while (Fov360Keys.OPEN_SETTINGS.consumeClick()) {
			if (client.screen == null) {
				client.setScreen(new Fov360OptionsScreen(null, options));
			}
		}
		while (Fov360Keys.TOGGLE_REAR_VIEW.consumeClick()) {
			Fov360Config config = Fov360Renderer.currentConfig();
			config.rearOverlay = !config.rearOverlay;
			config.save();
		}
	}

	@Inject(method = "getMainRenderTarget", at = @At("HEAD"), cancellable = true)
	private void panini$redirectMainRenderTarget(CallbackInfoReturnable<RenderTarget> cir) {
		RenderTarget target = Fov360Renderer.currentTarget;
		if (target != null) {
			cir.setReturnValue(target);
		}
	}
}
