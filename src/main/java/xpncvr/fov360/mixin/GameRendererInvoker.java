package xpncvr.fov360.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRenderer.class)
public interface GameRendererInvoker {

	@Invoker("renderItemInHand")
	void panini$renderItemInHand(PoseStack poseStack, Camera camera, float partialTick);

	@Invoker("getDepthFar")
	float panini$getDepthFar();
}
