/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.gui.widget;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.LogType;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.StringRepresentable;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// This updates again in 26.1, and I've already done that in another mod so when we update I'll just copy that over rather than fixing it twice.
public class InfoWidget extends AbstractSelectionList<InfoWidget.InfoEntry> {
	private final Font textRenderer;

	public InfoWidget(Minecraft client, int width, int height, int y, int lineHeight) {
		this(client, width, height, y, lineHeight, 0);
	}

	public InfoWidget(Minecraft client, int width, int height, int y, int lineHeight, double scrollY) {
		super(client, width, height, y, lineHeight);
		this.textRenderer = client.font;
		for (FormattedCharSequence row : this.textRenderer.split(load(Identifier.fromNamespaceAndPath(Data.getVersion().getID(), "texts/info.json"), InfoWidget::read), this.getRowWidth())) addEntry(new InfoEntry(row));
		setScrollAmount(scrollY);
	}

	@Override
	protected void renderItem(GuiGraphics context, int mouseX, int mouseY, float delta, InfoEntry entry) {
		entry.renderContent(context, mouseX, mouseY, this.isHovered, delta);
	}

	public int getRowWidth() {
		return this.width - 32;
	}

	protected void updateWidgetNarration(NarrationElementOutput builder) {
	}

	public class InfoEntry extends Entry<InfoEntry> {
		private final FormattedCharSequence text;
		public InfoEntry(FormattedCharSequence text) {
			this.text = text;
		}
		public void renderContent(GuiGraphics context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
			context.drawString(textRenderer, this.text, getX(), getY(), 0xFFAAAAAA);
		}
	}

	private static Component load(Identifier id, InfoReader infoReader) {
		try (Reader reader = ClientData.minecraft.getResourceManager().openAsReader(id)) {
			return infoReader.read(reader);
		} catch (Exception exception) {
			Data.getVersion().sendToLog(LogType.ERROR, "Couldn't load info from file " + id + ": "+ exception.getLocalizedMessage());
		}
		return Component.empty();
	}

	private static Component read(Reader reader) {
		JsonObject root = GsonHelper.parse(reader).getAsJsonObject();
		MutableComponent text = Component.empty();
		if (root.has("values")) {
			for (JsonElement element : root.getAsJsonArray("values")) {
				text.append(read(Component.empty(), element));
				if (root.has("line_breaks") && root.get("line_breaks").getAsBoolean()) text.append("\n");
			}
		}
		return text;
	}

	private static Component read(MutableComponent text, JsonElement element) {
		JsonObject jsonObject = element.getAsJsonObject();
		if (jsonObject.has("indent")) {
			int indents = jsonObject.get("indent").getAsInt();
			if (indents > 0) text.append(Component.literal(" ".repeat(indents)));
		}
		List<Component> args = new ArrayList<>();
		if (jsonObject.has("args")) {
			for (JsonElement argElement : jsonObject.getAsJsonArray("args")) {
				args.add(read(Component.empty(), argElement));
			}
		}
		if (jsonObject.has("value")) {
			text.append(switch (jsonObject.has("type") ? TextType.valueOf(jsonObject.get("type").getAsString()) : TextType.literal) {
				case literal -> Component.literal(jsonObject.get("value").getAsString());
				case translatable -> Component.translatable(jsonObject.get("value").getAsString(), args.toArray(new Object[0]));
				case variable -> switch (jsonObject.get("value").getAsString()) {
					case "id" -> Component.literal(Data.getVersion().getID());
					case "name" -> Translation.getTranslation(Data.getVersion().getID(), "name");
					case "version" -> Component.literal(Data.getVersion().getFriendlyString());
                    case null, default -> Component.empty();
                };
			});
		}
		Style style = Style.EMPTY;
		if (jsonObject.has("style")) {
			JsonObject styleObject = jsonObject.getAsJsonObject("style");
			if (styleObject.has("color")) {
				Optional<Pair<TextColor, JsonElement>> oPair = TextColor.CODEC.decode(JsonOps.INSTANCE, styleObject.get("color")).result();
				if (oPair.isPresent()) style = style.withColor(oPair.get().getFirst());
			}
			if (styleObject.has("shadow_color")) style = style.withShadowColor(styleObject.get("shadow_color").getAsInt());
			if (styleObject.has("bold")) style = style.withBold(styleObject.get("bold").getAsBoolean());
			if (styleObject.has("italic")) style = style.withItalic(styleObject.get("italic").getAsBoolean());
			if (styleObject.has("underlined")) style = style.withUnderlined(styleObject.get("underlined").getAsBoolean());
			if (styleObject.has("strikethrough")) style = style.withStrikethrough(styleObject.get("strikethrough").getAsBoolean());
			if (styleObject.has("obfuscated")) style = style.withObfuscated(styleObject.get("obfuscated").getAsBoolean());
			if (styleObject.has("click_event")) {
				Optional<Pair<ClickEvent, JsonElement>> oPair = ClickEvent.CODEC.decode(JsonOps.INSTANCE, styleObject.get("click_event")).result();
				if (oPair.isPresent()) style = style.withClickEvent(oPair.get().getFirst());
			}
			if (styleObject.has("hover_event")) {
				Optional<Pair<HoverEvent, JsonElement>> oPair = HoverEvent.CODEC.decode(JsonOps.INSTANCE, styleObject.get("hover_event")).result();
				if (oPair.isPresent()) style = style.withHoverEvent(oPair.get().getFirst());
			}
			if (styleObject.has("insertion")) style = style.withInsertion(styleObject.get("insertion").getAsString());
			// TODO: update
			//if (styleObject.has("font")) style = style.withFont(Identifier.of(styleObject.get("font").getAsString()));
		}
		text.setStyle(style);
		return text;
	}

	interface InfoReader {
		Component read(Reader reader) throws IOException;
	}

	private enum TextType implements StringRepresentable {
		literal("literal"),
		translatable("translatable"),
		variable("variable");

		final String id;
		
		TextType(String id) {
			this.id = id;
		}

		public String getSerializedName() {
			return this.id;
		}
	}
}

