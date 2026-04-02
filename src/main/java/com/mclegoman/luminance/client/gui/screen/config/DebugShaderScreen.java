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
import com.mclegoman.luminance.client.shaders.Shaders;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.LogType;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class DebugShaderScreen extends Screen {
	private final Screen parentScreen;
	private GridWidget grid;
	private boolean shouldClose;
	private IdentifierListWidget registryList;
	private IdentifierListWidget shaderList;
	private Identifier selectedRegistry;

	public DebugShaderScreen(Screen parent) {
		this(parent, Debug.debugShader.getFirst());
	}

	public DebugShaderScreen(Screen parent, Identifier selectedRegistry) {
		super(Text.literal(""));
		this.parentScreen = parent;
		this.selectedRegistry = selectedRegistry;
	}

	public void init() {
		try {
			this.grid = new GridWidget();
			this.grid.getMainPositioner().alignHorizontalCenter().margin(2);
			GridWidget.Adder gridAdder = this.grid.createAdder(2);
			gridAdder.add(ButtonWidget.builder(Translation.getText("Debug Shader: {}", false, new Object[]{Debug.debugShaderEnabled}), button -> {
				Debug.debugShaderEnabled = !Debug.debugShaderEnabled;
				button.setMessage(Translation.getText("Debug Shader: {}", false, new Object[]{Debug.debugShaderEnabled}));
			}).build());
			gridAdder.add(ButtonWidget.builder(Translation.getText("Debug Render Type: {}", false, new Object[]{Debug.debugRenderType.toString()}), button -> {
				Debug.cycleDebugRenderType();
				button.setMessage(Translation.getText("Debug Render Type: {}", false, new Object[]{Debug.debugRenderType.toString()}));
			}).build());

			this.registryList = new IdentifierListWidget(150, 200, 20, 20, 20, Shaders.getRegistries(), this.selectedRegistry, (id, widget) -> {});

			gridAdder.add(this.registryList);
			IdentifierListWidget.Entry registryListSelected = this.registryList.getSelectedOrNull();
			this.shaderList = new IdentifierListWidget(150, 200, 20, 20, 20, Shaders.getShaderIds(registryListSelected != null ? registryListSelected.id : Shaders.getMainRegistryId()), Debug.debugShader.getSecond(), (id, widget) -> Debug.setDebugShader(registryListSelected != null ? registryListSelected.id : Shaders.getMainRegistryId(), id), (identifier) -> Shaders.getShaderName(registryListSelected != null ? registryListSelected.id : Shaders.getMainRegistryId(), identifier));
			gridAdder.add(this.shaderList);

			this.registryList.onSelect = (id, widget) -> {
				if (this.selectedRegistry != id) {
					this.selectedRegistry = id;
					this.shaderList.setScrollY(0);
					this.registryList.scrollToSelected();
				}
			};

			gridAdder.add(ButtonWidget.builder(Translation.getConfigTranslation(Data.getVersion().getID(), "back"), (button) -> this.shouldClose = true).width(304).build(), 2);

			grid.refreshPositions();
			grid.forEachChild(this::addDrawableChild);
			initTabNavigation();
		} catch (Exception error) {
			Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to initialize config screen: {}", error));
		}
	}

	public void tick() {
		try {
			if (this.shouldClose) {
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
	public boolean keyPressed(KeyInput keyInput) {
		if (keyInput.key() == GLFW.GLFW_KEY_ESCAPE) this.shouldClose = true;
		return super.keyPressed(keyInput);
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
	@Override
	protected void applyBlur(DrawContext context) {
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
	public boolean shouldPause() {
		return false;
	}
}