package xpncvr.fov360.mixin;

import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xpncvr.fov360.Fov360Renderer;

@Mixin(Camera.class)
public abstract class CameraMixin {

	@Unique
	private static final float PANINI_MAX_VANILLA_FOV = 150.0F;

	@Unique
	private static final float CUBE_CORNER_NEAR_DISTANCE_SQ = 3.0F;

	@Unique
	private static final double CAPTURE_WALL_MARGIN = 0.2;

	@Unique
	private boolean panini$applyingCaptureRotation = false;

	@Shadow
	protected abstract void setRotation(float yRot, float xRot);

	@Shadow
	protected abstract void setPosition(Vec3 position);

	@Inject(method = "setup", at = @At("TAIL"))
	private void panini$overrideCaptureView(BlockGetter level, Entity entity, boolean detached, boolean thirdPersonReverse, float partialTick, CallbackInfo ci) {
		if (!Fov360Renderer.capturing) {
			return;
		}
		Camera self = (Camera) (Object) this;
		if (detached) {
			panini$keepOffWall(self, partialTick);
		}
		this.panini$applyingCaptureRotation = true;
		try {
			this.setRotation(Fov360Renderer.captureFaceYaw, Fov360Renderer.captureFacePitch);
		} finally {
			this.panini$applyingCaptureRotation = false;
		}
	}

	@Unique
	private void panini$keepOffWall(Camera self, float partialTicks) {
		Vec3 pos = self.getPosition();
		Vec3 forward = new Vec3(self.getLookVector());
		Vec3 eye = self.getEntity().getEyePosition(partialTicks);
		BlockHitResult hit = self.getEntity().level().clip(new ClipContext(
			pos,
			pos.subtract(forward.scale(CAPTURE_WALL_MARGIN)),
			ClipContext.Block.VISUAL,
			ClipContext.Fluid.NONE,
			self.getEntity()));
		double clearance = hit.getType() == HitResult.Type.MISS ? CAPTURE_WALL_MARGIN : hit.getLocation().distanceTo(pos);
		double push = Math.min(CAPTURE_WALL_MARGIN - clearance, pos.distanceTo(eye));
		if (push > 0.0) {
			this.setPosition(pos.add(forward.scale(push)));
		}
	}

	public static float panini$captureNear() {
		float tanY = (float) Math.tan(Fov360Renderer.captureFov * (Math.PI / 360.0));
		float tanX = tanY * Fov360Renderer.captureAspect;
		float cornerSq = 1.0F + tanX * tanX + tanY * tanY;
		return 0.05F * (float) Math.sqrt(Math.min(1.0F, CUBE_CORNER_NEAR_DISTANCE_SQ / cornerSq));
	}

	@Redirect(
		method = "setRotation",
		at = @At(value = "INVOKE", target = "Lorg/joml/Quaternionf;rotationYXZ(FFF)Lorg/joml/Quaternionf;"))
	private Quaternionf panini$captureRotation(Quaternionf rotation, float yaw, float pitch, float roll) {
		if (this.panini$applyingCaptureRotation && Fov360Renderer.captureUseRotation) {
			return rotation.set(Fov360Renderer.captureRotation);
		}
		return rotation.rotationYXZ(yaw, pitch, roll);
	}

	@Inject(method = "getFluidInCamera", at = @At("HEAD"), cancellable = true)
	private void panini$stableFogType(CallbackInfoReturnable<FogType> cir) {
		if (Fov360Renderer.capturing) {
			cir.setReturnValue(Fov360Renderer.capturedFogType);
		}
	}
}
