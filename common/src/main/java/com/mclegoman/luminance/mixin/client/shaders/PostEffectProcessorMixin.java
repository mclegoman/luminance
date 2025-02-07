/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.client.shaders;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mclegoman.luminance.client.shaders.PersistentFramebufferFactory;
import com.mclegoman.luminance.client.shaders.interfaces.PostEffectProcessorInterface;
import com.mclegoman.luminance.client.shaders.interfaces.pipeline.PipelineInterface;
import com.mclegoman.luminance.client.shaders.interfaces.pipeline.PipelineTargetInterface;
import com.mclegoman.luminance.client.shaders.interfaces.PostEffectPassInterface;
import net.minecraft.client.gl.*;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.ClosableFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@SuppressWarnings("DataFlowIssue")
@Mixin(PostEffectProcessor.class)
public abstract class PostEffectProcessorMixin implements PostEffectProcessorInterface {
    @Shadow @Final private List<PostEffectPass> passes;

    @Shadow
    private static PostEffectPass parsePass(TextureManager textureManager, ShaderLoader shaderLoader, PostEffectPipeline.Pass pass) throws ShaderLoader.LoadException {
        return null;
    }

    @Shadow public abstract void render(FrameGraphBuilder builder, int textureWidth, int textureHeight, PostEffectProcessor.FramebufferSet framebufferSet);

    @Shadow @Final private Map<Identifier, PostEffectPipeline.Targets> internalTargets;
    @Unique private Map<Identifier, List<PostEffectPass>> luminance$customPasses;

    @Unique @Nullable private Identifier luminance$currentCustomPasses;

    @Unique private Object luminance$persistentBufferSource;

    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/FrameGraphBuilder;createResourceHandle(Ljava/lang/String;Lnet/minecraft/client/util/ClosableFactory;)Lnet/minecraft/client/util/Handle;"), method = "render(Lnet/minecraft/client/render/FrameGraphBuilder;IILnet/minecraft/client/gl/PostEffectProcessor$FramebufferSet;)V", index = 1)
    private ClosableFactory<Framebuffer> replaceFramebufferFactory(ClosableFactory<Framebuffer> factory, @Local Map.Entry<Identifier, PostEffectPipeline.Targets> target) {
        PostEffectPipeline.Targets targets = target.getValue();
        if (!((PipelineTargetInterface)(Object)targets).luminance$getPersistent()) {
            return factory;
        }

        SimpleFramebufferFactory simpleFramebufferFactory = (SimpleFramebufferFactory)factory;
        return new PersistentFramebufferFactory(simpleFramebufferFactory, luminance$persistentBufferSource, target.getKey());
    }

    @Inject(at = @At("RETURN"), method = "<init>")
    private void setForceVisit(List<PostEffectPass> passes, Map<Identifier, PostEffectPipeline.Targets> internalTargets, Set<Identifier> externalTargets, CallbackInfo ci) {
        passes.forEach((pass) -> luminance$trySetForceVisit(pass, internalTargets));
        luminance$persistentBufferSource = this;
    }

    @Unique private static void luminance$trySetForceVisit(PostEffectPass postEffectPass, Map<Identifier, PostEffectPipeline.Targets> internalTargets) {
        PostEffectPassInterface passInterface = (PostEffectPassInterface)postEffectPass;
        PostEffectPipeline.Targets targets = internalTargets.get(passInterface.luminance$getOutputTarget());

        if (targets == null) return;
        if (!((PipelineTargetInterface)(Object)targets).luminance$getPersistent()) return;

        passInterface.luminance$setForceVisit(true);
    }

