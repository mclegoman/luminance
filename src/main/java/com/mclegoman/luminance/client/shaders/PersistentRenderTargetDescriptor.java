/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.RenderTargetDescriptor;
import com.mojang.blaze3d.resource.ResourceDescriptor;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record PersistentRenderTargetDescriptor(RenderTargetDescriptor renderTargetDescriptor, Object source, Identifier target) implements ResourceDescriptor<RenderTarget> {
    // TODO: 1.21.5 implemented persistence in vanilla
    //  it does this by tracking a String name
    //  this is a little less flexible than my Object method
    //  but it will suffice for even soup's ridiculous use case, so this is no longer needed
    //  however! this is currently being used by LuminanceTargetBundle!! so it cant be removed until that is changed to manage its render targets properly

    // equal objects in the game renderer pool get reused
    // this means screen-sized buffers will get messed with before they return to where they should be...
    // by changing the factory in PostEffectProcessorMixin with a new record that varies per persistent render target
    // the game will always get the same render target for the target, and nothing else will touch it
    public @NotNull RenderTarget allocate() {
        return renderTargetDescriptor.allocate();
    }

    @Override
    public void prepare(@NotNull RenderTarget renderTarget) {
        renderTargetDescriptor.prepare(renderTarget);
    }

    public void free(@NotNull RenderTarget renderTarget) {
        renderTargetDescriptor.free(renderTarget);
    }
}