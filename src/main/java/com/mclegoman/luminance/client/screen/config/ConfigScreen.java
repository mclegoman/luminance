/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

// This class is deprecated, Use com.mclegoman.luminance.client.gui.screen.config.ConfigScreen instead!
// This class is intended for use with older versions of soup, or other mods that open the luminance config screen/alpha slider.

package com.mclegoman.luminance.client.screen.config;

import com.mclegoman.luminance.client.LuminanceClient;
import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.gui.widget.AlphaSliderWidget;
import com.mclegoman.luminance.client.translation.Translation;
import dev.dannytaylor.perspective.seam.common.data.log.SeamLog;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

@Deprecated
public class ConfigScreen extends Screen {
    private final Screen parent;
    private final boolean isPride;
    private final Translation.Data splashText;

    public ConfigScreen(Screen parent, boolean refresh, boolean saveConfig, Translation.Data splashText, boolean isPride) {
        this(parent, refresh, saveConfig, splashText, isPride, false);
    }

    public ConfigScreen(Screen parent, boolean refresh, boolean saveConfig, Translation.Data splashText, boolean isPride, boolean invis) {
        super(Component.empty());
        this.parent = parent;
        this.splashText = splashText;
        this.isPride = isPride;
    }

    public ConfigScreen(Screen parent, boolean refresh, Translation.Data splashText, boolean isPride) {
        this(parent, refresh, false, splashText, isPride);
    }

    public ConfigScreen(Screen parent, Translation.Data splashText, boolean isPride) {
        this(parent, false, false, splashText, isPride);
    }

    public ConfigScreen(Screen parent, boolean refresh, boolean saveConfig, boolean isPride) {
        this(parent, refresh, saveConfig, null, isPride);
    }

    public ConfigScreen(Screen parent, boolean refresh, boolean isPride) {
        this(parent, refresh, false, null, isPride);
    }

    public ConfigScreen(Screen parent, boolean isPride) {
        this(parent, false, false, null, isPride);
    }

    public void init() {
        SeamLog.warn(LuminanceClient.getMod(), "You are using a deprecated class (screen/config/ConfigScreen), if you are a developer please update to use (gui/screen/config/ConfigScreen) instead.");
        ClientData.minecraft.setScreen(new com.mclegoman.luminance.client.gui.screen.config.ConfigScreen(this.parent, -1, this.splashText, this.isPride));
    }
    @Deprecated
    public static class AlphaSlider extends AlphaSliderWidget {
        public AlphaSlider(int x, int y, int width, int height, double value, Runnable onChange) {
            super(x, y, width, height, value, onChange);
            SeamLog.warn(LuminanceClient.getMod(), "You are using a deprecated class (screen/config/ConfigScreen$AlphaSlider), if you are a developer please update to use (gui/widget/AlphaSliderWidget) instead.");
        }
    }
}
