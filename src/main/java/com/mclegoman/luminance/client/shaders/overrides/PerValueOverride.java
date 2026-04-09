/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.overrides;

import com.mclegoman.luminance.client.shaders.ShaderTime;
import com.mclegoman.luminance.client.shaders.uniforms.config.UniformConfig;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public class PerValueOverride implements UniformOverride {
    public final List<OverrideSource> overrideSources;

    protected final List<Float> values;

    public PerValueOverride(List<String> overrideStrings) {
        values = new ArrayList<>(overrideStrings.size());
        overrideSources = new ArrayList<>(overrideStrings.size());

        for (String string : overrideStrings) {
            values.add(null);
            overrideSources.add(sourceFromString(string));
        }
    }

    @Override
    public List<Float> getOverride(UniformConfig config, ShaderTime shaderTime) {
        updateValues(config, shaderTime);
        return values;
    }

    @Override
    public UniformOverride copy() {
        return new PerValueOverride(getStrings());
    }

    protected void updateValues(UniformConfig config, ShaderTime shaderTime) {
        PerValueConfig perValueConfig = new PerValueConfig(config);
        for (int i = 0; i < values.size(); i++) {
            OverrideSource overrideSource = overrideSources.get(i);
            perValueConfig.setIndex(i);
            values.set(i, overrideSource != null ? overrideSource.get(perValueConfig, shaderTime) : null);
        }
    }

    public List<String> getStrings() {
        List<String> strings = new ArrayList<>(overrideSources.size());
        for (OverrideSource source : overrideSources) {
            strings.add(source != null ? source.getString() : null);
        }
        return strings;
    }

    public static OverrideSource sourceFromString(String string) {
        if (string == null) {
            return null;
        }
        try {
            float value = Float.parseFloat(string);
            return new FixedValueSource(value);
        } catch (Exception ignored) {
            // only accept namespaced indexes as uniforms
            int i = string.indexOf(':');
            if (i >= 0) {
                Identifier identifier = Identifier.tryBuild(string.substring(0, i), string.substring(i+1));
                if (identifier != null) {
                    return new UniformSource(identifier);
                }
            }
            return new NullSource(string);
        }
    }
}
