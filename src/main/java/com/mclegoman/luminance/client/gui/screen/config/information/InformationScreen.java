/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.gui.screen.config.information;

import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.gui.screen.AbstractScrollableListScreen;
import com.mclegoman.luminance.client.gui.widget.ListWidget;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.common.data.Data;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextWidget;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class InformationScreen extends AbstractScrollableListScreen {
	public ListWidget list;

	public InformationScreen(Screen parent) {
		super(parent);
	}

	public InformationScreen(Screen parent, double scrollY) {
		super(parent, scrollY);
	}

	public InformationScreen(Screen parent, double scrollY, Translation.@Nullable Data splashText, boolean isPride) {
		super("information", parent, scrollY, splashText, isPride);
	}

	public void initBody() {
		this.list = new ListWidget(ClientData.minecraft, this.width, this.layout.getContentHeight(), this.layout.getHeaderHeight(), 24, getWidgets(), this.scrollY);
		this.layout.addBody(this.list);
	}

	public List<ClickableWidget> getWidgets() {
		List<ClickableWidget> widgets = new ArrayList<>();
		widgets.add(new TextWidget(Translation.getConfigTranslation(Data.getVersion().getID(), "information"), ClientData.minecraft.textRenderer));
		widgets.add(ButtonWidget.builder(Translation.getConfigTranslation(Data.getVersion().getID(), "information.source_code"), ConfirmLinkScreen.opening(this, "https://github.com/mclegoman/luminance")).width(304).build());
		widgets.add(ButtonWidget.builder(Translation.getConfigTranslation(Data.getVersion().getID(), "information.report"), ConfirmLinkScreen.opening(this, "https://github.com/mclegoman/luminance/issues")).width(304).build());
		widgets.add(ButtonWidget.builder(Translation.getConfigTranslation(Data.getVersion().getID(), "credits_attribution"), button -> ClientData.minecraft.setScreen(new CreditsAttributionScreen(ClientData.minecraft.currentScreen, 0, splashText, isPride))).width(304).build());
		return widgets;
	}

	public Screen getRefreshScreen() {
		return new InformationScreen(this.parent, this.list != null ? this.list.getScrollY() : scrollY, this.splashText, this.isPride);
	}
}
