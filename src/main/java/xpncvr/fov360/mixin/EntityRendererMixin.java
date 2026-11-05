package xpncvr.fov360.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xpncvr.fov360.Fov360Renderer;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {

	private static final double CAPTURE_CULL_MARGIN = 16.0;

	@Unique
	private static final Quaternionf panini$nameTagLook = new Quaternionf();

	@Redirect(
		method = "shouldRender",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/culling/Frustum;isVisible(Lnet/minecraft/world/phys/AABB;)Z"))
	private boolean panini$captureCullMargin(Frustum frustum, AABB box) {
		if (Fov360Renderer.capturing) {
			return frustum.isVisible(box.inflate(CAPTURE_CULL_MARGIN));
		}
		return frustum.isVisible(box);
	}

	@Redirect(
		method = "renderNameTag",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;cameraOrientation()Lorg/joml/Quaternionf;"))
	private Quaternionf panini$nameTagTowardEye(EntityRenderDispatcher dispatcher, T entity, Component displayName, PoseStack poseStack, MultiBufferSource buffer, int packedLight, float partialTicks) {
		if (Fov360Renderer.capturing) {
			Matrix4f m = poseStack.last().pose();
			Fov360Renderer.billboardRotation(panini$nameTagLook, m.m30(), m.m31(), m.m32(), false);
			return panini$nameTagLook;
		}
		return dispatcher.cameraOrientation();
	}
}
