package xpncvr.fov360.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xpncvr.fov360.Fov360Renderer;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

	@Inject(method = "prepareCullFrustum", at = @At("HEAD"), cancellable = true)
	private void panini$overrideCullFrustum(Vec3 cameraPosition, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
		Minecraft client = Minecraft.getInstance();
		if (Fov360Renderer.capturing) {
			float depthFar = client.gameRenderer.getDepthFar();
			Matrix4f cullProjection = new Matrix4f().perspective(
				Fov360Renderer.captureCullFov * (float) (Math.PI / 180.0),
				Fov360Renderer.captureAspect,
				0.05F,
				depthFar);
			Frustum frustum = new Frustum(modelViewMatrix, cullProjection);
			frustum.prepare(cameraPosition.x, cameraPosition.y, cameraPosition.z);
			((LevelRendererAccessor) this).panini$setCullFrustum(frustum);
			ci.cancel();
			return;
		}

		float requested = (float) client.options.fov().get();
		if (requested > 150.0F) {
			float depthFar = client.gameRenderer.getDepthFar();
			Matrix4f cullProjection = new Matrix4f().perspective(
				150.0F * (float) (Math.PI / 180.0),
				(float) client.getWindow().getWidth() / (float) client.getWindow().getHeight(),
				0.05F,
				depthFar);
			Frustum frustum = new Frustum(modelViewMatrix, cullProjection);
			frustum.prepare(cameraPosition.x, cameraPosition.y, cameraPosition.z);
			((LevelRendererAccessor) this).panini$setCullFrustum(frustum);
			ci.cancel();
		}
	}
}
