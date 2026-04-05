/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebufferFactory;
import net.minecraft.client.util.ClosableFactory;
import net.minecraft.util.Identifier;

public record PersistentFramebufferFactory(SimpleFramebufferFactory simpleFramebufferFactory, Object source, Identifier target, int clearColor) implements ClosableFactory<Framebuffer> {
    // TODO: 1.21.5 implemented persistence in vanilla
    //  it does this by tracking a String name
    //  this is a little less flexible than my Object method
    //  but it will suffice for even soup's ridiculous use case, so this is no longer needed
    //  however! this is currently being used by LuminanceFramebufferSet!! so it cant be removed until that is changed to manage its framebuffers properly

    // equal objects in the game renderer pool get reused
    // this means screen-sized buffers will get messed with before they return to where they should be...
    // by changing the factory in PostEffectProcessorMixin with a new record that varies per persistent framebuffer
    // the game will always get the same framebuffer for the target, and nothing else will touch it
    public Framebuffer create() {
        return simpleFramebufferFactory.create();
    }

    @Override
    public void prepare(Framebuffer framebuffer) {
        RenderSystem.getDevice().createCommandEncoder().clearColorTexture(framebuffer.getColorAttachment(), this.clearColor);
    }

    public void close(Framebuffer framebuffer) {
        framebuffer.delete();
    }
}