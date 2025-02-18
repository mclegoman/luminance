/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders;

import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.shaders.interfaces.PostEffectProcessorInterface;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.common.util.LogType;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.util.Identifier;

import java.util.concurrent.Callable;

public class Shader {
	private PostEffectProcessor postProcessor;
	private boolean useDepth;
	private Identifier shaderId;
	private Callable<RenderType> renderType;
	private Callable<Boolean> shouldRender;
	private ShaderRegistryEntry shaderData;
	public Shader(ShaderRegistryEntry shaderData, Callable<RenderType> renderType, Callable<Boolean> shouldRender) {
		reload(shaderData, renderType, shouldRender);
	}
	public Shader(ShaderRegistryEntry shaderData, Callable<RenderType> renderType) {
		this(shaderData, renderType, () -> true);
	}
	public PostEffectProcessor getPostProcessor() {
		return this.postProcessor;
	}
	public void setPostProcessor() {
		try {
			this.postProcessor = ClientData.minecraft.getShaderLoader().loadPostEffect(this.shaderId, DefaultFramebufferSet.STAGES);
			if (postProcessor != null && ((PostEffectProcessorInterface)this.postProcessor).luminance$usesDepth()) {
				setUseDepth(true);
			}
		} catch (Exception error) {
			com.mclegoman.luminance.common.data.Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to set post processor: {}", error));
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
	public Identifier getShaderId() {
		return this.shaderId;
	}
	private void setShaderId(Identifier id) {
		setUseDepth(false);
		closePostProcessor();
		this.shaderId = id;
	}
	public Callable<RenderType> getRenderType() {
		return this.renderType;
	}
	public void setRenderType(Callable<RenderType> renderType) {
		this.renderType = renderType;
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
		this.shaderData = shaderData;
		if (getShaderData() != null) setShaderId(getShaderData().getPostEffect(false));
	}
	public enum RenderType {
		WORLD(0),
		GAME(1);
		private final int id;
		RenderType(int id) {
			this.id = id;
		}
		public int getId() {
			return this.id;
		}
	}
	public void reload() {
		reload(shaderData, renderType, shouldRender);
	}
	public void reload(ShaderRegistryEntry shaderData, Callable<RenderType> renderType, Callable<Boolean> shouldRender) {
		closePostProcessor();
		setRenderType(renderType);
		setShouldRender(shouldRender);
		setShaderData(shaderData);
	}
	public record Data(Identifier id, Shader shader) {
	}
}
