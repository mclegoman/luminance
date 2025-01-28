/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.overrides;

import com.mclegoman.luminance.client.shaders.uniforms.config.MapConfig;
import com.mclegoman.luminance.client.shaders.uniforms.config.UniformConfig;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class OverrideConfig implements UniformConfig {
    protected UniformConfig uniformConfig;

    protected int index;

    public OverrideConfig(UniformConfig uniformConfig) {
        this.uniformConfig = uniformConfig;
    }

    public OverrideConfig(UniformConfig uniformConfig, int index) {
        MapConfig mapConfig = new MapConfig(List.of());
        String prefix = index+"_";
        for (String name : uniformConfig.getNames()) {
            mapConfig.config.put(prefix+name, uniformConfig.getObjects(name));
        }
        this.uniformConfig = mapConfig;
        this.index = index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    private String preprocessName(String name) {
        return index >= 0 ? index+"_"+name : name;
    }

    @Override
    public Set<String> getNames() {
        return uniformConfig.getNames();
    }

    @Override @Nullable
    public List<Object> getObjects(String name) {
        return uniformConfig.getObjects(preprocessName(name));
    }

    @Override
    public Optional<Number> getNumber(String name, int index) {
        return uniformConfig.getNumber(preprocessName(name), index);
    }
}