    @ModifyExpressionValue(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/PostEffectPipeline;passes()Ljava/util/List;", ordinal = 0), method = "parseEffect")
    private static List<PostEffectPipeline.Pass> includeCustomPasses(List<PostEffectPipeline.Pass> original, PostEffectPipeline pipeline, TextureManager textureManager, ShaderLoader shaderLoader, Set<Identifier> availableExternalTargets) {
        Optional<Map<Identifier, List<PostEffectPipeline.Pass>>> customPasses = ((PipelineInterface)(Object)pipeline).luminance$getCustomPasses();
        if (customPasses.isEmpty()) {
            return original;
        }

        List<PostEffectPipeline.Pass> passes = new ArrayList<>(original.size());
        customPasses.get().forEach((identifier, list) -> passes.addAll(list));
        return passes;
    }

    @ModifyReturnValue(at = @At(value = "RETURN"), method = "parseEffect")
    private static PostEffectProcessor setTargets(PostEffectProcessor original, PostEffectPipeline pipeline, TextureManager textureManager, ShaderLoader shaderLoader, Set<Identifier> availableExternalTargets) {
        ((PipelineInterface)(Object)pipeline).luminance$getCustomPasses().ifPresentOrElse((map) -> {
            PostEffectProcessorInterface processor = (PostEffectProcessorInterface)original;

            Map<Identifier, List<PostEffectPass>> customPasses = new HashMap<>(map.size());

            for (Map.Entry<Identifier, List<PostEffectPipeline.Pass>> entry : map.entrySet()) {
                ImmutableList.Builder<PostEffectPass> builder = ImmutableList.builder();

                for (PostEffectPipeline.Pass pass : entry.getValue()) {
                    try {
                        builder.add(parsePass(textureManager, shaderLoader, pass));
                    } catch (ShaderLoader.LoadException e) {
                        throw new RuntimeException(e);
                    }
                }

                List<PostEffectPass> passes = builder.build();

                // for some reason custom passes aren't visited properly if we just let them into the frameGraphBuilder normally
                // so instead of only force-visiting the persistent ones, we force visit all of them
                // this would only cause a performance penalty if there are excessive passes in a custom pass that *should* be unvisited
                // but if someone's using a custom pass, i (Nettakrim) think they probably know what they're doing
                passes.forEach((pass) -> ((PostEffectPassInterface)pass).luminance$setForceVisit(true));
                //passes.forEach((pass) -> luminance$trySetForceVisit(pass, pipeline.internalTargets()));

                customPasses.put(entry.getKey(), passes);
            }

            processor.luminance$setCustomPasses(customPasses);
        }, () -> ((PostEffectProcessorInterface)original).luminance$setCustomPasses(Map.of()));
        return original;
    }

    @Override @Nullable @Contract("null -> !null")
    public List<PostEffectPass> luminance$getPasses(@Nullable Identifier identifier) {
        if (identifier == null) {
            return passes;
        }
        return luminance$customPasses.get(identifier);
    }

    @Override
    public void luminance$setCustomPasses(Map<Identifier, List<PostEffectPass>> customPasses) {
        luminance$customPasses = customPasses;
    }

    @Override
    public void luminance$render(FrameGraphBuilder builder, int textureWidth, int textureHeight, PostEffectProcessor.FramebufferSet framebufferSet, @Nullable Identifier customPasses) {
        if (customPasses == null || luminance$customPasses.containsKey(customPasses)) {
            luminance$currentCustomPasses = customPasses;
            render(builder, textureWidth, textureHeight, framebufferSet);
            luminance$currentCustomPasses = null;
        }
    }

    @ModifyReceiver(at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"), method = "render(Lnet/minecraft/client/render/FrameGraphBuilder;IILnet/minecraft/client/gl/PostEffectProcessor$FramebufferSet;)V")
    private List<PostEffectPass> replacePasses(List<PostEffectPass> instance) {
        if (luminance$currentCustomPasses == null) {
            return instance;
        }
        return luminance$customPasses.getOrDefault(luminance$currentCustomPasses, instance);
    }

    @Override
    public Set<Identifier> luminance$getCustomPassNames() {
        return luminance$customPasses.keySet();
    }

    @Override
    public boolean luminance$usesDepth() {
        if (luminance$passListUsesDepth(passes)) {
            return true;
        }
        for (List<PostEffectPass> customPasses : luminance$customPasses.values()) {
            if (luminance$passListUsesDepth(customPasses)) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private boolean luminance$passListUsesDepth(List<PostEffectPass> passes) {
        for (PostEffectPass pass : passes) {
            if (((PostEffectPassInterface)pass).luminance$usesDepth()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean luminance$usesPersistentBuffers() {
        for (PostEffectPipeline.Targets targets : internalTargets.values()) {
            if (((PipelineTargetInterface)(Object)targets).luminance$getPersistent()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void luminance$setPersistentBufferSource(@Nullable Object source) {
        luminance$persistentBufferSource = source == null ? this : source;
    }
}
