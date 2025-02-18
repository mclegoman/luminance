/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders.uniforms;

import com.mclegoman.luminance.client.events.Callables;
import com.mclegoman.luminance.client.shaders.ShaderTime;
import com.mclegoman.luminance.client.shaders.uniforms.config.DefaultableConfig;
import com.mclegoman.luminance.client.shaders.uniforms.config.EmptyConfig;
import com.mclegoman.luminance.client.shaders.uniforms.config.UniformConfig;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class RootUniform extends TreeUniform {
	protected final Callables.UniformCalculation callable;
	@Nullable private final UniformValue min;
	@Nullable private final UniformValue max;
	private final UniformConfig defaultConfig;

	protected UniformValue value;

	public RootUniform(String name, Callables.UniformCalculation callable, int length, @Nullable UniformValue min, @Nullable UniformValue max, @Nullable UniformConfig defaultConfig) {
		super(name, defaultConfig != null);
		this.callable = callable;
		this.min = min;
		this.max = max;
		this.defaultConfig = defaultConfig == null ? EmptyConfig.INSTANCE : defaultConfig;

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
		this.callable.call(new DefaultableConfig(config, getDefaultConfig()), shaderTime, value);
		clampToRange();
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

	@Override
	public UniformConfig getDefaultConfig() {
		return defaultConfig;
	}
}
