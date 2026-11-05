package xpncvr.fov360.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xpncvr.fov360.Fov360Renderer;

@Mixin(ScreenEffectRenderer.class)
public abstract class ScreenEffectRendererMixin {

	@Inject(method = "renderScreenEffect", at = @At("HEAD"), cancellable = true)
	private static void panini$cancelScreenEffectsDuringCapture(Minecraft minecraft, PoseStack poseStack, CallbackInfo ci) {
		if (Fov360Renderer.capturing) {
			ci.cancel();
		}
	}
}
