/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.overrides;

import com.mclegoman.luminance.client.events.Events;
import com.mclegoman.luminance.client.shaders.ShaderTime;
import com.mclegoman.luminance.client.shaders.Uniforms;
import com.mclegoman.luminance.client.shaders.uniforms.Uniform;
import com.mclegoman.luminance.client.shaders.uniforms.config.UniformConfig;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LuminanceUniformOverride implements UniformOverride {
    public final List<OverrideSource> overrideSources;

    protected final List<Float> values;

    public LuminanceUniformOverride(List<String> overrideStrings) {
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

    protected void updateValues(UniformConfig config, ShaderTime shaderTime) {
        OverrideConfig overrideConfig = new OverrideConfig(config);
        for (int i = 0; i < values.size(); i++) {
            OverrideSource overrideSource = overrideSources.get(i);
            overrideConfig.setIndex(i);
            values.set(i, overrideSource != null ? overrideSource.get(overrideConfig, shaderTime) : null);
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
        //TODO: account for vanilla dynamic uniforms (time, insize(?), outsize(?) [those two probably arent needed])
        //(this should be done by just registering them as LuminanceUniforms, that way they interact with all the other systems)
        try {
            float value = Float.parseFloat(string);
            return new FixedValueSource(value);
        } catch (Exception ignored) {
            return new UniformSource(string);
        }
    }

    @Nullable
    public static LuminanceUniformOverride overrideFromUniform(String name) {
        Uniform uniform = Events.ShaderUniform.registry.get(name);
        if (uniform == null) return null;

        List<String> overrideStrings = new ArrayList<>();
        int count = uniform.get(uniform.getDefaultConfig(), Uniforms.shaderTime).values.size();
        if (count <= 4) {
            if (count == 1) {
                overrideStrings.add(name);
            }
            if (count >= 2) {
                overrideStrings.add(name + "_x");
                overrideStrings.add(name + "_y");
            }
            if (count >= 3) {
                overrideStrings.add(name + "_z");
            }
            if (count == 4) {
                overrideStrings.add(name + "_w");
            }
        }

        for (int i = 0; i < count; i++) {
            if (i > overrideStrings.size()) {
                overrideStrings.add(null);
                continue;
            }

            String s = overrideStrings.get(i);
            if (s != null && Events.ShaderUniform.registry.get(s) == null) {
                overrideStrings.set(i, null);
            }
        }

        return new LuminanceUniformOverride(overrideStrings);
    }
}
