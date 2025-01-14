/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.events;

import com.mclegoman.luminance.client.shaders.ShaderTime;
import com.mclegoman.luminance.client.shaders.uniforms.UniformValue;

public class Callables {
	public interface ShaderRender {
		// This should be updated to contain the shader's options JsonObject when we add it.
		void call(ShaderTime shaderTime, UniformValue uniformValue);
	}

	public interface SingleValueShaderRender {
		float call(ShaderTime shaderTime);
	}
}