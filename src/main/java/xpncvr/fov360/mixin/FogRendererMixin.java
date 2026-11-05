package xpncvr.fov360.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xpncvr.fov360.Fov360Renderer;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {

	@Redirect(
		method = "setupColor",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/Camera;getLookVector()Lorg/joml/Vector3f;"))
	private static Vector3f panini$sunsetTintFromTrueView(Camera camera) {
		if (Fov360Renderer.capturing) {
			return Fov360Renderer.captureViewForward;
		}
		return camera.getLookVector();
	}
}
