/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.gui.widget;

import com.mclegoman.luminance.client.LuminanceClient;
import com.mclegoman.luminance.client.config.LuminanceConfig;
import com.mclegoman.luminance.client.shaders.Uniforms;
import com.mclegoman.luminance.client.translation.Translation;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

public class AlphaSliderWidget extends AbstractSliderButton {
    private final Runnable onChange;

    public AlphaSliderWidget(int x, int y, int width, int height, double value, Runnable onChange) {
        super(x, y, width, height, getText(), value);
        this.onChange = onChange;
    }

    @Override
    protected void updateMessage() {
        setMessage(getText());
    }

    @Override
    protected void applyValue() {
        LuminanceConfig.config.alphaLevel.setValue((int) ((value) * 100), false);
        onChange.run();
    }

    private static Component getText() {
        return Translation.getConfigTranslation(LuminanceClient.getMod().getId(), "alpha", new Object[]{Component.literal(Uniforms.getRawAlpha() + "%")}, false);
    }
}