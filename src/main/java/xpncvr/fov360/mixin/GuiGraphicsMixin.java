package xpncvr.fov360.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import xpncvr.fov360.Fov360Renderer;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin {

	@ModifyVariable(
		method = "enableScissor",
		at = @At("HEAD"),
		argsOnly = true,
		ordinal = 0)
	private int panini$scissorRightShiftMinX(int minX) {
		if (Fov360Renderer.splitGuiOnRight()) {
			return minX + Minecraft.getInstance().getWindow().getWidth() / 2;
		}
		return minX;
	}

	@ModifyVariable(
		method = "enableScissor",
		at = @At("HEAD"),
		argsOnly = true,
		ordinal = 2)
	private int panini$scissorRightShiftMaxX(int maxX) {
		if (Fov360Renderer.splitGuiOnRight()) {
			return maxX + Minecraft.getInstance().getWindow().getWidth() / 2;
		}
		return maxX;
	}
}
