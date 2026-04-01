/*
    Luminance
    Contributor(s): Nettakrim (also indirectly: cputnam-a11y in the fabric discord for showing me how to do the codec wrapping magic)
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.client.shaders;

import com.google.common.collect.ImmutableList;
import com.mclegoman.luminance.client.shaders.interfaces.pipeline.UniformValueInterface;
import com.mclegoman.luminance.client.shaders.uniforms.config.ConfigData;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import net.minecraft.client.gl.UniformValue;
import org.jetbrains.annotations.NotNull;
import org.joml.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.Optional;

@Mixin({UniformValue.IntValue.class, UniformValue.FloatValue.class, UniformValue.Vec2fValue.class, UniformValue.Vec3fValue.class, UniformValue.Vec4fValue.class, UniformValue.Vec3iValue.class, UniformValue.Matrix4fValue.class})
public abstract class UniformValueImplMixin implements UniformValueInterface {
    @Shadow(remap = false) public abstract void addSize(Std140SizeCalculator calculator);

    @Unique
    private String name;

    @Override
    public Optional<String> luminance$getName() {
        return Optional.ofNullable(name);
    }

    @Override
    public void luminance$setName(String name) {
        this.name = name;
    }

    @Unique
    private List<String> luminance$override;

    @Override
    public Optional<List<String>> luminance$getOverride() {
        return Optional.ofNullable(luminance$override);
    }

    @Override
    public void luminance$setOverride(List<String> override) {
        this.luminance$override = override;
    }

    @Unique
    private List<ConfigData> luminance$config;

    @Override
    public Optional<List<ConfigData>> luminance$getConfig() {
        return Optional.ofNullable(luminance$config);
    }

    @Override
    public void luminance$setConfig(List<ConfigData> config) {
        this.luminance$config = config;
    }

    @Override
    public int luminance$getLength() {
        Std140SizeCalculator calculator = new Std140SizeCalculator();
        addSize(calculator);
        // this could break if they add a half/double uniform, but currently everything is 4 bytes per value
        return calculator.get() / 4;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public @NotNull ImmutableList<Number> luminance$getValue() {
        switch ((Object)this) {
            case UniformValue.IntValue value -> {
                return ImmutableList.of(value.value());
            }
            case UniformValue.FloatValue value -> {
                return ImmutableList.of(value.value());
            }
            case UniformValue.Vec2fValue value -> {
                Vector2fc vec = value.value();
                return ImmutableList.of(vec.x(), vec.y());
            }
            case UniformValue.Vec3fValue value -> {
                Vector3fc vec = value.value();
                return ImmutableList.of(vec.x(), vec.y(), vec.z());
            }
            case UniformValue.Vec4fValue value -> {
                Vector4fc vec = value.value();
                return ImmutableList.of(vec.x(), vec.y(), vec.z(), vec.w());
            }
            case UniformValue.Vec3iValue value -> {
                Vector3ic vec = value.value();
                return ImmutableList.of(vec.x(), vec.y(), vec.z());
            }
            case UniformValue.Matrix4fValue value -> {
                Matrix4fc mat = value.value();
                ImmutableList.Builder<Number> builder = ImmutableList.builder();
                for (float f : mat.get(new float[16])) {
                    builder.add(f);
                }
                return builder.build();
            }
            default -> {
                return ImmutableList.of();
            }
        }
    }
}
