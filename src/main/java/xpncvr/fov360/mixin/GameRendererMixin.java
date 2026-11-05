package xpncvr.fov360.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xpncvr.fov360.Fov360Renderer;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

	@Inject(method = "renderLevel", at = @At("HEAD"), cancellable = true)
	private void panini$driveLevel(DeltaTracker deltaTracker, CallbackInfo ci) {
		if (Fov360Renderer.capturing) {
			return;
		}
		Minecraft client = Minecraft.getInstance();
		if (!Fov360Renderer.willCapture(client)) {
			return;
		}
		if (Fov360Renderer.INSTANCE.runFrame((GameRenderer) (Object) this, deltaTracker)) {
			ci.cancel();
		}
	}

	@Inject(method = "renderLevel", at = @At("TAIL"))
	private void panini$rearOverlay(DeltaTracker deltaTracker, CallbackInfo ci) {
		if (!Fov360Renderer.capturing) {
			Fov360Renderer.INSTANCE.renderRearOverlay((GameRenderer) (Object) this, deltaTracker, false);
		}
	}

	@Inject(method = "renderItemInHand", at = @At("HEAD"), cancellable = true)
	private void panini$cancelHandDuringCapture(PoseStack poseStack, Camera camera, float partialTick, CallbackInfo ci) {
		if (Fov360Renderer.capturing || Fov360Renderer.suppressHand) {
			ci.cancel();
		}
	}

	@Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
	private void panini$cancelBob(PoseStack poseStack, float partialTicks, CallbackInfo ci) {
		if (Fov360Renderer.capturing) {
			ci.cancel();
		}
	}

	@Inject(method = "bobHurt", at = @At("HEAD"), cancellable = true)
	private void panini$cancelTilt(PoseStack poseStack, float partialTicks, CallbackInfo ci) {
		if (Fov360Renderer.capturing) {
			ci.cancel();
		}
	}

	@Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
	private void panini$matchRectilinearFov(Camera camera, float partialTicks, boolean useFovSetting, CallbackInfoReturnable<Double> cir) {
		if (Fov360Renderer.capturing) {
			cir.setReturnValue((double) Fov360Renderer.captureFov);
			return;
		}
		if (!useFovSetting) {
			return;
		}
		float scale = Fov360Renderer.vanillaFovScale(Minecraft.getInstance());
		if (scale != 1.0F) {
			cir.setReturnValue(cir.getReturnValueD() * (double) scale);
		}
	}

	@Inject(method = "getProjectionMatrix", at = @At("HEAD"), cancellable = true)
	private void panini$captureProjectionMatrix(double fov, CallbackInfoReturnable<Matrix4f> cir) {
		if (Fov360Renderer.capturing) {
			cir.setReturnValue(new Matrix4f().perspective(
				Fov360Renderer.captureFov * (float) (Math.PI / 180.0),
				Fov360Renderer.captureAspect,
				CameraMixin.panini$captureNear(),
				((GameRendererInvoker) this).panini$getDepthFar()));
		}
	}

	@ModifyArg(
		method = "render",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/systems/RenderSystem;setProjectionMatrix(Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/vertex/VertexSorting;)V"),
		index = 0)
	private Matrix4f panini$rightHalfProjection(Matrix4f original) {
		if (Fov360Renderer.splitGuiOnRight()) {
			Matrix4f shifted = Fov360Renderer.rightHalfGuiProjection(Minecraft.getInstance().getWindow());
			if (shifted != null) {
				return shifted;
			}
		}
		return original;
	}
}
