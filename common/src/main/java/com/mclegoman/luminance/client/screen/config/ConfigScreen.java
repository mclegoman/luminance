/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.screen.config;

import com.mclegoman.luminance.client.config.LuminanceConfig;
import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.debug.Debug;
import com.mclegoman.luminance.client.keybindings.Keybindings;
import com.mclegoman.luminance.client.logo.LuminanceLogo;
import com.mclegoman.luminance.client.screen.config.information.InformationScreen;
import com.mclegoman.luminance.client.shaders.Shaders;
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
import net.minecraft.util.Identifier;
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
	private TextFieldWidget debugShaderRegistry;
	private TextFieldWidget debugShader;
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
			Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to initialize config screen: {}", error));
		}
	}
	private void updateShader() {
		if (ClientData.isDevelopment()) {
			if (this.debugShader != null) {
				try {
					Debug.setDebugShader((this.debugShaderRegistry != null && !Identifier.of(this.debugShaderRegistry.getText()).getPath().equalsIgnoreCase("")) ? Identifier.of(this.debugShaderRegistry.getText()) : Shaders.getMainRegistryId(), !Identifier.of(this.debugShader.getText()).getPath().equalsIgnoreCase("") ? Identifier.of(this.debugShader.getText()) : Identifier.of("box_blur"));
				} catch (Exception error) {
					Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to update debug shader: {}", error));
					Debug.setDebugShader(Shaders.getMainRegistryId(), Identifier.of("box_blur"));
				}
			}
		}
	}
	public void tick() {
		try {
			if (this.refresh) {
				ClientData.minecraft.setScreen(new ConfigScreen(parentScreen, false, this.saveConfig, this.splashText, this.isPride));
				updateShader();
			}
			if (this.shouldClose) {
				updateShader();
				if (this.saveConfig) LuminanceConfig.config.save();
				ClientData.minecraft.setScreen(parentScreen);
			}
		} catch (Exception error) {
			Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to tick luminance$config screen: {}", error));
		}
	}
	private GridWidget createConfig() {
		GridWidget grid = new GridWidget();
		grid.getMainPositioner().alignHorizontalCenter().margin(2);
		GridWidget.Adder gridAdder = grid.createAdder(2);
		gridAdder.add(new SliderWidget(gridAdder.getGridWidget().getX(), gridAdder.getGridWidget().getY(), 150, 20, Translation.getConfigTranslation(Data.getVersion().getID(), "alpha", new Object[]{Text.literal(Uniforms.getRawAlpha() + "%")}, false), Uniforms.getRawAlpha() / 100.0F) {
			@Override
			protected void updateMessage() {
				setMessage(Translation.getConfigTranslation(Data.getVersion().getID(),  "alpha", new Object[]{Text.literal(Uniforms.getRawAlpha() + "%")}, false));
			}
			@Override
			protected void applyValue() {
				LuminanceConfig.config.alphaLevel.setValue((int) ((value) * 100), false);
				saveConfig = true;
			}
		}, 1).setTooltip(Tooltip.of(Translation.getConfigTranslation(Data.getVersion().getID(), "alpha", new Object[]{Translation.getConfigTranslation(Data.getVersion().getID(), "keybinding", new Object[]{Keybindings.adjustAlpha.getBoundKeyLocalizedText()}, new Formatting[]{Formatting.RED, Formatting.BOLD})}, true)));
		gridAdder.add(ButtonWidget.builder(Translation.getConfigTranslation(Data.getVersion().getID(), "alpha.show_overlay", new Object[]{Translation.getVariableTranslation(Data.getVersion().getID(), "onff", LuminanceConfig.config.showAlphaLevelOverlay.value())}), (button) -> {
			LuminanceConfig.config.showAlphaLevelOverlay.setValue(!LuminanceConfig.config.showAlphaLevelOverlay.value(), false);
			this.saveConfig = true;
			this.refresh = true;
		}).build(), 1).setTooltip(Tooltip.of(Translation.getConfigTranslation(Data.getVersion().getID(), "alpha.show_overlay", true)));
		gridAdder.add(ButtonWidget.builder(Translation.getConfigTranslation(Data.getVersion().getID(), "information"), button -> ClientData.minecraft.setScreen(new InformationScreen(ClientData.minecraft.currentScreen, false, splashText, isPride))).width(304).build(), 2);

		if (ClientData.isDevelopment()) {
			gridAdder.add(ButtonWidget.builder(Translation.getText("Debug Shader: {}", false, new Object[]{Debug.debugShaderEnabled}), button -> {
				Debug.debugShaderEnabled = !Debug.debugShaderEnabled;
				this.refresh = true;
			}).build());
			gridAdder.add(ButtonWidget.builder(Translation.getText("Debug Render Type: {}", false, new Object[]{Debug.debugRenderType.toString()}), button -> {
				Debug.cycleDebugRenderType();
				this.refresh = true;
			}).build());
			debugShaderRegistry = new TextFieldWidget(this.textRenderer, 148, 20, Text.literal(Debug.debugShader.getFirst().toString()));
			debugShaderRegistry.setText(Debug.debugShader.getFirst().toString());
			gridAdder.add(debugShaderRegistry);
			debugShader = new TextFieldWidget(this.textRenderer, 148, 20, Text.literal(Debug.debugShader.getSecond().toString()));
			debugShader.setText(Debug.debugShader.getSecond().toString());
			gridAdder.add(debugShader);
		}

		return grid;
	}
	private GridWidget createFooter() {
		GridWidget grid = new GridWidget();
		grid.getMainPositioner().alignHorizontalCenter().margin(2);
		GridWidget.Adder gridAdder = grid.createAdder(2);
		gridAdder.add(ButtonWidget.builder(Translation.getConfigTranslation(Data.getVersion().getID(), "reset"), (button) -> {
			LuminanceConfigHelper.reset(LuminanceConfig.config, false);
			this.saveConfig = true;
			this.refresh = true;
		}).build());
		gridAdder.add(ButtonWidget.builder(Translation.getConfigTranslation(Data.getVersion().getID(), "back"), (button) -> this.shouldClose = true).build());
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