/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders;

import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.shaders.interfaces.PostChainInterface;
import com.mclegoman.luminance.common.util.LogType;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.resources.Identifier;

import java.util.concurrent.Callable;

public class Shader {
	private PostChain postProcessor;
	private boolean useDepth;
	private boolean useFabulous;
	private Identifier shaderId;
	private Callable<RenderLocations.RenderLocation<?>> RenderLocation;
	private Callable<Boolean> shouldRender;
	private ShaderRegistryEntry shaderData;

	public Shader(ShaderRegistryEntry shaderData, Callable<RenderLocations.RenderLocation<?>> RenderLocation, Callable<Boolean> shouldRender) {
		reload(shaderData, RenderLocation, shouldRender);
	}

	public Shader(ShaderRegistryEntry shaderData, Callable<RenderLocations.RenderLocation<?>> RenderLocation) {
		this(shaderData, RenderLocation, () -> true);
	}

	public PostChain getPostProcessor() {
		return this.postProcessor;
	}

	public void setPostProcessor() {
		try {
			this.postProcessor = ClientData.minecraft.getShaderManager().getPostChain(this.shaderId, LevelTargetBundle.SORTING_TARGETS);
			if (postProcessor != null) {
				if (((PostChainInterface)this.postProcessor).luminance$usesDepth()) {
					setUseDepth(true);
				}
				if (((PostChainInterface)this.postProcessor).luminance$usesFabulous()) {
					setUseFabulous(true);
				}
			}
		} catch (Exception error) {
			com.mclegoman.luminance.common.data.Data.getVersion().sendToLog(LogType.ERROR, "Failed to set post processor: {}", error);
			closePostProcessor();
		}
	}

	public void closePostProcessor() {
		if (this.postProcessor != null) this.postProcessor = null;
	}

	public boolean getUseDepth() {
		return this.useDepth;
	}

	public void setUseDepth(boolean useDepth) {
		this.useDepth = useDepth;
	}

	public boolean getUseFabulous() {
		return this.useFabulous;
	}

	public void setUseFabulous(boolean useFabulous) {
		this.useFabulous = useFabulous;
	}

	public Identifier getShaderId() {
		return this.shaderId;
	}

	private void setShaderId(Identifier id) {
		setUseDepth(false);
		setUseFabulous(false);
		closePostProcessor();
		this.shaderId = id;
	}

	public Callable<RenderLocations.RenderLocation<?>> getRenderLocation() {
		return this.RenderLocation;
	}

	public void setRenderLocation(Callable<RenderLocations.RenderLocation<?>> RenderLocation) {
		this.RenderLocation = RenderLocation;
	}

	public Boolean getShouldRender() {
		try {
			return this.shouldRender.call();
		} catch (Exception error) {
			return false;
		}
	}

	public void setShouldRender(Callable<Boolean> shouldRender) {
		this.shouldRender = shouldRender;
	}

	public ShaderRegistryEntry getShaderData() {
		return this.shaderData;
	}

	public void setShaderData(ShaderRegistryEntry shaderData) {
		setUseDepth(false);
		setUseFabulous(false);
		this.shaderData = shaderData;
		if (getShaderData() != null) setShaderId(getShaderData().getPostEffect(false));
	}

	public void reload() {
		reload(shaderData, RenderLocation, shouldRender);
	}

	public void reload(ShaderRegistryEntry shaderData, Callable<RenderLocations.RenderLocation<?>> renderLocation, Callable<Boolean> shouldRender) {
		closePostProcessor();
		setRenderLocation(renderLocation);
		setShouldRender(shouldRender);
		setShaderData(shaderData);
	}

	public record Data(Identifier id, Shader shader) {
	}
}
