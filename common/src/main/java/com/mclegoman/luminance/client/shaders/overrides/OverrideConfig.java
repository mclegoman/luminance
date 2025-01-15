package com.mclegoman.luminance.client.shaders.overrides;

import com.mclegoman.luminance.client.shaders.uniforms.config.UniformConfig;

import java.util.List;

public class OverrideConfig implements UniformConfig {
    protected UniformConfig uniformConfig;

    private int index;

    public OverrideConfig(UniformConfig uniformConfig) {
        this.uniformConfig = uniformConfig;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    private String preprocessName(String name) {
        return index+"_"+name;
    }

    @Override
    public Number getOrDefault(String name, int index, Number defaultValue) {
        return uniformConfig.getOrDefault(preprocessName(name), index, defaultValue);
    }

    @Override
    public List<Object> get(String name) {
        return uniformConfig.get(preprocessName(name));
    }
}
