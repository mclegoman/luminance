/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.client.shaders;

import com.mclegoman.luminance.client.shaders.interfaces.pipeline.PipelineTargetInterface;
import net.minecraft.client.renderer.PostChainConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PostChainConfig.InternalTarget.class)
public class PostEffectPipelineTargetsMixin implements PipelineTargetInterface {
    @Unique
    private DynamicSize luminance$dynamicSize;

    @Override
    public DynamicSize luminance$getDynamicSize() {
        return luminance$dynamicSize;
    }

    @Override
    public void luminance$setDynamicSize(DynamicSize dynamicSize) {
        this.luminance$dynamicSize = dynamicSize;
    }
}
