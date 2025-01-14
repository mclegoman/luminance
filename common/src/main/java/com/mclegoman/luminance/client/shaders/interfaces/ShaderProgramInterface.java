/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.interfaces;

import java.util.List;

public interface ShaderProgramInterface {
    List<String> luminance$getUniformNames();

    List<Float> luminance$getCurrentUniformValues(String uniform);
}
