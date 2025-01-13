/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.uniforms;

import com.mclegoman.luminance.client.shaders.ShaderTime;

import java.util.Optional;

public interface Uniform {
	float get(UniformConfig config, ShaderTime shaderTime);

	void tick();
	void update(ShaderTime shaderTime);

	Optional<Float> getMin();
	Optional<Float> getMax();
}
