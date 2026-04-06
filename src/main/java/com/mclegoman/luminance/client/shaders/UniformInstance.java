package com.mclegoman.luminance.client.shaders;

import com.google.common.collect.ImmutableList;
import com.mclegoman.luminance.client.shaders.overrides.UniformOverride;
import com.mclegoman.luminance.client.shaders.uniforms.config.EmptyConfig;
import com.mclegoman.luminance.client.shaders.uniforms.config.UniformConfig;
import com.mojang.blaze3d.buffers.Std140Builder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

// wrapper to link overrides and configs
public class UniformInstance {
    public final @NotNull String name;
    public final @NotNull ImmutableList<Number> defaultValue;
    public @Nullable UniformOverride override;
    public @Nullable UniformConfig config;

    public UniformInstance(@NotNull String name, @NotNull ImmutableList<Number> defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public @Nullable List<Float> getValues() {
        if (override == null) {
            return null;
        }

        return override.getOverride(config == null ? EmptyConfig.INSTANCE : config, Uniforms.shaderTime);
    }

    public void putValues(Std140Builder builder) {
        List<Float> values = getValues();

        // we write the values sequentially instead of using putVecN, so buffer alignment needs to be handled manually
        // doing a bunch of checks to do the function calls nicely would be a little awkward (since vector types dont like being handled generically)
        // but maybe storing the result of those calculations with a function reference would be decent?
        // this is *way* simpler though

        // vec3s are aligned to 16 bytes, instead of the expected 12
        // this is actually handled incorrectly by minecraft, see: https://bugs.mojang.com/browse/MC/issues/MC-307206
        // but we have a mixin to fix this, so that wouldn't matter, and we could use the putVec3 function
        if (defaultValue.size() >= 3) {
            builder.align(16);
        } else if (defaultValue.size() == 2){
            builder.align(8);
        }

        if (values == null) {
            for (Number number : defaultValue) {
                putValue(builder, number, null);
            }
        } else {
            for (int i = 0; i < defaultValue.size(); i++) {
                Float value = values.get(i);
                putValue(builder, defaultValue.get(i), value);
            }
        }
    }

    private void putValue(Std140Builder builder, Number defaultValue, @Nullable Float overrideValue) {
        if (overrideValue != null) {
            // match type
            if (defaultValue instanceof Float) {
                builder.putFloat(overrideValue);
            } else if (defaultValue instanceof Integer) {
                builder.putInt(Math.round(overrideValue));
            }
            return;
        }

        if (defaultValue instanceof Float f) {
            builder.putFloat(f);
        } else if (defaultValue instanceof Integer i) {
            builder.putInt(i);
        }
    }

    public int length() {
        return defaultValue.size();
    }
}
