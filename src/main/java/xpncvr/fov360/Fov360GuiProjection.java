package xpncvr.fov360;

import com.mojang.blaze3d.platform.Window;
import org.joml.Matrix4f;

public final class Fov360GuiProjection {
	public Matrix4f matrix(Window window) {
		float w = (float) window.getWidth() / (float) window.getGuiScale();
		float h = (float) window.getHeight() / (float) window.getGuiScale();
		return new Matrix4f()
			.setOrtho(0.0F, w, h, 0.0F, 1000.0F, 21000.0F)
			.translate(w / 2.0F, 0.0F, 0.0F);
	}
}
