/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.interfaces.pipeline;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public interface PipelineTargetInterface {
    @Nullable
    PipelineTargetInterface.DynamicSize luminance$getDynamicSize();
    void luminance$setDynamicSize(DynamicSize dynamicSize);

    record DynamicSize(Calculation width, Calculation height) {
        public static Codec<DynamicSize> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                Codec.STRING.optionalFieldOf("width", "w1").forGetter(DynamicSize::toString),
                Codec.STRING.optionalFieldOf("height", "h1").forGetter(DynamicSize::toString)
        ).apply(instance, (w,h) -> new DynamicSize(Calculation.parse(w), Calculation.parse(h))));

        public record Calculation(float add, float width, float height) {
            public int run(float width, float height) {
                return Math.max(Mth.floor(this.add + this.width*width + this.height*height), 1);
            }

            @Override
            public String toString() {
                StringBuilder s = new StringBuilder();
                if (add != 0) s.append('+').append(add);
                if (width != 0) s.append('w').append(width);
                if (height != 0) s.append('h').append(height);
                return s.toString();
            }

            static Calculation parse(String str) {
                try {
                    float value = Float.parseFloat(str);
                    return new Calculation(value, 0, 0);
                } catch (Exception ignored) {}

                float add = 0f;
                float width = 0f;
                float height = 0f;
                for (String parameter : str.replace(" ", "").split("(?=[+\\-wh])")) {
                    try {
                        float value;
                        char type = parameter.charAt(0);
                        if (parameter.length() == 1 && type == 'w' || type == 'h') {
                            value = 1;
                        } else {
                            char first = parameter.charAt(1);
                            parameter = parameter.replace('~','-');
                            if (first == '/') {
                                value = 1f / Float.parseFloat(parameter.substring(2));
                            } else {
                                value = Float.parseFloat(parameter.substring((first == '*' || first == 'x') ? 2 : 1));
                            }
                        }
                        switch (type) {
                            case '+': add += value; break;
                            case '-': add -= value; break;
                            case 'w': width += value; break;
                            case 'h': height += value; break;
                        }
                    } catch (Exception ignored) {}
                }
                return new Calculation(add, width, height);
            }
        }
    }
}
