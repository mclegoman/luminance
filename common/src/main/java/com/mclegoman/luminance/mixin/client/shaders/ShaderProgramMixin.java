/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.client.shaders;

import com.mclegoman.luminance.client.shaders.interfaces.ShaderProgramInterface;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(ShaderProgram.class)
public abstract class ShaderProgramMixin implements ShaderProgramInterface {
    @Shadow @Nullable public abstract GlUniform getUniform(String name);

    @Override
    public List<Float> luminance$getCurrentUniformValues(String name) {
        GlUniform uniform = getUniform(name);
        if (uniform == null) {
            return null;
        }

        List<Float> values = new ArrayList<>(uniform.getCount());
        if (uniform.getDataType() <= 3) {
            int[] arr = new int[uniform.getCount()];
            uniform.getIntData().position(0);
            uniform.getIntData().get(arr);
            for (int i : arr) {
                values.add((float)i);
            }
        } else {
            float[] arr = new float[uniform.getCount()];
            uniform.getFloatData().position(0);
            uniform.getFloatData().get(arr);
            for (float f : arr) {
                values.add(f);
            }
        }

        return values;
    }
}
