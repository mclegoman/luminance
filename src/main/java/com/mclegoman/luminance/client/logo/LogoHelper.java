/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.logo;

import com.mclegoman.luminance.client.LuminanceClient;
import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.translation.Translation;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Util;
import net.minecraft.util.Mth;

public class LogoHelper {
	public static void renderDevelopmentOverlay(GuiGraphics guiGraphics, int x, int y, int width, int height, boolean shouldRender, int xOffset, int yOffset) {
		if (shouldRender) guiGraphics.blit(RenderPipelines.GUI_TEXTURED, LuminanceClient.getMod().idOf("textures/gui/logo/development.png"), x + xOffset, y + yOffset, 0.0F, 0.0F, (int) (width * 0.75F), height / 4, (int) (width * 0.75F), height / 4);
	}
	public static void renderDevelopmentOverlay(GuiGraphics guiGraphics, int x, int y, int width, int height, boolean shouldRender) {
		renderDevelopmentOverlay(guiGraphics, x, y, width, height, shouldRender, 0, 0);
	}
	public static void createSplashText(GuiGraphics guiGraphics, int width, int x, int y, Font textRenderer, Translation.Data splashText, float rotation) {
		if (splashText != null && !ClientData.minecraft.options.hideSplashTexts().get()) {
			guiGraphics.pose().pushMatrix();
			guiGraphics.pose().translate(x + width, y);
			guiGraphics.pose().rotate(rotation);
			float scale = (1.8F - Mth.abs(Mth.sin((float)(Util.getMillis() % 1000L) / 1000.0F * ((float)Math.PI * 2)) * 0.1F)) * 100.0F / (float)(textRenderer.width(Translation.getText(splashText)) + 32);
			guiGraphics.pose().scale(scale, scale);
			guiGraphics.drawCenteredString(textRenderer, Translation.getText(splashText), 0, -8, 0xFFFF00);
			guiGraphics.pose().popMatrix();
		}
	}
}