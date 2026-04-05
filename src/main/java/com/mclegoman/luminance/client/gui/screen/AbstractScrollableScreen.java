/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.gui.screen;

import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.logo.LuminanceLogo;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.DateHelper;
import com.mclegoman.luminance.common.util.ReleaseType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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
		return Translation.getTranslation(Data.getVersion().getID(), "name");
	}
	
	public static Component getTitle(Component title) {
		return Translation.getConfigTranslation(Data.getVersion().getID(), "title", new Object[]{getName(""), title});
	}

	public static Component getSubtitle(String id) {
		return Translation.getConfigTranslation(Data.getVersion().getID(), id);
	}

	public static Component getMore() {
		return Translation.getConfigTranslation(Data.getVersion().getID(), "more");
	}

	public static Component getExternal() {
		return Translation.getConfigTranslation(Data.getVersion().getID(), "external");
	}

	public void resize(Minecraft client, int width, int height) {
		super.resize(width, height);
		client.setScreen(getRefreshScreen());
	}

	public Screen getRefreshScreen() {
		return null;
	}

	@Override
	public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		renderDevNotice(context);
	}

	public void renderDevNotice(GuiGraphics context) {
		if (!Data.getVersion().getType().equals(ReleaseType.RELEASE)) {
			context.drawString(this.font, Translation.getTranslation(Data.getVersion().getID(), "dev"), 2, this.height - 11, 0xFFAAAAAA);
			MutableComponent versionText = Translation.getTranslation(Data.getVersion().getID(), "dev.version", new Object[]{Data.getVersion().getFriendlyString()});
			context.drawString(this.font, versionText, this.width - 2 - this.font.width(versionText), this.height - 11, 0xFFAAAAAA);
		}
	}

	@Override
	public void resize(int width, int height) {
		this.minecraft.setScreen(getRefreshScreen());
	}
}
