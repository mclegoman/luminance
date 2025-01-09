/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.logo;

import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.common.data.Data;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class LogoHelper {
	public static void renderDevelopmentOverlay(DrawContext context, int x, int y, int width, int height, boolean shouldRender, int xOffset, int yOffset) {
		if (shouldRender) context.drawTexture(RenderLayer::getGuiTextured, Identifier.of(Data.version.getID(), "textures/gui/logo/development.png"), x + xOffset, y + yOffset, 0.0F, 0.0F, (int) (width * 0.75F), height / 4, (int) (width * 0.75F), height / 4);
	}
	public static void renderDevelopmentOverlay(DrawContext context, int x, int y, int width, int height, boolean shouldRender) {
		renderDevelopmentOverlay(context, x, y, width, height, shouldRender, 0, 0);
	}
	public static void createSplashText(DrawContext context, int width, int x, int y, TextRenderer textRenderer, Translation.Data splashText, float rotation) {
		if (splashText != null && !ClientData.minecraft.options.getHideSplashTexts().getValue()) {
			context.getMatrices().push();
			context.getMatrices().translate(x + width, y, 0.0F);
			context.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation));
			float scale = (1.8F - MathHelper.abs(MathHelper.sin((float)(Util.getMeasuringTimeMs() % 1000L) / 1000.0F * ((float)Math.PI * 2)) * 0.1F)) * 100.0F / (float)(textRenderer.getWidth(Translation.getText(splashText)) + 32);
			context.getMatrices().scale(scale, scale, scale);
			context.drawCenteredTextWithShadow(textRenderer, Translation.getText(splashText), 0, -8, 0xFFFF00);
			context.getMatrices().pop();
		}
	}
}