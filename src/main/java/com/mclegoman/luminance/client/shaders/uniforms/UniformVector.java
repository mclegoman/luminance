/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.uniforms;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class UniformVector {
    public List<Float> values;

    public UniformVector(int length) {
        this(new ArrayList<>(length));
        for (int i = 0; i < length; i++) {
            values.add(0f);
        }
    }

    protected UniformVector(List<Float> values) {
        this.values = values;
    }

    @Nullable
    public static UniformVector fromFloat(@Nullable Float f, int length) {
        if (f == null) {
            return null;
        }
        UniformVector uniformVector = new UniformVector(length);
        for (int i = 0; i < length; i++) {
            uniformVector.values.set(i, f);
        }
        return uniformVector;
    }

    public UniformVector copyTo(@Nullable UniformVector other) {
        if (other == null || other.values.size() != values.size()) {
            return new UniformVector(new ArrayList<>(values));
        } else {
            other.elementwise((a,b) -> b, this);
            return other;
        }
    }


    public void set(int index, float value) {
        if (index >= 0 && index < values.size()) {
            values.set(index, value);
        }
    }

    public void set(Vec3 vec3d) {
        if (values.size() == 3) {
            values.set(0, (float)vec3d.x);
            values.set(1, (float)vec3d.y);
            values.set(2, (float)vec3d.z);
        }
    }

    public void set(float[] value) {
        if (values.size() == value.length) {
            for (int i = 0; i < value.length; i++) values.set(i, value[i]);
        }
    }

    public void min(UniformVector other) {
        elementwise(Math::min, other);
    }

    public void max(UniformVector other) {
        elementwise(Math::max, other);
    }

    public void lerp(UniformVector other, float t) {
        elementwise((a,b) -> Mth.lerp(t, a, b), other);
    }

    public void subtract(UniformVector other) {
        elementwise((a,b) -> a-b, other);
    }

    public void delta(UniformVector other) {
        elementwise((a,b) -> b-a, other);
    }

    public void elementwise(BiFunction<Float, Float, Float> function, UniformVector other) {
        assert lengthEqual(other);
        for (int i = 0; i < values.size(); i++) {
            values.set(i, function.apply(values.get(i), other.values.get(i)));
        }
    }

    public boolean lengthEqual(UniformVector other) {
        return other.values.size() == values.size();
    }

    public void loopLerp(UniformVector other, float t, @Nullable UniformVector min, @Nullable UniformVector max) {
        if (min == null || max == null) {
            lerp(other, t);
            return;
        }

        for (int i = 0; i < values.size(); i++) {
            float value = values.get(i);
            float minValue = min.values.get(i);
            float maxValue = max.values.get(i);
            float range = maxValue - minValue;

            float lerp = value + t * wrapDelta(other.values.get(i) - value, range);
            values.set(i, lerp > maxValue ? lerp - range : (lerp < minValue ? lerp + range : lerp));
        }
    }

    public void loopDelta(UniformVector other, @Nullable UniformVector min, @Nullable UniformVector max) {
        if (min == null || max == null) {
            delta(other);
            return;
        }

        for (int i = 0; i < values.size(); i++) {
            values.set(i, wrapDelta(other.values.get(i) - values.get(i), max.values.get(i) - min.values.get(i)));
        }
    }

    private float wrapDelta(float delta, float range) {
        return Mth.abs(delta) < range/2 ? delta : (delta > 0 ? delta - range : delta + range);
    }
}
