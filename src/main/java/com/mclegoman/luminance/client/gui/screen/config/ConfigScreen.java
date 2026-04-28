/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.gui.screen.config;

import com.mclegoman.luminance.client.LuminanceClient;
import com.mclegoman.luminance.client.config.LuminanceConfig;
import com.mclegoman.luminance.client.config.value.SpectatorPriorityModeValue;
import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.gui.screen.AbstractScrollableListScreen;
import com.mclegoman.luminance.client.gui.screen.CursorableStringWidget;
import com.mclegoman.luminance.client.gui.widget.AlphaSliderWidget;
import com.mclegoman.luminance.client.gui.widget.ListWidget;
import com.mclegoman.luminance.client.keybindings.Keybindings;
import com.mclegoman.luminance.client.shaders.SpectatorHandler;
import com.mclegoman.luminance.client.shaders.Uniforms;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.common.util.DateHelper;
import com.mclegoman.luminance.config.LuminanceConfigHelper;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.ChatFormatting;
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

	public ConfigScreen(Screen parent, double scrollY, @Nullable Translation.Data splashText, boolean isPride) {
		super("", parent, scrollY, splashText, isPride);
	}

	public static ConfigScreen open(Screen screen) {
		return new ConfigScreen(screen, 0, null, DateHelper.isPride());
	}

	public void initBody() {
		this.list = new ListWidget(ClientData.minecraft, this.width, this.layout.getContentHeight(), this.layout.getHeaderHeight(), 22, getEntries(), this.scrollY);
		this.layout.addToContents(this.list);
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

	public List<ListWidget.ListEntry> getEntries() {
		List<ListWidget.ListEntry> widgets = new ArrayList<>();

		widgets.add(new ListWidget.ListEntry(new CursorableStringWidget(Translation.getConfigTranslation(LuminanceClient.getMod().getId(), "config"), ClientData.minecraft.font)));

		AlphaSliderWidget alphaSliderWidget = new AlphaSliderWidget(0, 0, 150, 20, Uniforms.getRawAlpha() / 100.0F, () -> saveConfig = true);
		alphaSliderWidget.setTooltip(Tooltip.create(Translation.getConfigTranslation(LuminanceClient.getMod().getId(), "alpha", new Object[]{Translation.getConfigTranslation(LuminanceClient.getMod().getId(), "keybinding", new Object[]{Keybindings.adjustAlpha.getTranslatedKeyMessage()}, new ChatFormatting[]{ChatFormatting.RED, ChatFormatting.BOLD})}, true)));
		widgets.add(new ListWidget.ListEntry(alphaSliderWidget,
				Button.builder(Translation.getConfigTranslation(LuminanceClient.getMod().getId(), "alpha.show_overlay", new Object[]{Translation.getVariableTranslation(LuminanceClient.getMod().getId(), "onff", LuminanceConfig.config.showAlphaLevelOverlay.value())}), (button) -> {
					LuminanceConfig.config.showAlphaLevelOverlay.setValue(!LuminanceConfig.config.showAlphaLevelOverlay.value(), true);
					button.setMessage(Translation.getConfigTranslation(LuminanceClient.getMod().getId(), "alpha.show_overlay", new Object[]{Translation.getVariableTranslation(LuminanceClient.getMod().getId(), "onff", LuminanceConfig.config.showAlphaLevelOverlay.value())}));
					button.setTooltip(Tooltip.create(Translation.getConfigTranslation(LuminanceClient.getMod().getId(), "alpha.show_overlay", true)));
				}).tooltip(Tooltip.create(Translation.getConfigTranslation(LuminanceClient.getMod().getId(), "alpha.show_overlay", true))).build()));

		List<AbstractWidget> widgets1 = new ArrayList<>();

		widgets1.add(Button.builder(Translation.getConfigTranslation(LuminanceClient.getMod().getId(), "spectator_priority_mode", new Object[]{LuminanceConfig.config.spectatorPriorityMode.value().getRepresentation()}), button -> {
			LuminanceConfig.config.spectatorPriorityMode.setValue(SpectatorPriorityModeValue.of(switch (LuminanceConfig.config.spectatorPriorityMode.value().getMode()) {
				case FIRST -> SpectatorHandler.Mode.EQUAL;
				case EQUAL -> SpectatorHandler.Mode.ALL;
				case ALL -> SpectatorHandler.Mode.FIRST;
			}), false);

			if (ClientData.minecraft.getCameraEntity() != null) SpectatorHandler.onSpectate(ClientData.minecraft.getCameraEntity(), LuminanceConfig.config.spectatorPriorityMode.value().getMode());
			this.saveConfig = true;

			button.setMessage(Translation.getConfigTranslation(LuminanceClient.getMod().getId(), "spectator_priority_mode", new Object[]{LuminanceConfig.config.spectatorPriorityMode.value().getRepresentation()}));
			button.setTooltip(Tooltip.create(Translation.getConfigTranslation(LuminanceClient.getMod().getId(), "spectator_priority_mode." + LuminanceConfig.config.spectatorPriorityMode.value().getRepresentation().toLowerCase(), true)));
		}).tooltip(Tooltip.create(Translation.getConfigTranslation(LuminanceClient.getMod().getId(), "spectator_priority_mode." + LuminanceConfig.config.spectatorPriorityMode.value().getRepresentation().toLowerCase(), true))).build());

		if (ClientData.isDevelopment()) widgets1.add(Button.builder(Translation.getConfigTranslation(LuminanceClient.getMod().getId(), "debug").append(getMore()), (button) -> ClientData.minecraft.setScreen(new DebugShaderScreen(getRefreshScreen()))).build());

		widgets.add(new ListWidget.ListEntry(widgets1.toArray(new AbstractWidget[0])));

		widgets.add(new ListWidget.ListEntry(Button.builder(Translation.getConfigTranslation(LuminanceClient.getMod().getId(), "reset"), (button) -> {
			LuminanceConfigHelper.reset(LuminanceConfig.config, false);
			this.saveConfig = true;
			this.refresh = true;
		}).build()));

		widgets.add(new ListWidget.ListEntry(new CursorableStringWidget(Translation.getConfigTranslation(LuminanceClient.getMod().getId(), "information"), ClientData.minecraft.font)));
		widgets.add(new ListWidget.ListEntry(Button.builder(Translation.getConfigTranslation(LuminanceClient.getMod().getId(), "information.source_code").append(getExternal()), ConfirmLinkScreen.confirmLink(this, "https://github.com/mclegoman/luminance")).width(304).build(),
				Button.builder(Translation.getConfigTranslation(LuminanceClient.getMod().getId(), "information.report").append(getExternal()), ConfirmLinkScreen.confirmLink(this, "https://github.com/mclegoman/luminance/issues")).width(304).build()));
		widgets.add(new ListWidget.ListEntry(Button.builder(Translation.getConfigTranslation(LuminanceClient.getMod().getId(), "credits_attribution").append(getMore()), button -> ClientData.minecraft.setScreen(new CreditsAttributionScreen(getRefreshScreen(), 0, splashText, isPride))).width(304).build()));
		return widgets;
	}

	public Screen getRefreshScreen() {
		return new ConfigScreen(this.parent, this.list != null ? this.list.scrollAmount() : scrollY, this.splashText, this.isPride);
	}

	// make sure shaders update properly while in the config screens
	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
