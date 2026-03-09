/*
    Luminance
    Contributor(s): Nettakrim (also indirectly: cputnam-a11y in the fabric discord for showing me how to do the codec wrapping magic)
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.client.shaders;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mclegoman.luminance.client.shaders.interfaces.pipeline.UniformValueInterface;
import com.mclegoman.luminance.client.shaders.uniforms.config.ConfigData;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gl.UniformValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Mixin({UniformValue.IntValue.class, UniformValue.FloatValue.class, UniformValue.Vec2fValue.class, UniformValue.Vec3fValue.class, UniformValue.Vec4fValue.class, UniformValue.Vec3iValue.class, UniformValue.Matrix4fValue.class})
public abstract class UniformValueMixin implements UniformValueInterface {
    @Shadow(remap = false) public abstract void addSize(Std140SizeCalculator calculator);

    // require = 0 means that the injection will be optional, meaning the same mixin file can be used for all uniform values
    @ModifyExpressionValue(require = 0, method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/codecs/PrimitiveCodec;xmap(Ljava/util/function/Function;Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;", remap = false))
    private static Codec<UniformValue> wrapCreatePrimitiveOverride(Codec<UniformValue> original) {
        return wrapCodec(original);
    }

    @ModifyExpressionValue(require = 0, method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/Codec;xmap(Ljava/util/function/Function;Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;", remap = false))
    private static Codec<UniformValue> wrapCreateVectorOverride(Codec<UniformValue> original) {
        return wrapCodec(original);
    }

    @Unique
    private static Codec<UniformValue> wrapCodec(Codec<UniformValue> original) {
        return RecordCodecBuilder.create(instance ->
                instance.group(
                        MapCodec.assumeMapUnsafe(original).forGetter(Function.identity()),
                        Codec.STRING.lenientOptionalFieldOf("name").forGetter(uniform -> ((UniformValueInterface)uniform).luminance$getName()),
                        Codec.STRING.sizeLimitedListOf(4).lenientOptionalFieldOf("override").forGetter((uniform -> ((UniformValueInterface)uniform).luminance$getOverride())),
                        ConfigData.CODEC.listOf().lenientOptionalFieldOf("config").forGetter((uniform -> ((UniformValueInterface)uniform).luminance$getConfig()))
                ).apply(instance, (uniform, name, override, config) -> {
                    name.ifPresent(string -> ((UniformValueInterface)uniform).luminance$setName(string));
                    override.ifPresent(strings -> ((UniformValueInterface) uniform).luminance$setOverride(strings));
                    config.ifPresent(list -> ((UniformValueInterface) uniform).luminance$setConfig(list));
                    return uniform;
                }));
    }

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
}
