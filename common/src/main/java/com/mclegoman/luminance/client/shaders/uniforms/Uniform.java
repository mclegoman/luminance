/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.uniforms;

import com.mclegoman.luminance.client.shaders.ShaderTime;
import com.mclegoman.luminance.client.shaders.uniforms.config.UniformConfig;

import java.util.Optional;

public interface Uniform {
	UniformValue get(UniformConfig config, ShaderTime shaderTime);
	int getLength();

	void tick();
	void update(ShaderTime shaderTime);

	Optional<UniformValue> getMin();
	Optional<UniformValue> getMax();

	UniformConfig getDefaultConfig();
}
