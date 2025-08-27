/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders;

import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

public class ShaderRegistryEntry {
	private final Identifier id;
	private final boolean disableUiRenderType;
	private final JsonObject custom;
	private ShaderRegistryEntry(Identifier id, boolean disableUiRenderType, JsonObject custom) {
		this.id = id;
		this.disableUiRenderType = disableUiRenderType;
		this.custom = custom;
	}
	public static Builder builder(Identifier id) {
		return new Builder(id);
	}
	public static class Builder {
		private final Identifier id;
		private boolean disableUiRenderType;
		private JsonObject custom;
		private Builder(Identifier id) {
			this.id = id;
			this.disableUiRenderType = false;
			this.custom = new JsonObject();
		}
		public Builder disableUiRenderType(boolean disableUiRenderType) {
			this.disableUiRenderType = disableUiRenderType;
			return this;
		}
		public Builder custom(JsonObject custom) {
			this.custom = custom;
			return this;
		}
		public ShaderRegistryEntry build() {
			return new ShaderRegistryEntry(this.id, this.disableUiRenderType, this.custom);
		}
	}
	public Identifier getID() {
		return this.id;
	}
	public Identifier getPostEffect(boolean full) {
		return Shaders.getPostShader(this.id, full);
	}
	public boolean getDisableUiRenderType() {
		return this.disableUiRenderType;
	}
	public JsonObject getCustom() {
		return this.custom;
	}
}
