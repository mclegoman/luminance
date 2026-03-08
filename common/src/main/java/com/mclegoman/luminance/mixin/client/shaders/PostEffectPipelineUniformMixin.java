/*
    Luminance
    Contributor(s): Nettakrim (also indirectly: cputnam-a11y in the fabric discord for showing me how to do the codec wrapping magic)
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.client.shaders;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mclegoman.luminance.client.shaders.interfaces.pipeline.PipelineUniformInterface;
import com.mclegoman.luminance.client.shaders.uniforms.config.ConfigData;
import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gl.PostEffectPipeline;
import net.minecraft.client.gl.UniformValue;
import net.minecraft.util.StringIdentifiable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Mixin(UniformValue.class)
public class PostEffectPipelineUniformMixin implements PipelineUniformInterface {
    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/StringIdentifiable$EnumCodec;dispatch(Ljava/util/function/Function;Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;", remap = false))
    private static Codec<UniformValue> wrapCreateOverride(Codec<UniformValue> original) {
        return RecordCodecBuilder.create(instance ->
            instance.group(
                MapCodec.assumeMapUnsafe(original).forGetter(Function.identity()),
                Codec.STRING.sizeLimitedListOf(4).lenientOptionalFieldOf("override").forGetter((uniform -> ((PipelineUniformInterface)uniform).luminance$getOverride())),
                ConfigData.CODEC.listOf().lenientOptionalFieldOf("config").forGetter((uniform -> ((PipelineUniformInterface)uniform).luminance$getConfig()))
            ).apply(instance, (uniform, override, config) -> {
                override.ifPresent(strings -> ((PipelineUniformInterface) uniform).luminance$setOverride(strings));
                config.ifPresent(list -> ((PipelineUniformInterface) uniform).luminance$setConfig(list));
                return uniform;
            }));
    }

    @Unique
    private List<String> luminance$override;

    @Unique
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
}
