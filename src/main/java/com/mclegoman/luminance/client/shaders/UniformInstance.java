package com.mclegoman.luminance.client.shaders;

import com.google.common.collect.ImmutableList;
import com.mclegoman.luminance.client.shaders.overrides.UniformOverride;
import com.mclegoman.luminance.client.shaders.uniforms.config.EmptyConfig;
import com.mclegoman.luminance.client.shaders.uniforms.config.UniformConfig;
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
}
