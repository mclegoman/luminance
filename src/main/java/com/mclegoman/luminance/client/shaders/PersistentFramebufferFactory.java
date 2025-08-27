/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders;

import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebufferFactory;
import net.minecraft.client.util.ClosableFactory;
import net.minecraft.util.Identifier;

public record PersistentFramebufferFactory(SimpleFramebufferFactory simpleFramebufferFactory, Object source, Identifier target, int clearColor) implements ClosableFactory<Framebuffer> {
    // equal objects in the game renderer pool get reused
    // this means screen-sized buffers will get messed with before they return to where they should be...
    // by changing the factory in PostEffectProcessorMixin with a new record that varies per persistent framebuffer
    // the game will always get the same framebuffer for the target, and nothing else will touch it
    public Framebuffer create() {
        Framebuffer framebuffer = simpleFramebufferFactory.create();
        framebuffer.setClearColor(((clearColor >> 16) & 255)/255f, ((clearColor >> 8) & 255)/255f, (clearColor & 255)/255f, (clearColor >>> 24)/255f);
        framebuffer.clear();
        return framebuffer;
    }

    public void close(Framebuffer framebuffer) {
        framebuffer.delete();
    }
}

