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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractScrollableScreen extends Screen {
	public final Screen parent;
	public final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
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
		this.layout.forEachChild(this::addDrawableChild);
		refreshWidgetPositions();
	}

	public void initHeader() {
		LuminanceLogo.Widget logo = new LuminanceLogo.Widget(splashText != null, this.splashText, this.isPride);
		this.layout.addHeader(logo, (positioner) -> {
			this.layout.setHeaderHeight(logo.getHeight());
			positioner.marginTop(11);
		});
	}

	public void initBody() {
	}

	public void initFooter() {
		this.layout.addFooter(ButtonWidget.builder(ScreenTexts.BACK, (button) -> this.close()).width(200).build());
	}

	public void close() {
		ClientData.minecraft.setScreen(this.parent);
	}

	public void refreshWidgetPositions() {
		this.layout.refreshPositions();
	}

	public static Text getName(String id) {
		return Translation.getTranslation(Data.getVersion().getID(), "name");
	}
	
	public static Text getTitle(Text title) {
		return Translation.getConfigTranslation(Data.getVersion().getID(), "title", new Object[]{getName(""), title});
	}

	public static Text getSubtitle(String id) {
		return Translation.getConfigTranslation(Data.getVersion().getID(), id);
	}

	public static Text getMore() {
		return Translation.getConfigTranslation(Data.getVersion().getID(), "more");
	}

	public static Text getExternal() {
		return Translation.getConfigTranslation(Data.getVersion().getID(), "external");
	}

	public void resize(MinecraftClient client, int width, int height) {
		super.resize(client, width, height);
		client.setScreen(getRefreshScreen());
	}

	public Screen getRefreshScreen() {
		return null;
	}
}
