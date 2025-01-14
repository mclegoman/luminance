/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.events;

import com.mclegoman.luminance.client.shaders.ShaderTime;
import com.mclegoman.luminance.client.shaders.uniforms.UniformConfig;
import com.mclegoman.luminance.client.shaders.uniforms.UniformValue;

public class Callables {
	public interface UniformCalculation {
		void call(ShaderTime shaderTime, UniformValue uniformValue);
	}

	public interface SingleUniformCalculation {
		float call(ShaderTime shaderTime);
	}

	public interface ConfigurableUniformCalculation {
		void call(UniformConfig config, ShaderTime shaderTime, UniformValue uniformValue);
	}
}