package xpncvr.fov360.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xpncvr.fov360.Fov360Renderer;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {

	@Shadow private double xpos;

	@Inject(method = "xpos", at = @At("HEAD"), cancellable = true)
	private void panini$scaleXHalf(CallbackInfoReturnable<Double> cir) {
		if (Fov360Renderer.splitGuiActive()) {
			Window window = Minecraft.getInstance().getWindow();
			double originX = Fov360Renderer.splitGuiOnRight() ? window.getScreenWidth() / 2.0 : 0.0;
			cir.setReturnValue(Math.max(0.0, (this.xpos - originX) * 2.0));
		}
	}

	@Redirect(
		method = "releaseMouse",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/platform/InputConstants;grabOrReleaseMouse(JIDD)V"))
	private void panini$centerCursorOnForwardHalf(long handle, int mode, double x, double y) {
		Window window = Minecraft.getInstance().getWindow();
		if (Fov360Renderer.splitGuiActive()) {
			double forwardCenterX = window.getScreenWidth() * (Fov360Renderer.splitGuiOnRight() ? 0.75 : 0.25);
			this.xpos = forwardCenterX;
			InputConstants.grabOrReleaseMouse(handle, mode, forwardCenterX, y);
		} else {
			InputConstants.grabOrReleaseMouse(handle, mode, x, y);
		}
	}
}
