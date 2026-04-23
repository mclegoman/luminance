/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders;

import com.mclegoman.luminance.client.shaders.interfaces.PostChainInterface;
import com.mclegoman.luminance.common.util.LogType;
import net.minecraft.resources.Identifier;

import java.util.concurrent.Callable;

public class Shader {
	private PostChainInterface postChain;
	private boolean useDepth;
	private boolean useImprovedTransparency;
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

	public PostChainInterface getPostChain() {
		return this.postChain;
	}

	public void loadPostChain() {
		try {
			this.postChain = Shaders.getPostChain(shaderId);
			if (postChain != null) {
				if (this.postChain.luminance$usesDepth()) setUseDepth(true);
				if (this.postChain.luminance$usesImprovedTransparency()) setUseImprovedTransparency(true);
			}
		} catch (Exception error) {
			com.mclegoman.luminance.common.data.Data.getVersion().sendToLog(LogType.ERROR, "Failed to set post processor", error);
			clearPostChain();
		}
	}

	public void clearPostChain() {
		if (this.postChain != null) this.postChain = null;
	}

	public boolean getUseDepth() {
		return this.useDepth;
	}

	public void setUseDepth(boolean useDepth) {
		this.useDepth = useDepth;
	}

	public boolean getUseImprovedTransparency() {
		return this.useImprovedTransparency;
	}

	public void setUseImprovedTransparency(boolean useImprovedTransparency) {
		this.useImprovedTransparency = useImprovedTransparency;
	}

	public Identifier getShaderId() {
		return this.shaderId;
	}

	private void setShaderId(Identifier id) {
		setUseDepth(false);
		setUseImprovedTransparency(false);
		clearPostChain();
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
		setUseImprovedTransparency(false);
		this.shaderData = shaderData;
		if (getShaderData() != null) setShaderId(getShaderData().getPostEffectIdentifier(false));
	}

	public void reload() {
		reload(shaderData, RenderLocation, shouldRender);
	}

	public void reload(ShaderRegistryEntry shaderData, Callable<RenderLocations.RenderLocation<?>> renderLocation, Callable<Boolean> shouldRender) {
		clearPostChain();
		setRenderLocation(renderLocation);
		setShouldRender(shouldRender);
		setShaderData(shaderData);
	}

	public record Data(Identifier id, Shader shader) {
	}
}
