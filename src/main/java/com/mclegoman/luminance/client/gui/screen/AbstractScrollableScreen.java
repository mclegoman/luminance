/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.gui.screen;

import com.mclegoman.luminance.client.LuminanceClient;
import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.logo.LuminanceLogo;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.DateHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractScrollableScreen extends Screen {
	public final Screen parent;
	public final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
	public final double scrollY;
	public final Translation.Data splashText;
	public final boolean isPride;

	public AbstractScrollableScreen(Screen parent) {
		this(parent, 0);
	}

	public AbstractScrollableScreen(Screen parent, double scrollY) {
		this(null, parent, scrollY, null, DateHelper.isPride());
	}

	public AbstractScrollableScreen(String id, Screen parent) {
		this(id, parent, 0, null, DateHelper.isPride());
	}

	public AbstractScrollableScreen(String id, Screen parent, double scrollY, @Nullable Translation.Data splashText, boolean isPride) {
		super(id != null ? getTitle(getSubtitle(id)) : getName(""));
		this.parent = parent;
		this.scrollY = scrollY;
		this.splashText = splashText;
		this.isPride = isPride;
	}

	public void init() {
		super.init();
		this.initHeader();
		this.initBody();
		this.initFooter();
		this.layout.visitWidgets(this::addRenderableWidget);
		repositionElements();
	}

	public void initHeader() {
		LuminanceLogo.Widget logo = new LuminanceLogo.Widget(this.splashText != null, this.splashText, this.isPride);
		this.layout.addToHeader(logo, (positioner) -> {
			this.layout.setHeaderHeight(logo.getHeight());
			positioner.paddingTop(11);
		});
	}

	public void initBody() {
	}

	public void initFooter() {
		this.layout.addToFooter(Button.builder(CommonComponents.GUI_BACK, (button) -> this.onClose()).width(200).build());
	}

	public void onClose() {
		ClientData.minecraft.setScreen(this.parent);
	}

	public void repositionElements() {
		this.layout.arrangeElements();
	}

	public static Component getName(String id) {
		return Translation.getText(LuminanceClient.getMod().getName(), false);
	}
	
	public static Component getTitle(Component title) {
		return Translation.getConfigTranslation(LuminanceClient.getMod().getId(), "title", new Object[]{getName(""), title});
	}

	public static Component getSubtitle(String id) {
		return Translation.getConfigTranslation(LuminanceClient.getMod().getId(), id);
	}

	public static Component getMore() {
		return Translation.getConfigTranslation(LuminanceClient.getMod().getId(), "more");
	}

	public static Component getExternal() {
		return Translation.getConfigTranslation(LuminanceClient.getMod().getId(), "external");
	}

	public void resize(int width, int height) {
		super.resize(width, height);
		Screen refreshScreen = getRefreshScreen();
		if (refreshScreen != null) this.minecraft.setScreen(refreshScreen);
	}

	public Screen getRefreshScreen() {
		return null;
	}

	@Override
	public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
		super.render(guiGraphics, mouseX, mouseY, delta);
		renderDevNotice(guiGraphics);
	}

	public String getDevVersion() {
		return Data.isDevelopmentBuild() ? Data.getFormattedVersion() : "";
	}

	public void renderDevNotice(GuiGraphics guiGraphics) {
		String version = getDevVersion();
		if (!version.isBlank()) {
			guiGraphics.drawString(this.font, Translation.getTranslation(LuminanceClient.getMod().getId(), "dev"), 2, this.height - 11, 0xFFAAAAAA);
			MutableComponent versionText = Translation.getTranslation(LuminanceClient.getMod().getId(), "dev.version", new Object[]{version});
			guiGraphics.drawString(this.font, versionText, this.width - 2 - this.font.width(versionText), this.height - 11, 0xFFAAAAAA);
		}
	}
}
