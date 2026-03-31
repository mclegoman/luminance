/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.gui.screen;

import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.gui.widget.ListWidget;
import com.mclegoman.luminance.client.translation.Translation;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractScrollableListScreen extends AbstractScrollableScreen {
	public ListWidget list;

	public AbstractScrollableListScreen(Screen parent) {
		super(parent);
	}

	public AbstractScrollableListScreen(Screen parent, double scrollY) {
		super(parent, scrollY);
	}

	public AbstractScrollableListScreen(String id, Screen parent) {
		super(id, parent);
	}

	public AbstractScrollableListScreen(String id, Screen parent, double scrollY, @Nullable Translation.Data splashText, boolean isPride) {
		super(id, parent, scrollY, splashText, isPride);
	}

	public void initBody() {
		this.list = new ListWidget(ClientData.minecraft, this.width, this.layout.getContentHeight(), this.layout.getHeaderHeight(), 20, getEntries(), this.scrollY);
		this.layout.addBody(this.list);
	}

	public List<ListWidget.ListEntry> getEntries() {
		return new ArrayList<>();
	}
}
