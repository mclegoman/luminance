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
import com.mclegoman.luminance.mixin.client.gui.ScreenAccessor;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.*;
import net.minecraft.network.protocol.common.ServerboundCustomClickActionPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PageWidget extends AbstractSelectionList<PageWidget.@NotNull Entry> {
    private final Font textRenderer;

    public PageWidget(Minecraft client, int width, int height, int y, int lineHeight, Identifier file) {
        this(client, width, height, y, lineHeight, 0, file);
    }

    public PageWidget(Minecraft client, int width, int height, int y, int lineHeight, double scrollY, Identifier file) {
        super(client, width, height, y, lineHeight);
        this.textRenderer = client.font;
        for (FormattedCharSequence row : this.textRenderer.split(load(file, PageWidget::read), this.getRowWidth())) addEntry(new Entry(row));
        setScrollAmount(scrollY);
    }

    protected void renderItem(@NotNull GuiGraphics context, int mouseX, int mouseY, float delta, Entry entry) {
        entry.renderContent(context, mouseX, mouseY, Objects.equals(this.getHovered(), entry), delta);
    }

    public int getRowWidth() {
        return this.width - 32;
    }

    protected void updateWidgetNarration(@NotNull NarrationElementOutput builder) {
    }

    public class Entry extends net.minecraft.client.gui.components.AbstractSelectionList.Entry<@NotNull Entry> {
        private final FormattedCharSequence text;

        public Entry(FormattedCharSequence text) {
            this.text = text;
        }

        public void renderContent(GuiGraphics context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            context.drawString(textRenderer, this.text, this.getX(), this.getY(), 0xFFAAAAAA);
            if (hovered) {
                Style style = Translation.getStyleAt(this.text, mouseX - this.getX(), textRenderer.getSplitter());
                if (style != null) {
                    if (style.getClickEvent() != null) context.requestCursor(CursorTypes.POINTING_HAND);
                    if (style.getHoverEvent() != null) context.renderComponentHoverEffect(textRenderer, style, mouseX, mouseY);
                }
            }
        }

        public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
            Style style = Translation.getStyleAt(this.text, (int)(click.x() - this.getX()), textRenderer.getSplitter());

            if (style != null && style.getClickEvent() != null) {
                handleClickEvent(style.getClickEvent(), minecraft, minecraft.screen);
                return true;
            }

            return super.mouseClicked(click, doubled);
        }

        public static void handleClickEvent(ClickEvent clickEvent, Minecraft client, @Nullable Screen screenAfterRun) {
            if (client.player != null) {
                switch (clickEvent) {
                    case ClickEvent.RunCommand(String string):
                        client.player.connection.sendUnattendedCommand(Commands.trimOptionalPrefix(string), screenAfterRun); // Screen.clickCommandAction
                        break;
                    case ClickEvent.ShowDialog showDialog:
                        client.player.connection.showDialog(showDialog.dialog(), screenAfterRun);
                        break;
                    case ClickEvent.Custom custom:
                        client.player.connection.send(new ServerboundCustomClickActionPacket(custom.id(), custom.payload()));
                        if (client.screen != screenAfterRun) client.setScreen(screenAfterRun);
                        break;
                    default:
                        handleBasicClickEvent(clickEvent, client, screenAfterRun);
                }
            } else handleBasicClickEvent(clickEvent, client, screenAfterRun);
        }

        public static void handleBasicClickEvent(ClickEvent clickEvent, Minecraft client, @Nullable Screen screenAfterRun) {
            if (switch (clickEvent) {
                case ClickEvent.OpenUrl(URI uRI) -> {
                    handleOpenUri(client, screenAfterRun, uRI);
                    yield false;
                }
                case ClickEvent.OpenFile openFile -> {
                    Util.getPlatform().openFile(openFile.file());
                    yield true;
                }
                case ClickEvent.SuggestCommand(String string) -> {
                    if (screenAfterRun != null) ((ScreenAccessor)screenAfterRun).invokeInsertText(string, true);
                    yield true;
                }
                case ClickEvent.CopyToClipboard(String string2) -> {
                    client.keyboardHandler.setClipboard(string2);
                    yield true;
                }
                default -> {
                    Data.getVersion().sendToLog(LogType.ERROR, "Don't know how to handle {}", clickEvent);
                    yield true;
                }
            } && client.screen != screenAfterRun) client.setScreen(screenAfterRun);
        }
    }

    public static void handleOpenUri(Minecraft client, @Nullable Screen screen, URI uri) {
        client.setScreen(new ConfirmLinkScreen((confirmed) -> {
            if (confirmed) Util.getPlatform().openUri(uri);
            client.setScreen(screen);
        }, uri.toString(), true));
    }

    private static Component load(Identifier id, Reader Reader) {
        try (java.io.Reader reader = ClientData.minecraft.getResourceManager().openAsReader(id)) {
            return Reader.read(reader);
        } catch (Exception exception) {
            Data.getVersion().sendToLog(LogType.ERROR, "Couldn't load file {}: {}", id, exception.getLocalizedMessage());
        }

        return Component.empty();
    }

    private static MutableComponent read(java.io.Reader reader) {
        JsonObject root = GsonHelper.parse(reader).getAsJsonObject();
        MutableComponent text = Component.empty();

        if (root.has("values")) {
            for (JsonElement element : root.getAsJsonArray("values")) {
                PageLine line = read(Component.empty(), element);
                if (!line.shouldSkip) {
                    text.append(line.text);
                    if (root.has("line_breaks") && root.get("line_breaks").getAsBoolean()) text.append("\n");
                }
            }
        }

        return text;
    }

    private record PageLine(MutableComponent text, boolean shouldSkip) {}

    private static PageLine read(MutableComponent text, JsonElement element) {
        JsonObject jsonObject = element.getAsJsonObject();
        if (jsonObject.has("requires") && !FabricLoader.getInstance().isModLoaded(jsonObject.get("requires").getAsString())) {
            return new PageLine(text, true);
        }
        Style style = text.getStyle();
        if (jsonObject.has("style")) {
            JsonObject styleObject = jsonObject.getAsJsonObject("style");
            if (styleObject.has("color")) {
                Optional<Pair<TextColor, JsonElement>> oPair = TextColor.CODEC.decode(JsonOps.INSTANCE, styleObject.get("color")).result();
                if (oPair.isPresent()) style = style.withColor(oPair.get().getFirst());
            }
            if (styleObject.has("shadowColor")) style = style.withShadowColor(styleObject.get("shadowColor").getAsInt());
            if (styleObject.has("bold")) style = style.withBold(styleObject.get("bold").getAsBoolean());
            if (styleObject.has("italic")) style = style.withItalic(styleObject.get("italic").getAsBoolean());
            if (styleObject.has("underlined")) style = style.withUnderlined(styleObject.get("underlined").getAsBoolean());
            if (styleObject.has("strikethrough")) style = style.withStrikethrough(styleObject.get("strikethrough").getAsBoolean());
            if (styleObject.has("obfuscated")) style = style.withObfuscated(styleObject.get("obfuscated").getAsBoolean());
            if (styleObject.has("clickEvent")) {
                Optional<Pair<ClickEvent, JsonElement>> oPair = ClickEvent.CODEC.decode(JsonOps.INSTANCE, styleObject.get("clickEvent")).result();
                if (oPair.isPresent()) style = style.withClickEvent(oPair.get().getFirst());
            }
            if (styleObject.has("hoverEvent")) {
                Optional<Pair<HoverEvent, JsonElement>> oPair = HoverEvent.CODEC.decode(JsonOps.INSTANCE, styleObject.get("hoverEvent")).result();
                if (oPair.isPresent()) style = style.withHoverEvent(oPair.get().getFirst());
            }
            if (styleObject.has("insertion")) style = style.withInsertion(styleObject.get("insertion").getAsString());
            if (styleObject.has("font")) {
                Optional<Pair<FontDescription, JsonElement>> oPair = FontDescription.CODEC.decode(JsonOps.INSTANCE, styleObject.get("font")).result();
                if (oPair.isPresent()) style = style.withFont(oPair.get().getFirst());
            }
        }

        if (jsonObject.has("indent")) {
            int indents = jsonObject.get("indent").getAsInt();
            if (indents > 0) text.append(Component.literal(" ".repeat(indents)));
        }

        List<MutableComponent> args = new ArrayList<>();
        if (jsonObject.has("args")) {
            for (JsonElement argElement : jsonObject.getAsJsonArray("args")) {
                PageLine line = read(Component.empty(), argElement);
                if (!line.shouldSkip) args.add(line.text);
            }
        }

        if (jsonObject.has("value")) {
            MutableComponent valueNode = switch (jsonObject.has("type") ? TextType.valueOf(jsonObject.get("type").getAsString()) : TextType.literal) {
                case literal -> Component.literal(jsonObject.get("value").getAsString());
                case translatable ->
                        Component.translatable(jsonObject.get("value").getAsString(), args.toArray(new Object[0]));
                case variable -> switch (jsonObject.get("value").getAsString()) {
                    case "id" -> Component.literal(Data.getVersion().getID());
                    case "name" -> Translation.getTranslation(Data.getVersion().getID(), "name");
                    case "build_date" -> {
                        Optional<ModContainer> mod = FabricLoader.getInstance().getModContainer(Data.getVersion().getID());
                        yield Component.literal(mod.isPresent() && mod.get().getMetadata().containsCustomValue("build_date") ? mod.get().getMetadata().getCustomValue("build_date").getAsString() : "UNKNOWN");
                    }
                    case "version" -> {
                        Optional<ModContainer> mod = FabricLoader.getInstance().getModContainer(Data.getVersion().getID());
                        yield Component.literal(mod.isPresent() ? mod.get().getMetadata().getVersion().getFriendlyString() : "UNKNOWN");
                    }
                    case null, default -> Component.empty();
                };
            };
            text.append(valueNode.setStyle(style));
        }
        return new PageLine(text, false);
    }

    interface Reader {
        Component read(java.io.Reader reader) throws IOException;
    }

    private enum TextType implements StringRepresentable {
        literal("literal"),
        translatable("translatable"),
        variable("variable");

        final String id;

        TextType(String id) {
            this.id = id;
        }

        public @NotNull String getSerializedName() {
            return this.id;
        }
    }
}

