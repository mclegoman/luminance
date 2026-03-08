package com.mclegoman.luminance.client.shaders;

import com.mclegoman.luminance.client.shaders.overrides.UniformOverride;
import com.mclegoman.luminance.client.shaders.uniforms.config.EmptyConfig;
import com.mclegoman.luminance.client.shaders.uniforms.config.UniformConfig;
import org.jetbrains.annotations.Nullable;

import java.util.List;

// wrapper to link overrides and configs
public class UniformData {
    public @Nullable UniformOverride override;
    public @Nullable UniformConfig config;

    public List<Float> getValues() {
        if (override == null) {
            return null;
        }

        return override.getOverride(config == null ? EmptyConfig.INSTANCE : config, Uniforms.shaderTime);
    }
}
