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
import com.mclegoman.luminance.client.gui.widget.IdentifierListWidget;
import com.mclegoman.luminance.client.shaders.ShaderStacks;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.LogType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class DebugShaderScreen extends Screen {
	private final Screen parentScreen;
	private GridLayout grid;
	private boolean shouldClose;
	private IdentifierListWidget registryList;
	private IdentifierListWidget shaderList;
	private Identifier selectedRegistry;
	private boolean reducedAlpha = true;

	public DebugShaderScreen(Screen parent) {
		this(parent, Debug.getDebugShader().getFirst());
	}

	public DebugShaderScreen(Screen parent, Identifier selectedRegistry) {
		super(Component.literal(""));
		this.parentScreen = parent;
		this.selectedRegistry = selectedRegistry;
	}

	public void init() {
		try {
			this.addRenderableWidget(Button.builder(Translation.getVariableTranslation(Data.getVersion().getID(), "onff", Debug.getRawDisablePhotosensitive()), button -> {
				Debug.setDisablePhotosensitive(!Debug.getRawDisablePhotosensitive());
				button.setMessage(Translation.getVariableTranslation(Data.getVersion().getID(), "onff", Debug.getRawDisablePhotosensitive()));
			}).width(32).tooltip(Tooltip.create(Translation.getTranslation(Data.getVersion().getID(), "debug.disable_photosensitive"))).pos(2, 2).build());

			this.addRenderableWidget(Button.builder(Translation.getVariableTranslation(Data.getVersion().getID(), "onff", this.reducedAlpha), button -> {
				this.reducedAlpha = !this.reducedAlpha;
				button.setMessage(Translation.getVariableTranslation(Data.getVersion().getID(), "onff", this.reducedAlpha));
			}).width(32).tooltip(Tooltip.create(Translation.getTranslation(Data.getVersion().getID(), "debug.reduced_alpha"))).pos(2, 22).build());

			this.grid = new GridLayout();
			this.grid.defaultCellSetting().alignHorizontallyCenter().padding(2);
			GridLayout.RowHelper gridAdder = this.grid.createRowHelper(2);

			gridAdder.addChild(Button.builder(Translation.getTranslation(Data.getVersion().getID(), "debug.render", new Object[]{Translation.getVariableTranslation(Data.getVersion().getID(), "onff", Debug.isDebugShaderEnabled())}), button -> {
				Debug.setDebugShaderEnabled(!Debug.isDebugShaderEnabled());
				button.setMessage(Translation.getTranslation(Data.getVersion().getID(), "debug.render", new Object[]{Translation.getVariableTranslation(Data.getVersion().getID(), "onff", Debug.isDebugShaderEnabled())}));
			}).width(140).build());
			gridAdder.addChild(Button.builder(Translation.getTranslation(Data.getVersion().getID(), "debug.render_type", new Object[]{Translation.getRenderTypeTranslation(Debug.debugRenderType)}), button -> {
				Debug.cycleDebugRenderType(ClientData.minecraft.hasShiftDown());
				button.setMessage(Translation.getTranslation(Data.getVersion().getID(), "debug.render_type", new Object[]{Translation.getRenderTypeTranslation(Debug.debugRenderType)}));
			}).width(140).build());

			this.registryList = new IdentifierListWidget(150, 200, 20, 20, 20, ShaderStacks.getRegistries(), this.selectedRegistry, (id, widget) -> {});

			gridAdder.addChild(this.registryList);
			IdentifierListWidget.Entry registryListSelected = this.registryList.getSelected();
			this.shaderList = new IdentifierListWidget(150, 200, 20, 20, 20, ShaderStacks.getShaderStacks(registryListSelected != null ? registryListSelected.id : ShaderStacks.getMainRegistryId()), Debug.getDebugShader().getSecond(), (id, widget) -> Debug.setDebugShader(registryListSelected != null ? registryListSelected.id : ShaderStacks.getMainRegistryId(), id), (identifier) -> ShaderStacks.getShaderName(registryListSelected != null ? registryListSelected.id : ShaderStacks.getMainRegistryId(), identifier), (identifier) -> ShaderStacks.getShaderDescription(registryListSelected != null ? registryListSelected.id : ShaderStacks.getMainRegistryId(), identifier));
			gridAdder.addChild(this.shaderList);

			this.registryList.onSelect = (id, widget) -> {
				if (this.selectedRegistry != id) {
					this.selectedRegistry = id;
					this.shaderList.setScrollAmount(0);
					this.registryList.scrollToSelected();
				}
			};

			gridAdder.addChild(Button.builder(Translation.getConfigTranslation(Data.getVersion().getID(), "back"), (button) -> this.shouldClose = true).width(304).build(), 2);

			grid.arrangeElements();
			grid.visitWidgets(this::addRenderableWidget);
			initTabNavigation();
		} catch (Exception error) {
			Data.getVersion().sendToLog(LogType.ERROR, "Failed to initialize config screen: {}", error);
		}
	}

	public void tick() {
		try {
			if (this.shouldClose) {
				ClientData.minecraft.setScreen(parentScreen);
			}
		} catch (Exception error) {
			Data.getVersion().sendToLog(LogType.ERROR, "Failed to tick luminance$config screen: {}", error);
		}
	}

	public void initTabNavigation() {
		FrameLayout.centerInRectangle(grid, getRectangle());
	}

	public Component getNarrationMessage() {
		return CommonComponents.joinForNarration();
	}

	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	public boolean keyPressed(KeyEvent keyInput) {
		if (keyInput.key() == GLFW.GLFW_KEY_ESCAPE) this.shouldClose = true;
		return super.keyPressed(keyInput);
	}

	@Override
	public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
		for (GuiEventListener element : children()) {
			if (element instanceof AbstractWidget clickableWidget) {
				clickableWidget.setAlpha(this.reducedAlpha ? 0.24F : 1.0F);
			}
		}
		super.render(context, mouseX, mouseY, delta);
	}
	@Override
	protected void renderBlurredBackground(GuiGraphics context) {
		// noop
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		this.registryList.scrollToSelected();
		this.shaderList.scrollToSelected();
	}

	// make sure shaders update properly while in the config screens
	@Override
	public boolean isPauseScreen() {
		return false;
	}
}