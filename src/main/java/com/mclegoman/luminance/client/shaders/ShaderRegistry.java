/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders;

import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

public class ShaderRegistry {
	private final Identifier id;
	private final boolean translatable;
	private final boolean disableGameRendertype;
	private final JsonObject custom;
	private ShaderRegistry(Identifier id, boolean translatable, boolean disableGameRendertype, JsonObject custom) {
		this.id = id;
		this.translatable = translatable;
		this.disableGameRendertype = disableGameRendertype;
		this.custom = custom;
	}
	public static Builder builder(Identifier id) {
		return new Builder(id);
	}
	public static class Builder {
		private final Identifier id;
		private boolean translatable;
		private boolean disableGameRendertype;
		private JsonObject custom;
		private Builder(Identifier id) {
			this.id = id;
			this.translatable = false;
			this.disableGameRendertype = false;
			this.custom = new JsonObject();
		}
		public Builder translatable(boolean translatable) {
			this.translatable = translatable;
			return this;
		}
		public Builder disableGameRendertype(boolean disableGameRendertype) {
			this.disableGameRendertype = disableGameRendertype;
			return this;
		}
		public Builder custom(JsonObject custom) {
			this.custom = custom;
			return this;
		}
		public ShaderRegistry build() {
			return new ShaderRegistry(this.id, this.translatable, this.disableGameRendertype, this.custom);
		}
	}
	public Identifier getID() {
		return this.id;
	}
	public Identifier getPostEffect(boolean full) {
		return Shaders.getPostShader(this.id, full);
	}
	public boolean getTranslatable() {
		return this.translatable;
	}
	public boolean getDisableGameRendertype() {
		return this.disableGameRendertype;
	}
	public JsonObject getCustom() {
		return this.custom;
	}
}
