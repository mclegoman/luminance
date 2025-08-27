/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.gui.widget;

import com.mclegoman.luminance.client.data.ClientData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.EntryListWidget;

import java.util.*;

public class ListWidget extends EntryListWidget<ListWidget.ListEntry> {
	public static ListWidget fromWidgets(MinecraftClient client, int width, int height, int y, int lineHeight, Map<Integer, ClickableWidget[]> rows, double scrollY) {
		List<ListEntry> entries = new ArrayList<>();
		rows.forEach((spacing, widgets) -> entries.add(new ListEntry(spacing, widgets)));
		return new ListWidget(client, width, height, y, lineHeight, entries, scrollY);
	}

	public static ListWidget fromWidgets(MinecraftClient client, int width, int height, int y, int lineHeight, List<ClickableWidget[]> rows, double scrollY) {
		return fromWidgets(client, width, height, y, lineHeight, rowListToMap(rows), scrollY);
	}

	public static Map<Integer, ClickableWidget[]> rowListToMap(List<ClickableWidget[]> rows) {
		return rowListToMap(4, rows);
	}

	public static Map<Integer, ClickableWidget[]> rowListToMap(int spacing, List<ClickableWidget[]> rows) {
		Map<Integer, ClickableWidget[]> rowMap = new HashMap<>();
		for (ClickableWidget[] row : rows) rowMap.put(spacing, row);
		return rowMap;
	}

	public ListWidget(MinecraftClient client, int width, int height, int y, int lineHeight, List<ListEntry> entries, double scrollY) {
		super(client, width, height, y, lineHeight);
		for (ListEntry entry : entries) addEntry(entry);
		setScrollY(scrollY);
	}

	public int getRowWidth() {
		return this.width - 24;
	}

	protected void appendClickableNarrations(NarrationMessageBuilder builder) {
	}

	protected void renderEntry(DrawContext context, int mouseX, int mouseY, float delta, int index, int x, int y, int entryWidth, int entryHeight) {
		ListEntry entry = this.getEntry(index);
		entry.render(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, Objects.equals(this.getHoveredEntry(), entry), delta);
	}

	public static class ListEntry extends Entry<ListEntry> {
		private final int spacing;
		private final ClickableWidget[] widgets;
		private ClickableWidget selected = null;

		public ListEntry(ClickableWidget... widgets) {
			this(4, widgets);
		}

		public ListEntry(int spacing, ClickableWidget... widgets) {
			this.spacing = spacing;
			this.widgets = widgets;
		}

		@Override
		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
			int totalSpacing = (this.widgets.length - 1) * this.spacing;
			int widgetWidth = (entryWidth - totalSpacing) / this.widgets.length;
			int currentX = x;
			for (ClickableWidget widget : this.widgets) {
				widget.setDimensions(widgetWidth, entryHeight);
				widget.setPosition(currentX, y);
				widget.render(context, mouseX, mouseY, ClientData.minecraft.getRenderTickCounter().getTickDelta(true));
				currentX += widgetWidth + this.spacing;
			}
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			this.selected = null;
			Optional<ClickableWidget> widget = Arrays.stream(this.widgets).filter((w) -> w.isMouseOver(mouseX, mouseY) && w.mouseClicked(mouseX, mouseY, button)).findFirst();
			if (widget.isPresent()) {
				this.selected = widget.get();
				return true;
			}
			return false;
		}

		@Override
		public boolean mouseReleased(double mouseX, double mouseY, int button) {
			if (this.selected != null) {
				boolean result = this.selected.mouseReleased(mouseX, mouseY, button);
				this.selected = null;
				return result;
			}
			return false;
		}

		@Override
		public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
			if (this.selected != null) {
				return this.selected.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
			}
			return false;
		}

		@Override
		public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
			for (ClickableWidget widget : widgets) {
				if (widget.isMouseOver(mouseX, mouseY) && widget.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public boolean isMouseOver(double mouseX, double mouseY) {
			return Arrays.stream(widgets).anyMatch(w -> w.isMouseOver(mouseX, mouseY));
		}

		@Override
		public void mouseMoved(double mouseX, double mouseY) {
			for (ClickableWidget widget : widgets) widget.mouseMoved(mouseX, mouseY);
		}

		@Override
		public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
			for (ClickableWidget widget : widgets) if (widget.keyPressed(keyCode, scanCode, modifiers)) return true;
			return false;
		}

		@Override
		public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
			for (ClickableWidget widget : widgets) if (widget.keyReleased(keyCode, scanCode, modifiers)) return true;
			return false;
		}

		@Override
		public boolean charTyped(char chr, int modifiers) {
			for (ClickableWidget widget : widgets) if (widget.charTyped(chr, modifiers)) return true;
			return false;
		}
	}
}

