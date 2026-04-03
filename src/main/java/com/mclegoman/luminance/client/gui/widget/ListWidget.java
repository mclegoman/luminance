/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.gui.widget;

import com.mclegoman.luminance.client.data.ClientData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;

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

	public ListWidget(MinecraftClient client, int width, int height, int y, int itemHeight, List<ListEntry> entries, double scrollY) {
		super(client, width, height, y, itemHeight);
		for (ListEntry entry : entries) {
			addEntry(entry);
		}
		setScrollY(scrollY);
	}

	public int getRowWidth() {
		return this.width - 32;
	}

	protected void appendClickableNarrations(NarrationMessageBuilder builder) {
	}

	@Override
	protected boolean isEntrySelectionAllowed() {
		return false;
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
		public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
			int totalSpacing = (this.widgets.length - 1) * this.spacing;
			int widgetWidth = (this.getWidth() - totalSpacing) / this.widgets.length;
			int currentX = this.getX();
			int verticalSpacing = this.spacing / 4;
			int widgetHeight = this.getHeight() - (verticalSpacing * 2);
			int yOffset = this.getY() + verticalSpacing;
			for (ClickableWidget widget : this.widgets) {
				widget.setDimensions(widgetWidth, widgetHeight);
				widget.setPosition(currentX, yOffset);
				widget.render(context, mouseX, mouseY, ClientData.minecraft.getRenderTickCounter().getTickProgress(true));
				currentX += widgetWidth + this.spacing;
			}
		}

		@Override
		public boolean mouseClicked(Click click, boolean doubled) {
			this.selected = null;
			Optional<ClickableWidget> widget = Arrays.stream(this.widgets).filter((w) -> w.isMouseOver(click.x(), click.y()) && w.mouseClicked(click, doubled)).findFirst();
			if (widget.isPresent()) {
				this.selected = widget.get();
				return true;
			}
			return false;
		}

		@Override
		public boolean mouseReleased(Click click) {
			if (this.selected != null) {
				boolean result = this.selected.mouseReleased(click);
				this.selected = null;
				return result;
			}
			return false;
		}

		@Override
		public boolean mouseDragged(Click click, double offsetX, double offsetY) {
			if (this.selected != null) {
				return this.selected.mouseDragged(click, offsetX, offsetY);
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
		public boolean keyPressed(KeyInput input) {
			for (ClickableWidget widget : widgets) if (widget.keyPressed(input)) return true;
			return false;
		}

		@Override
		public boolean keyReleased(KeyInput input) {
			for (ClickableWidget widget : widgets) if (widget.keyReleased(input)) return true;
			return false;
		}

		@Override
		public boolean charTyped(CharInput input) {
			for (ClickableWidget widget : widgets) if (widget.charTyped(input)) return true;
			return false;
		}
	}
}