package com.mclegoman.luminance.mixin.client.shaders;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mclegoman.luminance.client.shaders.interfaces.pipeline.UniformValueInterface;
import com.mclegoman.luminance.client.shaders.uniforms.config.ConfigData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gl.UniformValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.function.Function;

@Mixin(UniformValue.class)
public interface UniformValueMixin {
    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/StringIdentifiable$EnumCodec;dispatch(Ljava/util/function/Function;Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;", remap = false))
    private static Codec<UniformValue> wrapCreatePrimitiveOverride(Codec<UniformValue> original) {
        return wrapCodec(original);
    }

    @Unique
    private static Codec<UniformValue> wrapCodec(Codec<UniformValue> original) {
        return RecordCodecBuilder.create(instance ->
                instance.group(
                        MapCodec.assumeMapUnsafe(original).forGetter(Function.identity()),
                        Codec.STRING.lenientOptionalFieldOf("name").forGetter(uniform -> ((UniformValueInterface)uniform).luminance$getName()),
                        Codec.STRING.sizeLimitedListOf(4).withAlternative(Codec.STRING.xmap(s -> List.of("auto#"+s) /* slightly janky way to communicate that it should be handled differently*/, List::getFirst)).lenientOptionalFieldOf("override").forGetter((uniform -> ((UniformValueInterface)uniform).luminance$getOverride())),
                        ConfigData.CODEC.listOf().lenientOptionalFieldOf("config").forGetter((uniform -> ((UniformValueInterface)uniform).luminance$getConfig()))
                ).apply(instance, (uniform, name, override, config) -> {
                    name.ifPresent(string -> ((UniformValueInterface)uniform).luminance$setName(string));
                    override.ifPresent(strings -> ((UniformValueInterface) uniform).luminance$setOverride(strings));
                    config.ifPresent(list -> ((UniformValueInterface) uniform).luminance$setConfig(list));
                    return uniform;
                }));
    }
}
