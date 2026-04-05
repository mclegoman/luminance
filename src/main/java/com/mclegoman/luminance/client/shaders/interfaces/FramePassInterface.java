/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.interfaces;

import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import net.minecraft.resources.Identifier;

public interface FramePassInterface {
    void luminance$setForceVisit(boolean to);
    boolean luminance$getForceVisit();
    int luminance$getId();

    static void createForcedPass(FrameGraphBuilder frameGraphBuilder, Identifier name, Runnable renderer) {
        FramePass framePass = frameGraphBuilder.addPass(name.toString());
        ((FramePassInterface)framePass).luminance$setForceVisit(true);
        framePass.executes(renderer);
    }
}
