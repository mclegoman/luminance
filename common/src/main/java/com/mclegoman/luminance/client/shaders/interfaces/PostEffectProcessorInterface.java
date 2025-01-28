/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.interfaces;

import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PostEffectProcessorInterface {
    @Nullable @Contract("null -> !null")
    List<PostEffectPass> luminance$getPasses(@Nullable Identifier identifier);

    void luminance$render(FrameGraphBuilder builder, int textureWidth, int textureHeight, PostEffectProcessor.FramebufferSet framebufferSet, @Nullable Identifier customPasses);

    void luminance$setCustomPasses(Map<Identifier, List<PostEffectPass>> customPasses);

    Set<Identifier> luminance$getCustomPassNames();

    boolean luminance$usesDepth();

    boolean luminance$usesPersistentBuffers();
}
