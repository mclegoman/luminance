/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.uniforms;

import com.mclegoman.luminance.client.events.Callables;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class RootUniform extends TreeUniform {
	protected final Callables.ShaderRender<Float> callable;
	@Nullable private final Float min;
	@Nullable private final Float max;

	protected float value;

	public RootUniform(Callables.ShaderRender<Float> callable, String name) {
		this(callable, null, null, name);
	}
	public RootUniform(Callables.ShaderRender<Float> callable, @Nullable Float min, @Nullable Float max, String name) {
		super(name);
		this.callable = callable;
		this.min = min;
		this.max = max;
	}

	@Override
	public float get() {
		return this.value;
	}

	@Override
	public void updateValue(float tickDelta, float deltaTime) {
		value = call(tickDelta, deltaTime);
	}

	protected float call(float tickDelta, float deltaTime) {
		float value = this.callable.call(tickDelta, deltaTime);
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
