/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.screen.config;

import com.mclegoman.luminance.client.config.LuminanceConfig;
import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.debug.Debug;
import com.mclegoman.luminance.client.keybindings.Keybindings;
import com.mclegoman.luminance.client.logo.LuminanceLogo;
import com.mclegoman.luminance.client.screen.config.information.InformationScreen;
import com.mclegoman.luminance.client.shaders.Shader;
import com.mclegoman.luminance.client.shaders.Uniforms;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.LogType;
import com.mclegoman.luminance.config.LuminanceConfigHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.*;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

public class ConfigScreen extends Screen {
	private final Screen parentScreen;
	private final GridWidget grid;
	private boolean refresh;
	private boolean shouldClose;
	private boolean saveConfig;
	private boolean shouldRenderSplashText;
	private Translation.Data splashText;
	private final boolean isPride;
	public ConfigScreen(Screen parent, boolean refresh, boolean saveConfig, Translation.Data splashText, boolean isPride) {
		super(Text.literal(""));
		this.grid = new GridWidget();
		this.parentScreen = parent;
		this.refresh = refresh;
		this.saveConfig = saveConfig;
		if (splashText != null) {
			this.splashText = splashText;
			this.shouldRenderSplashText = true;
		}
		this.isPride = isPride;
	}
	public ConfigScreen(Screen parent, boolean refresh, Translation.Data splashText, boolean isPride) {
		this(parent, refresh, false, splashText, isPride);
	}
	public ConfigScreen(Screen parent, Translation.Data splashText, boolean isPride) {
		this(parent, false, false, splashText, isPride);
	}
	public ConfigScreen(Screen parent, boolean refresh, boolean saveConfig, boolean isPride) {
		this(parent, refresh, saveConfig, null, isPride);
	}
	public ConfigScreen(Screen parent, boolean refresh, boolean isPride) {
		this(parent, refresh, false, null, isPride);
	}
	public ConfigScreen(Screen parent, boolean isPride) {
		this(parent, false, false, null, isPride);
	}
	public void init() {
		try {
			grid.getMainPositioner().alignHorizontalCenter().margin(0);
			GridWidget.Adder gridAdder = grid.createAdder(1);
			gridAdder.add(new LuminanceLogo.Widget(shouldRenderSplashText, splashText, isPride));
			gridAdder.add(createConfig());
			gridAdder.add(new EmptyWidget(4, 4));
			gridAdder.add(createFooter());
			grid.refreshPositions();
			grid.forEachChild(this::addDrawableChild);
			initTabNavigation();
		} catch (Exception error) {
			Data.version.sendToLog(LogType.ERROR, Translation.getString("Failed to initialize config screen: {}", error));
		}
	}
	public void tick() {
		try {
			if (this.refresh) {
				ClientData.minecraft.setScreen(new ConfigScreen(parentScreen, false, this.saveConfig, this.splashText, this.isPride));
			}
			if (this.shouldClose) {
				if (this.saveConfig) LuminanceConfig.config.save();
				ClientData.minecraft.setScreen(parentScreen);
			}
		} catch (Exception error) {
			Data.version.sendToLog(LogType.ERROR, Translation.getString("Failed to tick luminance$config screen: {}", error));
		}
	}
	private GridWidget createConfig() {
		GridWidget grid = new GridWidget();
		grid.getMainPositioner().alignHorizontalCenter().margin(2);
		GridWidget.Adder gridAdder = grid.createAdder(2);
		gridAdder.add(new SliderWidget(gridAdder.getGridWidget().getX(), gridAdder.getGridWidget().getY(), 150, 20, Translation.getConfigTranslation(Data.version.getID(), "alpha", new Object[]{Text.literal(Uniforms.getRawAlpha() + "%")}, false), Uniforms.getRawAlpha() / 100.0F) {
			@Override
			protected void updateMessage() {
				setMessage(Translation.getConfigTranslation(Data.version.getID(),  "alpha", new Object[]{Text.literal(Uniforms.getRawAlpha() + "%")}, false));
			}
			@Override
			protected void applyValue() {
				LuminanceConfig.config.alphaLevel.setValue((int) ((value) * 100), false);
				saveConfig = true;
			}
		}, 1).setTooltip(Tooltip.of(Translation.getConfigTranslation(Data.version.getID(), "alpha", new Object[]{Translation.getConfigTranslation(Data.version.getID(), "keybinding", new Object[]{Keybindings.adjustAlpha.getBoundKeyLocalizedText()}, new Formatting[]{Formatting.RED, Formatting.BOLD})}, true)));
		gridAdder.add(ButtonWidget.builder(Translation.getConfigTranslation(Data.version.getID(), "alpha.show_overlay", new Object[]{Translation.getVariableTranslation(Data.version.getID(), "onff", LuminanceConfig.config.showAlphaLevelOverlay.value())}), (button) -> {
			LuminanceConfig.config.showAlphaLevelOverlay.setValue(!LuminanceConfig.config.showAlphaLevelOverlay.value(), false);
			this.saveConfig = true;
			this.refresh = true;
		}).build(), 1).setTooltip(Tooltip.of(Translation.getConfigTranslation(Data.version.getID(), "alpha.show_overlay", true)));
		gridAdder.add(ButtonWidget.builder(Translation.getConfigTranslation(Data.version.getID(), "information"), button -> ClientData.minecraft.setScreen(new InformationScreen(ClientData.minecraft.currentScreen, false, splashText, isPride))).width(304).build(), 2);

		if (ClientData.isDevelopment) {
			gridAdder.add(ButtonWidget.builder(Translation.getText("Debug Shader: {}", false, new Object[]{Debug.debugShader}), button -> {
				Debug.debugShader = !Debug.debugShader;
				this.refresh = true;
			}).build());
			gridAdder.add(ButtonWidget.builder(Translation.getText("Debug Render Type: {}", false, new Object[]{Debug.debugRenderType.toString()}), button -> {
				switch (Debug.debugRenderType) {
					case GAME -> Debug.debugRenderType = Shader.RenderType.WORLD;
					case WORLD -> Debug.debugRenderType = Shader.RenderType.GAME;
				}
				this.refresh = true;
			}).build());
		}

		return grid;
	}
	private GridWidget createFooter() {
		GridWidget grid = new GridWidget();
		grid.getMainPositioner().alignHorizontalCenter().margin(2);
		GridWidget.Adder gridAdder = grid.createAdder(2);
		gridAdder.add(ButtonWidget.builder(Translation.getConfigTranslation(Data.version.getID(), "reset"), (button) -> {
			LuminanceConfigHelper.reset(LuminanceConfig.config, false);
			this.saveConfig = true;
			this.refresh = true;
		}).build());
		gridAdder.add(ButtonWidget.builder(Translation.getConfigTranslation(Data.version.getID(), "back"), (button) -> this.shouldClose = true).build());
		return grid;
	}
	public void initTabNavigation() {
		SimplePositioningWidget.setPos(grid, getNavigationFocus());
	}
	public Text getNarratedTitle() {
		return ScreenTexts.joinSentences();
	}
	public boolean shouldCloseOnEsc() {
		return false;
	}
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE) this.shouldClose = true;
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
	}
}