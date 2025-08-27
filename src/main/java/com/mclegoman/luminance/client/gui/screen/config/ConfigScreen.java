/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.gui.screen.config;

import com.mclegoman.luminance.client.config.LuminanceConfig;
import com.mclegoman.luminance.client.config.value.SpectatorPriorityModeValue;
import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.gui.screen.AbstractScrollableListScreen;
import com.mclegoman.luminance.client.gui.widget.AlphaSliderWidget;
import com.mclegoman.luminance.client.gui.widget.ListWidget;
import com.mclegoman.luminance.client.keybindings.Keybindings;
import com.mclegoman.luminance.client.shaders.SpectatorHandler;
import com.mclegoman.luminance.client.shaders.Uniforms;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.config.LuminanceConfigHelper;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ConfigScreen extends AbstractScrollableListScreen {
	public ListWidget list;
	private boolean saveConfig;
	private boolean refresh;

	public ConfigScreen(Screen parent) {
		super(parent);
	}

	public ConfigScreen(Screen parent, double scrollY) {
		super(parent, scrollY);
	}

	public ConfigScreen(Screen parent, double scrollY, Translation.@Nullable Data splashText, boolean isPride) {
		super("", parent, scrollY, splashText, isPride);
	}

	public void initBody() {
		this.list = new ListWidget(ClientData.minecraft, this.width, this.layout.getContentHeight(), this.layout.getHeaderHeight(), 24, getWidgets(), this.scrollY);
		this.layout.addBody(this.list);
	}

	@Override
	public void tick() {
		super.tick();
		if (this.saveConfig) {
			LuminanceConfig.config.save();
			this.saveConfig = false;
		}
		if (this.refresh) {
			ClientData.minecraft.setScreen(getRefreshScreen());
			this.refresh = false;
		}
	}

	public List<ClickableWidget> getWidgets() {
		List<ClickableWidget> widgets = new ArrayList<>();

		widgets.add(new TextWidget(Translation.getConfigTranslation(Data.getVersion().getID(), "config"), ClientData.minecraft.textRenderer));

		AlphaSliderWidget alphaSliderWidget = new AlphaSliderWidget(0, 0, 150, 20, Uniforms.getRawAlpha() / 100.0F, () -> saveConfig = true);
		alphaSliderWidget.setTooltip(Tooltip.of(Translation.getConfigTranslation(Data.getVersion().getID(), "alpha", new Object[]{Translation.getConfigTranslation(Data.getVersion().getID(), "keybinding", new Object[]{Keybindings.adjustAlpha.getBoundKeyLocalizedText()}, new Formatting[]{Formatting.RED, Formatting.BOLD})}, true)));
		widgets.add(alphaSliderWidget);
		widgets.add(ButtonWidget.builder(Translation.getConfigTranslation(Data.getVersion().getID(), "alpha.show_overlay", new Object[]{Translation.getVariableTranslation(Data.getVersion().getID(), "onff", LuminanceConfig.config.showAlphaLevelOverlay.value())}), (button) -> {
			LuminanceConfig.config.showAlphaLevelOverlay.setValue(!LuminanceConfig.config.showAlphaLevelOverlay.value(), true);
			button.setMessage(Translation.getConfigTranslation(Data.getVersion().getID(), "alpha.show_overlay", new Object[]{Translation.getVariableTranslation(Data.getVersion().getID(), "onff", LuminanceConfig.config.showAlphaLevelOverlay.value())}));
			button.setTooltip(Tooltip.of(Translation.getConfigTranslation(Data.getVersion().getID(), "alpha.show_overlay", true)));
		}).tooltip(Tooltip.of(Translation.getConfigTranslation(Data.getVersion().getID(), "alpha.show_overlay", true))).build());
		widgets.add(ButtonWidget.builder(Translation.getConfigTranslation(Data.getVersion().getID(), "spectator_priority_mode", new Object[]{LuminanceConfig.config.spectatorPriorityMode.value().getRepresentation()}), button -> {
			LuminanceConfig.config.spectatorPriorityMode.setValue(SpectatorPriorityModeValue.of(switch (LuminanceConfig.config.spectatorPriorityMode.value().getMode()) {
				case FIRST -> SpectatorHandler.Mode.EQUAL;
				case EQUAL -> SpectatorHandler.Mode.ALL;
				case ALL -> SpectatorHandler.Mode.FIRST;
			}), false);
			if (ClientData.minecraft.cameraEntity != null) SpectatorHandler.onSpectate(ClientData.minecraft.cameraEntity, LuminanceConfig.config.spectatorPriorityMode.value().getMode());
			this.saveConfig = true;

			button.setMessage(Translation.getConfigTranslation(Data.getVersion().getID(), "spectator_priority_mode", new Object[]{LuminanceConfig.config.spectatorPriorityMode.value().getRepresentation()}));
			button.setTooltip(Tooltip.of(Translation.getConfigTranslation(Data.getVersion().getID(), "spectator_priority_mode." + LuminanceConfig.config.spectatorPriorityMode.value().getRepresentation().toLowerCase(), true)));
		}).tooltip(Tooltip.of(Translation.getConfigTranslation(Data.getVersion().getID(), "spectator_priority_mode." + LuminanceConfig.config.spectatorPriorityMode.value().getRepresentation().toLowerCase(), true))).build());

		if (ClientData.isDevelopment()) {
			widgets.add(ButtonWidget.builder(Translation.getConfigTranslation(Data.getVersion().getID(), "debug").append(getMore()), (button) -> {
				ClientData.minecraft.setScreen(new DebugShaderScreen(getRefreshScreen(), this.isPride));
			}).build());
		}

		widgets.add(ButtonWidget.builder(Translation.getConfigTranslation(Data.getVersion().getID(), "reset"), (button) -> {
			LuminanceConfigHelper.reset(LuminanceConfig.config, false);
			this.saveConfig = true;
			this.refresh = true;
		}).build());

		widgets.add(new TextWidget(Translation.getConfigTranslation(Data.getVersion().getID(), "information"), ClientData.minecraft.textRenderer));
		widgets.add(ButtonWidget.builder(Translation.getConfigTranslation(Data.getVersion().getID(), "information.source_code"), ConfirmLinkScreen.opening(this, "https://github.com/mclegoman/luminance")).width(304).build());
		widgets.add(ButtonWidget.builder(Translation.getConfigTranslation(Data.getVersion().getID(), "information.report"), ConfirmLinkScreen.opening(this, "https://github.com/mclegoman/luminance/issues")).width(304).build());
		widgets.add(ButtonWidget.builder(Translation.getConfigTranslation(Data.getVersion().getID(), "credits_attribution"), button -> ClientData.minecraft.setScreen(new CreditsAttributionScreen(ClientData.minecraft.currentScreen, 0, splashText, isPride))).width(304).build());
		return widgets;
	}

	public Screen getRefreshScreen() {
		return new ConfigScreen(this.parent, this.list != null ? this.list.getScrollY() : scrollY, this.splashText, this.isPride);
	}
}
