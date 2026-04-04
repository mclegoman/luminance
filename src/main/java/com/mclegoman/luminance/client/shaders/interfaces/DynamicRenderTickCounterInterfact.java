/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.interfaces;

public interface DynamicRenderTickCounterInterfact {
    float luminance$getRawTickProgress(); // allows ui background and panorama render types to use shader time
}
