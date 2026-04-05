/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.logo;

import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.DateHelper;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class LuminanceLogo {
	public static Logo getLogo() {
		return getLogo(DateHelper.isPride());
	}
	public static Logo getLogo(boolean isPride) {
		return new Logo(Identifier.fromNamespaceAndPath(Data.getVersion().getID(), Data.getVersion().getID()), isPride ? "pride" : "normal");
	}
	public static void renderLogo(GuiGraphics context, int x, int y, int width, int height, boolean isPride) {
		context.blit(RenderPipelines.GUI_TEXTURED, getLogo(isPride).getTexture(), x, y, 0.0F, 0.0F, width, (int) (height * 0.6875), width, height);
		LogoHelper.renderDevelopmentOverlay(context, (int) ((x + ((float) width / 2)) - ((width * 0.75F) / 2)), (int) (y + (height - (height * 0.45F))), width, height, Data.getVersion().isDevelopmentBuild(), 0, 0);
	}
	public static void renderLogo(GuiGraphics context, int x, int y, int width, int height) {
		renderLogo(context, x, y, width, height, DateHelper.isPride());
	}
	public Logo Logo(Identifier id) {
		return new Logo(id, "");
	}
	public record Logo(Identifier id, String type) {
		public String getNamespace() {
			return this.id.getNamespace();
		}
		public String getName() {
			return this.id.getPath();
		}
		public String getType() {
			return this.type;
		}
		public Identifier getTexture() {
			return Identifier.fromNamespaceAndPath(getNamespace(), "textures/gui/logo/" + this.type + (this.type.endsWith("/") || this.type.isEmpty() ? "" : "/") + getName() + ".png");
		}
	}
	public static class Widget extends AbstractWidget {
		private final boolean shouldRenderSplashText;
		private final Translation.Data splashText;
		private final boolean isPride;
		public Widget(boolean shouldRenderSplashText, Translation.Data splashText, boolean isPride) {
			super(0, 0, 256, 64, Component.empty());
			this.shouldRenderSplashText = shouldRenderSplashText;
			this.splashText = splashText;
			this.isPride = isPride;
		}
		public Widget(boolean shouldRenderSplashText, Translation.Data splashText) {
			this (shouldRenderSplashText, splashText, DateHelper.isPride());
		}
		public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
			renderLogo(context, this.getX(), this.getY(), this.getWidth(), this.getHeight(), isPride);
			if (shouldRenderSplashText) LogoHelper.createSplashText(context, this.getWidth(), this.getX(), this.getY() + 32, ClientData.minecraft.font, splashText, -20.0F);
		}
		@Override
		protected void updateWidgetNarration(NarrationElementOutput builder) {
		}
		@Override
		protected boolean isValidClickButton(MouseButtonInfo input) {
			return false;
		}
	}
}
