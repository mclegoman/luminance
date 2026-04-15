/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.client.shaders;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mclegoman.luminance.client.shaders.interfaces.PostPassInterface;
import com.mclegoman.luminance.client.shaders.interfaces.PostChainInterface;
import com.mclegoman.luminance.client.shaders.interfaces.pipeline.PostChainConfigInterface;
import com.mclegoman.luminance.client.shaders.interfaces.pipeline.PipelineTargetInterface;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.RenderTargetDescriptor;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostChainConfig;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.client.renderer.ShaderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.*;

@Mixin(PostChain.class)
public abstract class PostChainMixin implements PostChainInterface {
    @Shadow @Final private List<PostPass> passes;

    @Shadow
    private static PostPass createPass(TextureManager textureManager, PostChainConfig.Pass pass, Identifier id) throws ShaderManager.CompilationException {
        return null;
    }

    @Shadow public abstract void addToFrame(FrameGraphBuilder builder, int textureWidth, int textureHeight, PostChain.TargetBundle targetBundle);

    @Unique private Map<Identifier, List<PostPass>> luminance$customChains;
    @Unique @Nullable private Identifier luminance$currentChain;

    @Unique private Identifier luminance$persistentBufferSource;

    @ModifyExpressionValue(at = @At(value = "NEW", target = "(IIZI)Lcom/mojang/blaze3d/resource/RenderTargetDescriptor;"), method = "addToFrame(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;IILnet/minecraft/client/renderer/PostChain$TargetBundle;)V")
    private RenderTargetDescriptor replaceRenderTargetDescriptor(RenderTargetDescriptor original, @Local Map.Entry<Identifier, PostChainConfig.InternalTarget> target) {
        PostChainConfig.InternalTarget targets = target.getValue();
        PipelineTargetInterface.DynamicSize dynamicSize = ((PipelineTargetInterface)(Object)targets).luminance$getDynamicSize();

        if (dynamicSize != null) {
            return new RenderTargetDescriptor(dynamicSize.width().run(original.width(), original.height()), dynamicSize.height().run(original.width(), original.height()), original.useDepth(), original.clearColor());
        }
        return original;
    }

    @WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/PostChain;getOrCreatePersistentTarget(Lnet/minecraft/resources/Identifier;Lcom/mojang/blaze3d/resource/RenderTargetDescriptor;)Lcom/mojang/blaze3d/pipeline/RenderTarget;"), method = "addToFrame(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;IILnet/minecraft/client/renderer/PostChain$TargetBundle;)V")
    private RenderTarget replacePersistentSource(PostChain instance, Identifier identifier, RenderTargetDescriptor renderTargetDescriptor, Operation<RenderTarget> original) {
        return original.call(instance, luminance$persistentBufferSource != null ? luminance$persistentBufferSource : identifier, renderTargetDescriptor);
    }

    @ModifyExpressionValue(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/PostChainConfig;passes()Ljava/util/List;", ordinal = 0), method = "load")
    private static List<PostChainConfig.Pass> includeCustomChains(List<PostChainConfig.Pass> original, PostChainConfig pipeline, TextureManager textureManager) {
        Optional<Map<Identifier, List<PostChainConfig.Pass>>> customChains = ((PostChainConfigInterface)(Object)pipeline).luminance$getCustomChains();
        if (customChains.isEmpty()) {
            return original;
        }

        List<PostChainConfig.Pass> passes = new ArrayList<>(original.size());
        customChains.get().forEach((identifier, list) -> passes.addAll(list));
        return passes;
    }

    @ModifyReturnValue(at = @At(value = "RETURN"), method = "load")
    private static PostChain setCustomPassTargets(PostChain original, PostChainConfig pipeline, TextureManager textureManager, Set<Identifier> availableExternalTargets, Identifier id) {
        ((PostChainConfigInterface)(Object)pipeline).luminance$getCustomChains().ifPresentOrElse((map) -> {
            PostChainInterface processor = (PostChainInterface)original;

            Map<Identifier, List<PostPass>> customChains = new HashMap<>(map.size());

            for (Map.Entry<Identifier, List<PostChainConfig.Pass>> entry : map.entrySet()) {
                ImmutableList.Builder<PostPass> builder = ImmutableList.builder();

                for (PostChainConfig.Pass pass : entry.getValue()) {
                    try {
                        //noinspection DataFlowIssue
                        builder.add(createPass(textureManager, pass, id));
                    } catch (ShaderManager.CompilationException e) {
                        throw new RuntimeException(e);
                    }
                }

                List<PostPass> passes = builder.build();

                customChains.put(entry.getKey(), passes);
            }

            processor.luminance$setCustomChains(customChains);
        }, () -> ((PostChainInterface)original).luminance$setCustomChains(Map.of()));
        return original;
    }

    @Override @Nullable @Contract("null -> !null")
    public List<PostPass> luminance$getPasses(@Nullable Identifier identifier) {
        if (identifier == null) {
            return passes;
        }
        return luminance$customChains.get(identifier);
    }

    @Override
    public void luminance$setCustomChains(Map<Identifier, List<PostPass>> customChains) {
        luminance$customChains = customChains;
    }

    @Override
    public void luminance$render(FrameGraphBuilder builder, int textureWidth, int textureHeight, PostChain.TargetBundle targetBundle, @Nullable Identifier chain) {
        if (chain == null || luminance$customChains.containsKey(chain)) {
            luminance$currentChain = chain;
            addToFrame(builder, textureWidth, textureHeight, targetBundle);
            luminance$currentChain = null;
        }
    }

    @ModifyReceiver(at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"), method = "addToFrame(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;IILnet/minecraft/client/renderer/PostChain$TargetBundle;)V")
    private List<PostPass> replacePasses(List<PostPass> instance) {
        if (luminance$currentChain == null) {
            return instance;
        }
        return luminance$customChains.getOrDefault(luminance$currentChain, instance);
    }

    @Override
    public Set<Identifier> luminance$getCustomChainNames() {
        return luminance$customChains.keySet();
    }

    @Override
    public boolean luminance$usesDepth() {
        if (luminance$passListUsesDepth(passes)) {
            return true;
        }
        for (List<PostPass> customChain : luminance$customChains.values()) {
            if (luminance$passListUsesDepth(customChain)) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private boolean luminance$passListUsesDepth(List<PostPass> passes) {
        for (PostPass pass : passes) {
            if (((PostPassInterface)pass).luminance$usesDepth()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void luminance$setPersistentBufferSource(@Nullable Identifier source) {
        luminance$persistentBufferSource = source;
    }
}
