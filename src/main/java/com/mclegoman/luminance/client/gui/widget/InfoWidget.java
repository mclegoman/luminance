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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.StringIdentifiable;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class InfoWidget extends EntryListWidget<InfoWidget.InfoEntry> {
	private final TextRenderer textRenderer;

	public InfoWidget(MinecraftClient client, int width, int height, int y, int lineHeight) {
		this(client, width, height, y, lineHeight, 0);
	}

	public InfoWidget(MinecraftClient client, int width, int height, int y, int lineHeight, double scrollY) {
		super(client, width, height, y, lineHeight);
		this.textRenderer = client.textRenderer;
		for (OrderedText row : this.textRenderer.wrapLines(load(Identifier.of(Data.getVersion().getID(), "texts/info.json"), InfoWidget::read), this.getRowWidth())) addEntry(new InfoEntry(row));
		setScrollY(scrollY);
	}

	protected void renderEntry(DrawContext context, int mouseX, int mouseY, float delta, int index, int x, int y, int entryWidth, int entryHeight) {
		// TODO: update
		//InfoEntry entry = this.getEntry(index);
		//entry.render(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, Objects.equals(this.getHoveredEntry(), entry), delta);
	}

	public int getRowWidth() {
		return this.width - 32;
	}

	protected void appendClickableNarrations(NarrationMessageBuilder builder) {
	}

	public class InfoEntry extends Entry<InfoEntry> {
		private final OrderedText text;
		public InfoEntry(OrderedText text) {
			this.text = text;
		}
		public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
			// TODO: update
			//context.drawTextWithShadow(textRenderer, this.text, x, y, 0xFFAAAAAA);
		}
	}

	private static Text load(Identifier id, InfoReader infoReader) {
		try (Reader reader = ClientData.minecraft.getResourceManager().openAsReader(id)) {
			return infoReader.read(reader);
		} catch (Exception exception) {
			Data.getVersion().sendToLog(LogType.ERROR, "Couldn't load info from file " + id + ": "+ exception.getLocalizedMessage());
		}
		return Text.empty();
	}

	private static Text read(Reader reader) {
		JsonObject root = JsonHelper.deserialize(reader).getAsJsonObject();
		MutableText text = Text.empty();
		if (root.has("values")) {
			for (JsonElement element : root.getAsJsonArray("values")) {
				text.append(read(Text.empty(), element));
				if (root.has("line_breaks") && root.get("line_breaks").getAsBoolean()) text.append("\n");
			}
		}
		return text;
	}

	private static Text read(MutableText text, JsonElement element) {
		JsonObject jsonObject = element.getAsJsonObject();
		if (jsonObject.has("indent")) {
			int indents = jsonObject.get("indent").getAsInt();
			if (indents > 0) text.append(Text.literal(" ".repeat(indents)));
		}
		List<Text> args = new ArrayList<>();
		if (jsonObject.has("args")) {
			for (JsonElement argElement : jsonObject.getAsJsonArray("args")) {
				args.add(read(Text.empty(), argElement));
			}
		}
		if (jsonObject.has("value")) {
			text.append(switch (jsonObject.has("type") ? TextType.valueOf(jsonObject.get("type").getAsString()) : TextType.literal) {
				case literal -> Text.literal(jsonObject.get("value").getAsString());
				case translatable -> Text.translatable(jsonObject.get("value").getAsString(), args.toArray(new Object[0]));
				case variable -> switch (jsonObject.get("value").getAsString()) {
					case "id" -> Text.literal(Data.getVersion().getID());
					case "name" -> Translation.getTranslation(Data.getVersion().getID(), "name");
					case "version" -> Text.literal(Data.getVersion().getFriendlyString());
                    case null, default -> Text.empty();
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
			if (styleObject.has("underlined")) style = style.withUnderline(styleObject.get("underlined").getAsBoolean());
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
		Text read(Reader reader) throws IOException;
	}

	private enum TextType implements StringIdentifiable {
		literal("literal"),
		translatable("translatable"),
		variable("variable");

		final String id;
		
		TextType(String id) {
			this.id = id;
		}

		public String asString() {
			return this.id;
		}
	}
}

