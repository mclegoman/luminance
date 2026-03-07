/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.interfaces;

import java.util.List;

public interface ShaderProgramInterface {
    List<Float> luminance$getCurrentUniformValues(String uniform);
}
