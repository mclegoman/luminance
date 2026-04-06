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
import com.llamalad7.mixinextras.sugar.Local;
import com.mclegoman.luminance.client.shaders.interfaces.PostPassInterface;
import com.mclegoman.luminance.client.shaders.interfaces.PostChainInterface;
import com.mclegoman.luminance.client.shaders.interfaces.pipeline.PostChainConfigInterface;
import com.mclegoman.luminance.client.shaders.interfaces.pipeline.PipelineTargetInterface;
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

    @Unique private Map<Identifier, List<PostPass>> luminance$customPasses;
    @Unique @Nullable private Identifier luminance$currentCustomPasses;

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

    @ModifyExpressionValue(at = @At(value = "INVOKE", target = "Ljava/util/Map$Entry;getKey()Ljava/lang/Object;"), method = "addToFrame(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;IILnet/minecraft/client/renderer/PostChain$TargetBundle;)V")
    private <K> K replacePersistentSource(K original, @Local Map.Entry<Identifier, PostChainConfig.InternalTarget> target) {
        if (target.getValue().persistent() && luminance$persistentBufferSource != null) {
            //noinspection unchecked
            return (K) luminance$persistentBufferSource;
        }
        return original;
    }

    // TODO: setting force visit for persistent buffers probably isnt needed anymore

//    @Inject(at = @At("RETURN"), method = "<init>")
//    private void setForceVisit(List<PostEffectPass> passes, Map<Identifier, PostEffectPipeline.Targets> internalTargets, Set<Identifier> externalTargets, ProjectionMatrix2 matrix, CallbackInfo ci) {
//        passes.forEach((pass) -> luminance$trySetForceVisit(pass, internalTargets));
//        luminance$persistentBufferSource = this.toString();
//    }

//    @Unique private static void luminance$trySetForceVisit(PostEffectPass postEffectPass, Map<Identifier, PostEffectPipeline.Targets> internalTargets) {
//        PostEffectPassInterface passInterface = (PostEffectPassInterface)postEffectPass;
//        PostEffectPipeline.Targets targets = internalTargets.get(passInterface.luminance$getOutputTarget());
//
//        if (targets == null) return;
//        if (!targets.persistent()) return;
//
//        passInterface.luminance$setForceVisit(true);
//    }

    @ModifyExpressionValue(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/PostChainConfig;passes()Ljava/util/List;", ordinal = 0), method = "load")
    private static List<PostChainConfig.Pass> includeCustomPasses(List<PostChainConfig.Pass> original, PostChainConfig pipeline, TextureManager textureManager) {
        Optional<Map<Identifier, List<PostChainConfig.Pass>>> customPasses = ((PostChainConfigInterface)(Object)pipeline).luminance$getCustomPasses();
        if (customPasses.isEmpty()) {
            return original;
        }

        List<PostChainConfig.Pass> passes = new ArrayList<>(original.size());
        customPasses.get().forEach((identifier, list) -> passes.addAll(list));
        return passes;
    }

    @ModifyReturnValue(at = @At(value = "RETURN"), method = "load")
    private static PostChain setCustomPassTargets(PostChain original, PostChainConfig pipeline, TextureManager textureManager, Set<Identifier> availableExternalTargets, Identifier id) {
        ((PostChainConfigInterface)(Object)pipeline).luminance$getCustomPasses().ifPresentOrElse((map) -> {
            PostChainInterface processor = (PostChainInterface)original;

            Map<Identifier, List<PostPass>> customPasses = new HashMap<>(map.size());

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

                // TODO: this context for force visiting is different to the force visiting for persistent targets, so it may be needed!
                //  it will be obvious, since custom passes just wont really work if they arent visited

                // for some reason custom passes aren't visited properly if we just let them into the frameGraphBuilder normally
                // so instead of only force-visiting the persistent ones, we force visit all of them
                // this would only cause a performance penalty if there are excessive passes in a custom pass that *should* be unvisited
                // but if someone's using a custom pass, i (Nettakrim) think they probably know what they're doing
                //passes.forEach((pass) -> ((PostEffectPassInterface)pass).luminance$setForceVisit(true));
                //passes.forEach((pass) -> luminance$trySetForceVisit(pass, pipeline.internalTargets()));

                customPasses.put(entry.getKey(), passes);
            }

            processor.luminance$setCustomPasses(customPasses);
        }, () -> ((PostChainInterface)original).luminance$setCustomPasses(Map.of()));
        return original;
    }

    @Override @Nullable @Contract("null -> !null")
    public List<PostPass> luminance$getPasses(@Nullable Identifier identifier) {
        if (identifier == null) {
            return passes;
        }
        return luminance$customPasses.get(identifier);
    }

    @Override
    public void luminance$setCustomPasses(Map<Identifier, List<PostPass>> customPasses) {
        luminance$customPasses = customPasses;
    }

    @Override
    public void luminance$render(FrameGraphBuilder builder, int textureWidth, int textureHeight, PostChain.TargetBundle targetBundle, @Nullable Identifier customPasses) {
        if (customPasses == null || luminance$customPasses.containsKey(customPasses)) {
            luminance$currentCustomPasses = customPasses;
            addToFrame(builder, textureWidth, textureHeight, targetBundle);
            luminance$currentCustomPasses = null;
        }
    }

    @ModifyReceiver(at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"), method = "addToFrame(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;IILnet/minecraft/client/renderer/PostChain$TargetBundle;)V")
    private List<PostPass> replacePasses(List<PostPass> instance) {
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
        for (List<PostPass> customPasses : luminance$customPasses.values()) {
            if (luminance$passListUsesDepth(customPasses)) {
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
