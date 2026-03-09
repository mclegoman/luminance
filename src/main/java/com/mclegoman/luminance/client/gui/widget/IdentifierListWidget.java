package com.mclegoman.luminance.client.gui.widget;

import com.mclegoman.luminance.client.data.ClientData;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class IdentifierListWidget extends AlwaysSelectedEntryListWidget<IdentifierListWidget.Entry> {
    public OnSelect onSelect;
    public final Label label;

    public IdentifierListWidget(int width, int height, int top, int bottom, int itemHeight, double scrollAmount, List<Identifier> identifiers, Identifier selected, OnSelect onSelect) {
        this (width, height, top, bottom, itemHeight, scrollAmount, identifiers, selected, onSelect, (identifier) -> Text.literal(identifier.toString()));
    }

    public IdentifierListWidget(int width, int height, int top, int bottom, int itemHeight, double scrollAmount, List<Identifier> identifiers, Identifier selected, OnSelect onSelect, Label label) {
        super(ClientData.minecraft, width, height - top - bottom, top, itemHeight);
        this.onSelect = onSelect;
        this.label = label;
        for (Identifier id : identifiers) {
            this.addEntry(new Entry(id, this));
        }
        if (selected != null) {
            int index = identifiers.indexOf(selected);
            if (scrollAmount < 0 && index != -1) this.setScrollY(index * 20);
            // TODO: update
            //this.setSelected(index);
        }
        if (scrollAmount >= 0) this.setScrollY(scrollAmount);
        this.setFocused(true);
    }

    @Override
    public void setSelected(@Nullable IdentifierListWidget.Entry entry) {
        super.setSelected(entry);
        if (entry != null) this.onSelect.call(entry.id, this);
    }

    // TODO: update
//    @Override
//    protected void renderEntry(DrawContext context, int mouseX, int mouseY, float delta, int index, int x, int y, int entryWidth, int entryHeight) {
//        Entry entry = this.getEntry(index);
//        entry.drawBorder(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, Objects.equals(this.getHoveredEntry(), entry), delta);
//        if (this.isSelectedEntry(index)) this.drawSelectionHighlight(context, y, entryWidth, entryHeight, -1, -16777216);
//        if (this.getHoveredEntry() != null && this.getHoveredEntry().equals(entry)) this.drawSelectionHighlight(context, y, entryWidth, entryHeight, -8355712, -16777216);
//        entry.render(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, Objects.equals(this.getHoveredEntry(), entry), delta);
//    }

    public int getRowWidth() {
        return this.width - 16;
    }

    @Override
    protected int getScrollbarX() {
        return this.getRowRight();
    }

    public static class Entry extends AlwaysSelectedEntryListWidget.Entry<Entry> {
        public final Identifier id;
        public final IdentifierListWidget parent;
        private final Text label;

        public Entry(Identifier id, IdentifierListWidget parent) {
            this.id = id;
            this.parent = parent;
            this.label = parent.label.call(id);
        }

        @Override
        public void render(DrawContext context, int y, int x, boolean hovered, float delta) {
            // TODO: update
            //context.drawCenteredTextWithShadow(ClientData.minecraft.textRenderer, this.label, x + rowWidth / 2, y + (rowHeight - ClientData.minecraft.textRenderer.fontHeight) / 2, 0xFFFFFF);
        }

        @Override
        public Text getNarration() {
            return this.label;
        }

        @Override
        public boolean mouseClicked(Click click, boolean doubled) {
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
        Text call(Identifier identifier);
    }
}