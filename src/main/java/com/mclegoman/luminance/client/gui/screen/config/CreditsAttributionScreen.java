/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.gui.screen.config;

import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.gui.screen.AbstractScrollableScreen;
import com.mclegoman.luminance.client.gui.widget.InfoWidget;
import com.mclegoman.luminance.client.translation.Translation;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

public class CreditsAttributionScreen extends AbstractScrollableScreen {
	public InfoWidget info;

	public CreditsAttributionScreen(Screen parent, double scrollY, Translation.@Nullable Data splashText, boolean isPride) {
		super("credits_attribution", parent, scrollY, splashText, isPride);
	}

	public void initBody() {
		this.info = new InfoWidget(ClientData.minecraft, this.width, this.layout.getContentHeight(), this.layout.getHeaderHeight(), 11, this.scrollY);
		this.layout.addToContents(this.info);
	}

	public Screen getRefreshScreen() {
		return new CreditsAttributionScreen(this.parent, this.info != null ? this.info.scrollAmount() : scrollY, this.splashText, this.isPride);
	}

	// make sure shaders update properly while in the config screens
	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
