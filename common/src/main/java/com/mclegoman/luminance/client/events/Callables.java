/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.events;

import com.mclegoman.luminance.client.shaders.ShaderTime;
import com.mclegoman.luminance.client.shaders.uniforms.UniformValue;
import com.mclegoman.luminance.client.shaders.uniforms.config.UniformConfig;

public class Callables {
	@FunctionalInterface
	public interface UniformCalculation {
		void call(UniformConfig config, ShaderTime shaderTime, UniformValue uniformValue);
	}

	@FunctionalInterface
	public interface SingleUniformCalculation {
		float call(ShaderTime shaderTime);

		default UniformCalculation convert() {
			return (config, shaderTime, uniformValue) -> uniformValue.set(0, call(shaderTime));
		}
	}
}