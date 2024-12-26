/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders;

import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.util.ClosableFactory;

public record PersistentFramebufferFactory(int width, int height, boolean useDepth) implements ClosableFactory<Framebuffer> {
    //for some reason when the factory is not a SimpleFramebufferFactory screen sized frame buffers are allowed to exist across frames
    public Framebuffer create() {
        return new SimpleFramebuffer(this.width, this.height, this.useDepth);
    }

    public void close(Framebuffer framebuffer) {
        framebuffer.delete();
    }

    public int width() {
        return this.width;
    }

    public int height() {
        return this.height;
    }

    public boolean useDepth() {
        return this.useDepth;
    }
}

