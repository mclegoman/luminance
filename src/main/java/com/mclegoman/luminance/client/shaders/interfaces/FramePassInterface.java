/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.interfaces;

public interface FramePassInterface {
    void luminance$setForceVisit(boolean to);
    boolean luminance$getForceVisit();
    int luminance$getId();
}
