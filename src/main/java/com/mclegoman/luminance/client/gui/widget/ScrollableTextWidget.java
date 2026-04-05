/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.gui.widget;

import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.LogType;
import com.mclegoman.luminance.mixin.client.gui.ScreenAccessor;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.common.ServerboundCustomClickActionPacket;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.net.URI;
import java.util.List;

public class ScrollableTextWidget extends AbstractSelectionList<ScrollableTextWidget.LineEntry> {
    public ScrollableTextWidget(Minecraft minecraft, int width, int height, int y, int lineHeight, List<FormattedText> texts) {
        this(minecraft, width, height, y, lineHeight, 0, texts);
    }

    public ScrollableTextWidget(Minecraft minecraft, int width, int height, int y, int lineHeight, double scrollY, List<FormattedText> texts) {
        super(minecraft, width, height, y, lineHeight);
        for (FormattedText text : texts) {
            for (FormattedCharSequence row : minecraft.font.split(text, this.getRowWidth())) {
                this.addEntry(new LineEntry(row, minecraft.font));
            }
        }
        if (scrollY > 0) setScrollAmount(scrollY);
    }

    public int getRowWidth() {
        return this.width - 32;
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput narrationElementOutput) {
    }

    public class LineEntry extends AbstractSelectionList.Entry<LineEntry> {
        private final FormattedCharSequence text;
        private final Font font;

        public LineEntry(FormattedCharSequence text, Font font) {
            this.text = text;
            this.font = font;
        }

        @Override
        public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            guiGraphics.drawString(this.font, this.text, this.getX(), this.getY(), 0xFFAAAAAA);
            if (hovered) {
                Style style = Translation.getStyleAt(this.text, mouseX - this.getX(), this.font.getSplitter());
                if (style != null) {
                    if (style.getClickEvent() != null) guiGraphics.requestCursor(CursorTypes.POINTING_HAND);
                    if (style.getHoverEvent() != null) guiGraphics.renderComponentHoverEffect(this.font, style, mouseX, mouseY);
                }
            }
        }

        public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
            Style style = Translation.getStyleAt(this.text, (int)(click.x() - this.getX()), this.font.getSplitter());

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
}
