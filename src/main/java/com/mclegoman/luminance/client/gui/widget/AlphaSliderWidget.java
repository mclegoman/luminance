/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.gui.widget;

import com.mclegoman.luminance.client.config.LuminanceConfig;
import com.mclegoman.luminance.client.shaders.Uniforms;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.common.data.Data;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class AlphaSliderWidget extends SliderWidget {
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

    private static Text getText() {
        return Translation.getConfigTranslation(Data.getVersion().getID(), "alpha", new Object[]{Text.literal(Uniforms.getRawAlpha() + "%")}, false);
    }
}