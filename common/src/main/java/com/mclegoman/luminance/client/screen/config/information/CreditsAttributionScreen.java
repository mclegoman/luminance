/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.screen.config.information;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.logo.LuminanceLogo;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.DateHelper;
import com.mclegoman.luminance.common.util.LogType;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.lwjgl.glfw.GLFW;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class CreditsAttributionScreen extends Screen {
	protected final Screen parentScreen;
	protected final boolean isPride;
	protected final Identifier creditsJsonId;
	protected float time;
	protected List<OrderedText> credits;
	protected final List<Integer> centeredLines = new ArrayList<>();
	protected int creditsHeight;
	protected boolean isHoldingSpace;
	public CreditsAttributionScreen(Screen parentScreen, boolean isPride, Identifier creditsJsonId) {
		super(NarratorManager.EMPTY);
		this.parentScreen = parentScreen;
		this.isPride = isPride;
		this.creditsJsonId = creditsJsonId;
	}
	public CreditsAttributionScreen(Screen parentScreen, boolean isPride) {
		this(parentScreen, isPride, Identifier.of(Data.getVersion().getID(), "/texts/credits.json"));
	}
	public CreditsAttributionScreen(Screen parentScreen, Identifier creditsJsonId) {
		this(parentScreen, DateHelper.isPride(), creditsJsonId);
	}
	public CreditsAttributionScreen(Screen parentScreen) {
		this(parentScreen, DateHelper.isPride(), Identifier.of(Data.getVersion().getID(), "/texts/credits.json"));
	}
	public void tick() {
		if (this.time > (this.creditsHeight + ClientData.minecraft.getWindow().getScaledHeight() + 64)) this.close();
	}
	protected float getSpeed() {
		return this.time > 0 ? ((isHoldingSpace ? 4.0F : 1.0F) * (hasControlDown() ? 4.0F : 1.0F)) : 1.0F;
	}
	public void close() {
		ClientData.minecraft.setScreen(this.parentScreen);
	}
	protected void init() {
		if (this.credits == null) {
			this.credits = Lists.newArrayList();
			this.load(creditsJsonId, this::readCredits);
			this.creditsHeight = this.credits.size() * 12;
		}
	}
	protected void load(Identifier id, CreditsAttributionReader reader) {
		try {
			Reader reader1 = ClientData.minecraft.getResourceManager().openAsReader(id);
			try {
				reader.read(reader1);
			} catch (Exception error) {
				reader1.close();
				Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("An error occurred whilst trying to load credits! {}", error));
			}
		} catch (Exception error) {
			Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("An error occurred whilst trying to load credits! {}", error));
		}
	}
	protected void readCredits(Reader reader) {
		try {
			readSection(JsonHelper.deserialize(reader));
			addEmptyLine();
		} catch (Exception error) {
			Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("An error occurred whilst trying to load credits! {}", error));
		}
	}
	protected JsonArray readSection(JsonObject section) {
		if (section != null && !section.isEmpty()) {
			String text = JsonHelper.getString(section, "text", "");
			if (!text.isEmpty()) this.addText(Text.literal(text).formatted(Formatting.byName(JsonHelper.getString(section, "formatting", "gray"))), JsonHelper.getBoolean(section, "centered", false));
			if (JsonHelper.getBoolean(section, "spaced", false)) addEmptyLine();
			readSections(JsonHelper.getArray(section, "sections", new JsonArray()));
		}
		return null;
	}
	protected void readSections(JsonArray sections) {
		if (sections != null && !sections.isEmpty()) for (JsonElement section : sections) readSections(readSection(section.getAsJsonObject()));
	}
	protected void addEmptyLine() {
		this.credits.add(OrderedText.EMPTY);
	}
	protected void addText(Text text, boolean centered) {
		if (centered) {
			this.centeredLines.add(this.credits.size());
		}
		this.credits.addAll(ClientData.minecraft.textRenderer.wrapLines(text, ClientData.minecraft.getWindow().getScaledWidth() - ClientData.minecraft.getWindow().getScaledWidth() / 2 - ClientData.minecraft.getWindow().getScaledWidth() / 2 + 320));
	}
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		RenderSystem.defaultBlendFunc();
		this.time = Math.max(0.0F, this.time + (delta * this.getSpeed()));
		context.getMatrices().push();
		context.getMatrices().translate(0.0F, -this.time, 0.0F);
		renderLogo(context);
		int height = ClientData.minecraft.getWindow().getScaledHeight() + 80;
		for(int l = 0; l < this.credits.size(); ++l) {
			if (l == this.credits.size() - 1) {
				float g = height - this.time - (float)(ClientData.minecraft.getWindow().getScaledHeight() / 2 - 6);
				if (g < 0.0F) {
					context.getMatrices().translate(0.0F, -g, 0.0F);
				}
			}
			if (height - this.time + 12.0F + 8.0F > 0.0F && height - this.time < (float)ClientData.minecraft.getWindow().getScaledHeight()) {
				OrderedText orderedText = this.credits.get(l);
				if (this.centeredLines.contains(l)) {
					context.drawCenteredTextWithShadow(this.textRenderer, orderedText, ClientData.minecraft.getWindow().getScaledWidth() / 2, height, 16777215);
				} else {
					context.drawTextWithShadow(this.textRenderer, orderedText, ClientData.minecraft.getWindow().getScaledWidth() / 2 - 160, height, 16777215);
				}
			}
			height += 12;
		}
		context.getMatrices().pop();
	}
	protected void renderLogo(DrawContext context) {
		LuminanceLogo.renderLogo(context, ClientData.minecraft.getWindow().getScaledWidth() / 2 - 128, ClientData.minecraft.getWindow().getScaledHeight() + 2, 256, 64, isPride);
	}
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_SPACE) {
			this.isHoldingSpace = true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_SPACE) {
			this.isHoldingSpace = false;
		}
		return super.keyReleased(keyCode, scanCode, modifiers);
	}
	protected interface CreditsAttributionReader {
		void read(Reader reader);
	}
}
