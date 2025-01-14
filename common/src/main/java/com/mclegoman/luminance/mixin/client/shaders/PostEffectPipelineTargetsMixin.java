/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.client.shaders;

import com.mclegoman.luminance.client.shaders.interfaces.PipelineTargetInterface;
import net.minecraft.client.gl.PostEffectPipeline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({PostEffectPipeline.ScreenSized.class,PostEffectPipeline.CustomSized.class})
public class PostEffectPipelineTargetsMixin implements PipelineTargetInterface {
    @Unique
    private boolean persistent;

    @Override
    public boolean luminance$getPersistent() {
        return persistent;
    }

    @Override
    public void luminance$setPersistent(boolean persistent) {
        this.persistent = persistent;
    }
}
