package xpncvr.fov360.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.joml.Quaternionf;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xpncvr.fov360.Fov360Renderer;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {

	@Redirect(
		method = "render",
		at = @At(
			value = "FIELD",
			opcode = Opcodes.GETFIELD,
			target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;cameraOrientation:Lorg/joml/Quaternionf;"))
	private Quaternionf panini$fireTowardEye(EntityRenderDispatcher dispatcher, Entity entity, double x, double y, double z, float rotationYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		return Fov360Renderer.billboardOrientation(poseStack, dispatcher.cameraOrientation(), true);
	}
}
