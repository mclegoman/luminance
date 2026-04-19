/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.gui.widget;

import com.mclegoman.luminance.client.data.ClientData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ListWidget extends AbstractSelectionList<ListWidget.ListEntry> {
	public static ListWidget fromWidgets(Minecraft client, int width, int height, int y, int lineHeight, Map<Integer, AbstractWidget[]> rows, double scrollY) {
		List<ListEntry> entries = new ArrayList<>();
		rows.forEach((spacing, widgets) -> entries.add(new ListEntry(spacing, widgets)));
		return new ListWidget(client, width, height, y, lineHeight, entries, scrollY);
	}

	public static ListWidget fromWidgets(Minecraft client, int width, int height, int y, int lineHeight, List<AbstractWidget[]> rows, double scrollY) {
		return fromWidgets(client, width, height, y, lineHeight, rowListToMap(rows), scrollY);
	}

	public static Map<Integer, AbstractWidget[]> rowListToMap(List<AbstractWidget[]> rows) {
		return rowListToMap(4, rows);
	}

	public static Map<Integer, AbstractWidget[]> rowListToMap(int spacing, List<AbstractWidget[]> rows) {
		Map<Integer, AbstractWidget[]> rowMap = new HashMap<>();
		for (AbstractWidget[] row : rows) rowMap.put(spacing, row);
		return rowMap;
	}

	public ListWidget(Minecraft client, int width, int height, int y, int itemHeight, List<ListEntry> entries, double scrollY) {
		super(client, width, height, y, itemHeight);
		for (ListEntry entry : entries) {
			addEntry(entry);
		}
		setScrollAmount(scrollY);
	}

	public int getRowWidth() {
		return this.width - 32;
	}

	protected void updateWidgetNarration(NarrationElementOutput builder) {
	}

	@Override
	protected boolean entriesCanBeSelected() {
		return false;
	}

	public static class ListEntry extends Entry<ListEntry> {
		private final int spacing;
		private final AbstractWidget[] widgets;
		private AbstractWidget selected = null;

		public ListEntry(AbstractWidget... widgets) {
			this(4, widgets);
		}

		public ListEntry(int spacing, AbstractWidget... widgets) {
			this.spacing = spacing;
			this.widgets = widgets;
		}

		@Override
		public void renderContent(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
			int totalSpacing = (this.widgets.length - 1) * this.spacing;
			int widgetWidth = (this.getWidth() - totalSpacing) / this.widgets.length;
			int currentX = this.getX();
			int verticalSpacing = this.spacing / 4;
			int widgetHeight = this.getHeight() - (verticalSpacing * 2);
			int yOffset = this.getY() + verticalSpacing;
			for (AbstractWidget widget : this.widgets) {
				widget.setSize(widgetWidth, widgetHeight);
				widget.setPosition(currentX, yOffset);
				widget.render(guiGraphics, mouseX, mouseY, ClientData.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(true));
				currentX += widgetWidth + this.spacing;
			}
		}

		@Override
		public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
			this.selected = null;
			Optional<AbstractWidget> widget = Arrays.stream(this.widgets).filter((w) -> w.isMouseOver(click.x(), click.y()) && w.mouseClicked(click, doubled)).findFirst();
			if (widget.isPresent()) {
				this.selected = widget.get();
				return true;
			}
			return false;
		}

		@Override
		public boolean mouseReleased(MouseButtonEvent click) {
			if (this.selected != null) {
				boolean result = this.selected.mouseReleased(click);
				this.selected = null;
				return result;
			}
			return false;
		}

		@Override
		public boolean mouseDragged(MouseButtonEvent click, double offsetX, double offsetY) {
			if (this.selected != null) {
				return this.selected.mouseDragged(click, offsetX, offsetY);
			}
			return false;
		}

		@Override
		public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
			for (AbstractWidget widget : widgets) {
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
			for (AbstractWidget widget : widgets) widget.mouseMoved(mouseX, mouseY);
		}

		@Override
		public boolean keyPressed(KeyEvent input) {
			for (AbstractWidget widget : widgets) if (widget.keyPressed(input)) return true;
			return false;
		}

		@Override
		public boolean keyReleased(KeyEvent input) {
			for (AbstractWidget widget : widgets) if (widget.keyReleased(input)) return true;
			return false;
		}

		@Override
		public boolean charTyped(CharacterEvent input) {
			for (AbstractWidget widget : widgets) if (widget.charTyped(input)) return true;
			return false;
		}
	}
}