/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.RenderTargetDescriptor;
import com.mojang.blaze3d.resource.ResourceDescriptor;
import net.minecraft.resources.Identifier;

public record PersistentFramebufferFactory(RenderTargetDescriptor simpleFramebufferFactory, Object source, Identifier target, int clearColor) implements ResourceDescriptor<RenderTarget> {
    // TODO: 1.21.5 implemented persistence in vanilla
    //  it does this by tracking a String name
    //  this is a little less flexible than my Object method
    //  but it will suffice for even soup's ridiculous use case, so this is no longer needed
    //  however! this is currently being used by LuminanceFramebufferSet!! so it cant be removed until that is changed to manage its framebuffers properly

    // equal objects in the game renderer pool get reused
    // this means screen-sized buffers will get messed with before they return to where they should be...
    // by changing the factory in PostEffectProcessorMixin with a new record that varies per persistent framebuffer
    // the game will always get the same framebuffer for the target, and nothing else will touch it
    public RenderTarget allocate() {
        return simpleFramebufferFactory.allocate();
    }

    @Override
    public void prepare(RenderTarget framebuffer) {
        RenderSystem.getDevice().createCommandEncoder().clearColorTexture(framebuffer.getColorTexture(), this.clearColor);
    }

    public void free(RenderTarget framebuffer) {
        framebuffer.destroyBuffers();
    }
}