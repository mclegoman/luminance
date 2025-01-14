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

public class RootUniform extends TreeUniform {
	@Nullable protected final Callables.UniformCalculation callable;
	@Nullable private final UniformValue min;
	@Nullable private final UniformValue max;

	protected UniformValue value;

	public RootUniform(String name, @Nullable Callables.UniformCalculation callable, int length, boolean useConfig, @Nullable UniformValue min, @Nullable UniformValue max) {
		super(name, useConfig);
		this.callable = callable;
		this.min = min;
		this.max = max;

		this.value = new UniformValue(length);
	}

	@Override
	public int getLength() {
		return value.values.size();
	}

	@Override
	public void beforeParentCacheUpdate(UniformConfig config, ShaderTime shaderTime) {

	}

	@Override
	public void calculateCache(UniformConfig config, ShaderTime shaderTime) {
		if (this.callable != null) {
			this.callable.call(config, shaderTime, value);
			clampToRange();
		}
	}

	@Override
	public UniformValue getCache(UniformConfig config, ShaderTime shaderTime) {
		return this.value;
	}

	protected void clampToRange() {
		getMin().ifPresent(value::max);
		getMax().ifPresent(value::min);
	}

	@Override
	public Optional<UniformValue> getMin() {
		return Optional.ofNullable(min);
	}

	@Override
	public Optional<UniformValue> getMax() {
		return Optional.ofNullable(max);
	}
}
