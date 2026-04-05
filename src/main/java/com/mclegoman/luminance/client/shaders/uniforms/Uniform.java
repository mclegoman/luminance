/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.uniforms;

import com.mclegoman.luminance.client.shaders.ShaderTime;
import com.mclegoman.luminance.client.shaders.uniforms.config.UniformConfig;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface Uniform {
	UniformVector get(UniformConfig config, ShaderTime shaderTime);
	int getLength();

	void tick();
	void update(ShaderTime shaderTime);

	Optional<UniformVector> getMin(@Nullable UniformConfig config, @Nullable ShaderTime shaderTime);
	Optional<UniformVector> getMax(@Nullable UniformConfig config, @Nullable ShaderTime shaderTime);
	boolean rangeCanChange();

	UniformConfig getDefaultConfig();
}
