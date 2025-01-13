/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.uniforms;

import com.mclegoman.luminance.client.events.Callables;
import com.mclegoman.luminance.client.shaders.ShaderTime;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class RootUniform extends TreeUniform<Float,Void> {
	protected final Callables.ShaderRender<Float> callable;
	@Nullable private final Float min;
	@Nullable private final Float max;

	protected float value;

	public RootUniform(String name, Callables.ShaderRender<Float> callable) {
		this(name, callable, null, null);
	}
	public RootUniform(String name, Callables.ShaderRender<Float> callable, @Nullable Float min, @Nullable Float max) {
		super(name);
		this.callable = callable;
		this.min = min;
		this.max = max;
	}

	@Override
	public Float get(UniformConfig config, ShaderTime shaderTime) {
		return this.value;
	}

	@Override
	public void updateValue(ShaderTime shaderTime) {
		value = call(shaderTime);
	}

	protected float call(ShaderTime shaderTime) {
		float value = this.callable.call(shaderTime);
		Optional<Float> min = getMin();
		if (min.isPresent() && value < min.get()) {
			return min.get();
		}
		Optional<Float> max = getMax();
		if (max.isPresent() && value > max.get()) {
			return max.get();
		}
		return value;
	}

	@Override
	public Optional<Float> getMin() {
		return Optional.ofNullable(min);
	}

	@Override
	public Optional<Float> getMax() {
		return Optional.ofNullable(max);
	}
}
