package com.mclegoman.luminance.client.shaders;

import com.google.common.collect.ImmutableList;
import com.mclegoman.luminance.client.shaders.interfaces.pipeline.UniformValueInterface;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.serialization.Codec;
import net.minecraft.client.renderer.UniformValue;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4i;
import org.joml.Vector4ic;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class IVec4Uniform implements UniformValue, UniformValueInterface {
    public static final Codec<IVec4Uniform> CODEC = Codec.INT.listOf().comapFlatMap((listA) -> Util.fixedSize(listA, 4).map((listB) -> (Vector4ic)new Vector4i(listB.get(0), listB.get(1), listB.get(2), listB.get(3))), (vector4ic) -> List.of(vector4ic.x(), vector4ic.y(), vector4ic.z(), vector4ic.w())).xmap(IVec4Uniform::new, IVec4Uniform::value);

    public Vector4ic value;

    public IVec4Uniform(Vector4ic value) {
        this.value = value;
    }

    public void writeTo(Std140Builder std140Builder) {
        std140Builder.putIVec4(value());
    }

    public void addSize(Std140SizeCalculator std140SizeCalculator) {
        std140SizeCalculator.putIVec4();
    }

    public @NotNull Type type() {
        return Type.valueOf("LUMINANCE_IVEC4");
    }

    public Vector4ic value() {
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
        return 4;
    }

    @Override
    public ImmutableList<Number> luminance$getValue() {
        return ImmutableList.of(value.x(), value.y(), value.z(), value.w());
    }
}
