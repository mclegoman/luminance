/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

// TODO: Update debug shader screen.

package com.mclegoman.luminance.client.gui.screen.config;

import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.debug.Debug;
import com.mclegoman.luminance.client.shaders.Shaders;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.LogType;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class DebugShaderScreen extends Screen {
	private final Screen parentScreen;
	private final GridWidget grid;
	private boolean refresh;
	private boolean shouldClose;
	private TextFieldWidget debugShaderRegistry;
	private TextFieldWidget debugShader;

	public DebugShaderScreen(Screen parent) {
		this(parent, false);
	}

	public DebugShaderScreen(Screen parent, boolean refresh) {
		super(Text.literal(""));
		this.grid = new GridWidget();
		this.parentScreen = parent;
		this.refresh = refresh;
	}

	public void init() {
		clearChildren();
		try {
			grid.getMainPositioner().alignHorizontalCenter().margin(2);
			GridWidget.Adder gridAdder = grid.createAdder(2);
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
			gridAdder.add(ButtonWidget.builder(Translation.getConfigTranslation(Data.getVersion().getID(), "back"), (button) -> this.shouldClose = true).width(304).build(), 2);
			grid.refreshPositions();
			grid.forEachChild(this::addDrawableChild);
			initTabNavigation();
		} catch (Exception error) {
			Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to initialize config screen: {}", error));
		}
	}

	private void updateShader() {
		if (this.debugShader != null) {
			try {
				Identifier shaderRegistry = (this.debugShaderRegistry != null && !Identifier.of(this.debugShaderRegistry.getText()).getPath().equalsIgnoreCase("")) ? Identifier.of(this.debugShaderRegistry.getText()) : Shaders.getMainRegistryId();
				Identifier shaderId = !Identifier.of(this.debugShader.getText()).getPath().equalsIgnoreCase("") ? Identifier.of(this.debugShader.getText()) : Identifier.of("box_blur");
				Shaders.guessPostShader(shaderRegistry, shaderId.toString()).ifPresentOrElse((postShader) -> Debug.setDebugShader(shaderRegistry, postShader.getID()), () -> Debug.setDebugShader(shaderRegistry, shaderId));
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to update debug shader: {}", error));
				Debug.setDebugShader(Shaders.getMainRegistryId(), Identifier.of("box_blur"));
			}
			try {
				boolean registryFocused = this.debugShaderRegistry.isFocused();
				boolean shaderFocused = this.debugShader.isFocused();
				int registryCursor = this.debugShaderRegistry.getCursor();
				int shaderCursor = this.debugShader.getCursor();
				String registryText = this.debugShaderRegistry.getText();
				String shaderText = this.debugShader.getText();
				this.debugShaderRegistry.setText(Debug.debugShader.getFirst().toString());
				this.debugShader.setText(Debug.debugShader.getSecond().toString());
				if (registryFocused) {
					this.debugShaderRegistry.setFocused(true);
					if (this.debugShaderRegistry.getText().length() > registryText.length()) registryCursor += (this.debugShaderRegistry.getText().length() - registryText.length());
					this.debugShaderRegistry.setCursor(registryCursor, false);
				}
				if (shaderFocused) {
					this.debugShader.setFocused(true);
					if (this.debugShader.getText().length() > shaderText.length()) shaderCursor += (this.debugShader.getText().length() - shaderText.length());
					this.debugShader.setCursor(shaderCursor, false);
				}
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to update debug shader text field: {}", error));
			}
		}
	}

	public void tick() {
		try {
			if (this.refresh) {
				updateShader();
				ClientData.minecraft.setScreen(new DebugShaderScreen(parentScreen, false));
			}
			if (this.shouldClose) {
				updateShader();
				ClientData.minecraft.setScreen(parentScreen);
			}
		} catch (Exception error) {
			Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to tick luminance$config screen: {}", error));
		}
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
		if (ClientData.isDevelopment() && (this.debugShaderRegistry.isActive() || this.debugShader.isActive()) && (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER)) updateShader();
		if (keyCode == GLFW.GLFW_KEY_ESCAPE) this.shouldClose = true;
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		for (Element element : children()) {
			if (element instanceof ClickableWidget clickableWidget) {
				clickableWidget.setAlpha(0.24F);
			}
		}
		super.render(context, mouseX, mouseY, delta);
	}

	protected void applyBlur() {
	}
}