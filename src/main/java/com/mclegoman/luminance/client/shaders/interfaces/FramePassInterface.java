/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.interfaces;

import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.FramePass;
import net.minecraft.util.Identifier;

public interface FramePassInterface {
    void luminance$setForceVisit(boolean to);
    boolean luminance$getForceVisit();
    int luminance$getId();

    static void createForcedPass(FrameGraphBuilder frameGraphBuilder, Identifier name, Runnable renderer) {
        FramePass framePass = frameGraphBuilder.createPass(name.toString());
        ((FramePassInterface)framePass).luminance$setForceVisit(true);
        framePass.setRenderer(renderer);
    }
}
