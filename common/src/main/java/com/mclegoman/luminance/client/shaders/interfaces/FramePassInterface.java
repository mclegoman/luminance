/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.interfaces;

import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.RenderPass;
import net.minecraft.util.Identifier;

public interface FramePassInterface {
    void luminance$setForceVisit(boolean to);
    boolean luminance$getForceVisit();
    int luminance$getId();

    static void createForcedPass(FrameGraphBuilder frameGraphBuilder, Identifier name, Runnable renderer) {
        RenderPass renderPass = frameGraphBuilder.createPass(name.toString());
        ((FramePassInterface)renderPass).luminance$setForceVisit(true);
        renderPass.setRenderer(renderer);
    }
}
