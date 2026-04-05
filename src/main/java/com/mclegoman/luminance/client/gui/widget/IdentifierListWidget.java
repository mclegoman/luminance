/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.gui.widget;

import com.mclegoman.luminance.client.data.ClientData;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class IdentifierListWidget extends ObjectSelectionList<IdentifierListWidget.Entry> {
    public OnSelect onSelect;
    public final Label label;
    public final Label hoverText;

    public IdentifierListWidget(int width, int height, int top, int bottom, int itemHeight, List<Identifier> identifiers, Identifier selected, OnSelect onSelect) {
        this(width, height, top, bottom, itemHeight, identifiers, selected, onSelect, (identifier) -> null);
    }

    public IdentifierListWidget(int width, int height, int top, int bottom, int itemHeight, List<Identifier> identifiers, Identifier selected, OnSelect onSelect, Label hoverText) {
        this (width, height, top, bottom, itemHeight, identifiers, selected, onSelect, (identifier) -> Component.literal(identifier.toString()), hoverText);
    }

    public IdentifierListWidget(int width, int height, int top, int bottom, int itemHeight, List<Identifier> identifiers, Identifier selected, OnSelect onSelect, Label label, Label hoverText) {
        super(ClientData.minecraft, width, height - top - bottom, top, itemHeight);
        this.onSelect = onSelect;
        this.label = label;
        this.hoverText = hoverText;

        for (Identifier id : identifiers) {
            this.addEntry(new Entry(id, this));
        }

        if (selected != null && identifiers.contains(selected)) {
            int index = identifiers.indexOf(selected);
            if (index != -1) this.setSelected(this.children().get(index));
        } else this.setSelected(this.children().getFirst());

        this.scrollToSelected();
        this.setFocused(true);
    }

    @Override
    public void setSelected(@Nullable IdentifierListWidget.Entry entry) {
        super.setSelected(entry);
        if (entry != null) this.onSelect.call(entry.id, this);
    }

    @Override
    protected void renderItem(GuiGraphics context, int mouseX, int mouseY, float delta, Entry entry) {
        if (this.getHovered() != null && this.getHovered().equals(entry)) {
            this.renderSelection(context, entry, -8355712);
            if (entry.hoverText != null && !entry.hoverText.getString().isBlank()) context.setTooltipForNextFrame(entry.hoverText, mouseX, mouseY);
        }
        if (entry.equals(getSelected())) this.renderSelection(context, entry, -1);
        entry.renderContent(context, mouseX, mouseY, this.isHovered, delta);
    }

    public int getRowWidth() {
        return this.width - 16;
    }

    @Override
    protected int scrollBarX() {
        return this.getRowRight();
    }

    public static class Entry extends ObjectSelectionList.Entry<Entry> {
        public final Identifier id;
        public final IdentifierListWidget parent;
        private final Component label;
        private final Component hoverText;

        public Entry(Identifier id, IdentifierListWidget parent) {
            this.id = id;
            this.parent = parent;
            this.label = parent.label.call(id);
            this.hoverText = parent.hoverText.call(id);
        }

        @Override
        public void renderContent(GuiGraphics context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            context.drawCenteredString(ClientData.minecraft.font, this.label, this.getX() + (this.getWidth() / 2), this.getY() + (this.getHeight() - ClientData.minecraft.font.lineHeight) / 2, 0xFFFFFFFF);
        }

        @Override
        public Component getNarration() {
            return this.label;
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
            if (isMouseOver(click.x(), click.y())) {
                parent.setSelected(this);
                return true;
            }
            return super.mouseClicked(click, doubled);
        }
    }

    @FunctionalInterface
    public interface OnSelect {
        void call(Identifier identifier, IdentifierListWidget widget);
    }

    @FunctionalInterface
    public interface Label {
        Component call(Identifier identifier);
    }

    @Override
    public void refreshScrollAmount() {
        this.scrollToSelected();
        super.refreshScrollAmount();
    }

    public void scrollToSelected() {
        if (this.getSelected() != null) ClientData.minecraft.execute(() -> this.centerScrollOn(this.getSelected()));
    }
}