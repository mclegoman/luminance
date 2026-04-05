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
	private final UniformValueSupplier min;
	private final UniformValueSupplier max;
	private final boolean rangeCanChange;
	private final UniformConfig defaultConfig;

	protected UniformVector value;

	public RootUniform(String name, Callables.UniformCalculation callable, int length, @Nullable UniformVector min, @Nullable UniformVector max, @Nullable UniformConfig defaultConfig) {
		super(name, defaultConfig != null);
		this.callable = callable;
		this.min = UniformValueSupplier.convert(min);
		this.max = UniformValueSupplier.convert(max);
		rangeCanChange = false;
		this.defaultConfig = defaultConfig == null ? EmptyConfig.INSTANCE : defaultConfig;

		this.value = new UniformVector(length);
	}

	public RootUniform(String name, Callables.UniformCalculation callable, int length, Callables.UniformCalculation min, Callables.UniformCalculation max, @Nullable UniformConfig defaultConfig) {
		super(name, defaultConfig != null);
		this.callable = callable;
		this.min = UniformValueSupplier.convert(min, length);
		this.max = UniformValueSupplier.convert(max, length);
		rangeCanChange = true;
		this.defaultConfig = defaultConfig == null ? EmptyConfig.INSTANCE : defaultConfig;

		this.value = new UniformVector(length);
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
		clampToRange(config, shaderTime);
	}

	@Override
	public UniformVector getCache(UniformConfig config, ShaderTime shaderTime) {
		return this.value;
	}

	protected void clampToRange(UniformConfig config, ShaderTime shaderTime) {
		getMin(config, shaderTime).ifPresent(value::max);
		getMax(config, shaderTime).ifPresent(value::min);
	}

	@Override
	public Optional<UniformVector> getMin(@Nullable UniformConfig config, @Nullable ShaderTime shaderTime) {
		if (rangeCanChange && (config == null || shaderTime == null)) {
			return Optional.empty();
		}
		return min.call(config, shaderTime);
	}

	@Override
	public Optional<UniformVector> getMax(@Nullable UniformConfig config, @Nullable ShaderTime shaderTime) {
		if (rangeCanChange && (config == null || shaderTime == null)) {
			return Optional.empty();
		}
		return max.call(config, shaderTime);
	}

	@Override
	public boolean rangeCanChange() {
		return rangeCanChange;
	}

	@Override
	public UniformConfig getDefaultConfig() {
		return defaultConfig;
	}

	@FunctionalInterface
	protected interface UniformValueSupplier {
		Optional<UniformVector> call(UniformConfig config, ShaderTime shaderTime);

		static UniformValueSupplier convert(Callables.UniformCalculation calculation, int length) {
			UniformVector uniformVector = new UniformVector(length);
			return (config, time) -> {
				calculation.call(config, time, uniformVector);
				return Optional.of(uniformVector);
			};
		}

		static UniformValueSupplier convert(@Nullable UniformVector value) {
			return (config, time) -> Optional.ofNullable(value);
		}
	}
}
