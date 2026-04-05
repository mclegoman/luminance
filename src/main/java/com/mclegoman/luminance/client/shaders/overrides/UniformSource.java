/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.overrides;

import com.mclegoman.luminance.client.events.Events;
import com.mclegoman.luminance.client.shaders.ShaderTime;
import com.mclegoman.luminance.client.shaders.uniforms.Uniform;
import com.mclegoman.luminance.client.shaders.uniforms.UniformVector;
import com.mclegoman.luminance.client.shaders.uniforms.config.*;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class UniformSource implements OverrideSource {
    @NotNull
    protected final Identifier identifier;

    @Nullable
    protected Uniform uniform = null;

    protected UniformConfig configTemplate;

    public UniformSource(@NotNull Identifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public Float get(UniformConfig config, ShaderTime shaderTime) {
        Uniform uniform = getUniform();
        if (uniform == null) return null;

        Float f = uniform.get(config, shaderTime).values.getFirst();
        if (f == null) return null;

        List<Object> range = config.getObjects("range");
        if (range == null || range.size() < 2) {
            return f;
        }

        Float min = uniform.getMin(config, shaderTime).map(value -> value.values.getFirst()).orElse(null);
        Float max = uniform.getMax(config, shaderTime).map(value -> value.values.getFirst()).orElse(null);
        return remapRange(remap01(f, min, max), min, max, range.get(0), range.get(1));
    }

    @Override
    public String getString() {
        return identifier.toString();
    }

    @Override
    public UniformConfig getTemplateConfig() {
        Uniform uniform = getUniform();
        if (uniform == null) return EmptyConfig.INSTANCE;
        return new DefaultableConfig(uniform.getDefaultConfig(), configTemplate);
    }

    public Uniform getUniform() {
        if (uniform == null) {
            uniform = Events.ShaderUniform.registry.get(identifier);

            configTemplate = nullRange;
            if (uniform != null && !uniform.rangeCanChange()) {
                Optional<UniformVector> min = uniform.getMin(null, null);
                Optional<UniformVector> max = uniform.getMax(null, null);
                if (min.isPresent() && max.isPresent()) {
                    ArrayList<Object> objects = new ArrayList<>(2);
                    objects.add(min.get().values.getFirst());
                    objects.add(max.get().values.getFirst());
                    configTemplate = new MapConfig(Map.of("range", objects));
                } else {
                    configTemplate = new MapConfig(Map.of("range", List.of(0.0f, 1.0f)));
                }
            }
        }

        return uniform;
    }

    @Nullable @Contract("_, null, _ -> param1; _, _, null -> param1; null, _, _ -> null; !null, _, _ -> !null;")
    public static Float remap01(@Nullable Float f, @Nullable Float min, @Nullable Float max) {
        if (f == null || min == null || max == null) {
            return f;
        }

        return (f - min) / (max - min);
    }

    @NotNull
    public static Float remapRange(@NotNull Float f, @Nullable Float min, @Nullable Float max, @Nullable Object rangeMin, @Nullable Object rangeMax) {
        float a = rangeMin == null ? (min == null ? 0 : min) : ((Number)rangeMin).floatValue();
        float b = rangeMax == null ? (max == null ? 1 : max) : ((Number)rangeMax).floatValue();
        return a + (b - a) * f;
    }

    private static final UniformConfig nullRange = new MapConfig(Map.of("range", new ArrayList<>(Collections.nCopies(2, null))));
}
