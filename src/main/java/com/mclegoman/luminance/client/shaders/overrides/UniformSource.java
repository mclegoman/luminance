/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.overrides;

import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.events.Events;
import com.mclegoman.luminance.client.shaders.uniforms.Uniform;

public class UniformSource implements OverrideSource {
    protected final String name;

    protected Uniform uniform = null;
    protected int type = 0;

    protected static final String[] types = new String[] {"", "_prev", "_delta", "_smooth", "_smoothPrev", "_smoothDelta"};

    public UniformSource(String name) {
        this.name = name;
    }

    @Override
    public Float get() {
        Uniform uniform = getUniform();
        if (uniform == null) return null;

        return switch (type) {
            default -> null;
            case 0 -> uniform.get();
            case 1 -> uniform.getPrev();
            case 2 -> uniform.getDelta();
            case 3 -> uniform.getSmooth(ClientData.minecraft.getRenderTickCounter().getTickDelta(true));
            case 4 -> uniform.getSmoothPrev();
            case 5 -> uniform.getSmoothDelta();
        };
    }

    @Override
    public String getString() {
        return name;
    }

    public Uniform getUniform() {
        if (uniform == null) {
            Events.ShaderUniform.registry.forEach((id, uniform) -> {
                String s = id.toUnderscoreSeparatedString();
                if (name.startsWith(s)) {
                    for (int i = 0; i < types.length; i++) {
                        if (name.equals(s+types[i])) {
                            this.uniform = uniform;
                            this.type = i;
                            return;
                        }
                    }
                }
            });
        }

        return uniform;
    }
}
