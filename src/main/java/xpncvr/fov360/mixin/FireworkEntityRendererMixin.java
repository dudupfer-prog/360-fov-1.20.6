package xpncvr.fov360.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.FireworkEntityRenderer;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xpncvr.fov360.Fov360Renderer;

@Mixin(FireworkEntityRenderer.class)
public abstract class FireworkEntityRendererMixin {

	@Redirect(
		method = "render(Lnet/minecraft/world/entity/projectile/FireworkRocketEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;cameraOrientation()Lorg/joml/Quaternionf;"))
	private Quaternionf panini$fireworkTowardEye(EntityRenderDispatcher dispatcher, FireworkRocketEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		return Fov360Renderer.billboardOrientation(poseStack, dispatcher.cameraOrientation(), false);
	}
}
