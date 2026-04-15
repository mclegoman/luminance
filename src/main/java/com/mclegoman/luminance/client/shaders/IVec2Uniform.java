package com.mclegoman.luminance.client.shaders;

import com.google.common.collect.ImmutableList;
import com.mclegoman.luminance.client.shaders.interfaces.pipeline.UniformValueInterface;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.serialization.Codec;
import net.minecraft.client.renderer.UniformValue;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class IVec2Uniform implements UniformValue, UniformValueInterface {
    public static final Codec<IVec2Uniform> CODEC = Codec.INT.listOf().comapFlatMap((listA) -> Util.fixedSize(listA, 2).map((listB) -> (Vector2ic)new Vector2i(listB.get(0), listB.get(1))), (vector2ic) -> List.of(vector2ic.x(), vector2ic.y())).xmap(IVec2Uniform::new, IVec2Uniform::value);

    public Vector2ic value;

    public IVec2Uniform(Vector2ic value) {
        this.value = value;
    }

    public void writeTo(Std140Builder std140Builder) {
        std140Builder.putIVec2(value());
    }

    public void addSize(Std140SizeCalculator std140SizeCalculator) {
        std140SizeCalculator.putIVec4();
    }

    public @NotNull Type type() {
        return Type.valueOf("LUMINANCE_IVEC2");
    }

    public Vector2ic value() {
        return this.value;
    }

    private String name;

    @Override
    public Optional<String> luminance$getName() {
        return Optional.ofNullable(name);
    }

    @Override
    public void luminance$setName(String name) {
        this.name = name;
    }

    private List<String> luminance$override;

    @Override
    public Optional<List<String>> luminance$getOverride() {
        return Optional.ofNullable(luminance$override);
    }

    @Override
    public void luminance$setOverride(List<String> override) {
        this.luminance$override = override;
    }

    private Map<String, List<Object>> luminance$config;

    @Override
    public Optional<Map<String, List<Object>>> luminance$getConfig() {
        return Optional.ofNullable(luminance$config);
    }

    @Override
    public void luminance$setConfig(Map<String,List<Object>> config) {
        this.luminance$config = config;
    }

    @Override
    public int luminance$getLength() {
        return 2;
    }

    @Override
    public ImmutableList<Number> luminance$getValue() {
        return ImmutableList.of(value.x(), value.y());
    }
}
