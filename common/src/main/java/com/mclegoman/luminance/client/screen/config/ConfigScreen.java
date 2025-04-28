/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.screen.config;

import com.mclegoman.luminance.client.config.LuminanceConfig;
import com.mclegoman.luminance.client.config.value.SpectatorPriorityModeValue;
import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.debug.Debug;
import com.mclegoman.luminance.client.keybindings.Keybindings;
import com.mclegoman.luminance.client.logo.LuminanceLogo;
import com.mclegoman.luminance.client.screen.config.information.InformationScreen;
import com.mclegoman.luminance.client.shaders.Shaders;
import com.mclegoman.luminance.client.shaders.SpectatorHandler;
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
	private boolean invis;
	private final Screen parentScreen;
	private final GridWidget grid;
	private boolean refresh;
	private boolean shouldClose;
	private boolean saveConfig;
	private Translation.Data splashText;
	private boolean shouldRenderSplashText;
	private final boolean isPride;
	private TextFieldWidget debugShaderRegistry;
	private TextFieldWidget debugShader;
	public ConfigScreen(Screen parent, boolean refresh, boolean saveConfig, Translation.Data splashText, boolean isPride) {
		this(parent, refresh, saveConfig, splashText, isPride, false);
	}
	public ConfigScreen(Screen parent, boolean refresh, boolean saveConfig, Translation.Data splashText, boolean isPride, boolean invis) {
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
		this.invis = invis;
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
		clearChildren();
		try {
			grid.getMainPositioner().alignHorizontalCenter().margin(0);
			GridWidget.Adder gridAdder = grid.createAdder(1);
			LuminanceLogo.Widget logo = new LuminanceLogo.Widget(shouldRenderSplashText, splashText, isPride);
			logo.visible = !this.invis;
			gridAdder.add(logo);
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
					Identifier shaderRegistry = (this.debugShaderRegistry != null && !Identifier.of(this.debugShaderRegistry.getText()).getPath().equalsIgnoreCase("")) ? Identifier.of(this.debugShaderRegistry.getText()) : Shaders.getMainRegistryId();
					Identifier shaderId = !Identifier.of(this.debugShader.getText()).getPath().equalsIgnoreCase("") ? Identifier.of(this.debugShader.getText()) : Identifier.of("box_blur");
					Shaders.guessPostShader(shaderRegistry, shaderId.toString()).ifPresentOrElse((postShader) -> Debug.setDebugShader(shaderRegistry, postShader.getID()), () -> Debug.setDebugShader(shaderRegistry, shaderId));
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
				updateShader();
				ClientData.minecraft.setScreen(new ConfigScreen(parentScreen, false, this.saveConfig, this.splashText, this.isPride, this.invis));
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
		ButtonWidget overlay = ButtonWidget.builder(Translation.getConfigTranslation(Data.getVersion().getID(), "alpha.show_overlay", new Object[]{Translation.getVariableTranslation(Data.getVersion().getID(), "onff", LuminanceConfig.config.showAlphaLevelOverlay.value())}), (button) -> {
			LuminanceConfig.config.showAlphaLevelOverlay.setValue(!LuminanceConfig.config.showAlphaLevelOverlay.value(), false);
			this.saveConfig = true;
			this.refresh = true;
		}).build();
		overlay.visible = !this.invis;
		gridAdder.add(overlay, 1).setTooltip(Tooltip.of(Translation.getConfigTranslation(Data.getVersion().getID(), "alpha.show_overlay", true)));

		gridAdder.add(ButtonWidget.builder(Translation.getConfigTranslation(Data.getVersion().getID(), "spectator_priority_mode", new Object[]{LuminanceConfig.config.spectatorPriorityMode.value().getRepresentation()}), button -> {
			LuminanceConfig.config.spectatorPriorityMode.setValue(SpectatorPriorityModeValue.of(switch (LuminanceConfig.config.spectatorPriorityMode.value().getMode()) {
				case FIRST -> SpectatorHandler.Mode.EQUAL;
				case EQUAL -> SpectatorHandler.Mode.ALL;
				case ALL -> SpectatorHandler.Mode.FIRST;
			}), false);
			if (ClientData.minecraft.cameraEntity != null) SpectatorHandler.onSpectate(ClientData.minecraft.cameraEntity, LuminanceConfig.config.spectatorPriorityMode.value().getMode());
			this.saveConfig = true;
			this.refresh = true;
		}).tooltip(Tooltip.of(Translation.getConfigTranslation(Data.getVersion().getID(), "spectator_priority_mode." + LuminanceConfig.config.spectatorPriorityMode.value().getRepresentation().toLowerCase(), true))).build());

		ButtonWidget information = ButtonWidget.builder(Translation.getConfigTranslation(Data.getVersion().getID(), "information"), button -> ClientData.minecraft.setScreen(new InformationScreen(ClientData.minecraft.currentScreen, false, splashText, isPride))).build();
		information.visible = !this.invis;
		gridAdder.add(information);

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
			debugShaderRegistry.setMaxLength(Integer.MAX_VALUE);
			debugShaderRegistry.setText(Debug.debugShader.getFirst().toString());
			gridAdder.add(debugShaderRegistry);
			debugShader = new TextFieldWidget(this.textRenderer, 148, 20, Text.literal(Debug.debugShader.getSecond().toString()));
			debugShader.setMaxLength(Integer.MAX_VALUE);
			debugShader.setText(Debug.debugShader.getSecond().toString());
			gridAdder.add(debugShader);
		}

		return grid;
	}
	private GridWidget createFooter() {
		GridWidget grid = new GridWidget();
		grid.getMainPositioner().alignHorizontalCenter().margin(2);
		GridWidget.Adder gridAdder = grid.createAdder(2);
		ButtonWidget reset = ButtonWidget.builder(Translation.getConfigTranslation(Data.getVersion().getID(), "reset"), (button) -> {
			LuminanceConfigHelper.reset(LuminanceConfig.config, false);
			this.saveConfig = true;
			this.refresh = true;
		}).build();
		reset.visible = !this.invis;
		gridAdder.add(reset);
		ButtonWidget back = ButtonWidget.builder(Translation.getConfigTranslation(Data.getVersion().getID(), "back"), (button) -> this.shouldClose = true).build();
		back.visible = !this.invis;
		gridAdder.add(back);
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
		if (ClientData.isDevelopment() && keyCode == GLFW.GLFW_KEY_F1) {
			this.invis = !this.invis;
			this.refresh = true;
		}
		if (ClientData.isDevelopment() && (this.debugShaderRegistry.isActive() || this.debugShader.isActive()) && (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER)) updateShader();
		if (keyCode == GLFW.GLFW_KEY_ESCAPE) this.shouldClose = true;
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		if (ClientData.isDevelopment()) context.drawTextWithShadow(this.textRenderer, "Press F1 to toggle config screen rendering.", 2, 2, 0xFFFFFF);
	}
	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
		if (!invis) super.renderBackground(context, mouseX, mouseY, delta);
	}
	@Override
	public boolean shouldPause() {
		return !ClientData.isDevelopment() && super.shouldPause();
	}
}