/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.events;

import com.mclegoman.luminance.client.shaders.ShaderTime;

public class Callables {
	public interface ShaderRender<V> {
		// This should be updated to contain the shader's options JsonObject when we add it.
		V call(ShaderTime shaderTime);
	}
}