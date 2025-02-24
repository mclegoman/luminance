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
	private final boolean translatable;
	private final boolean description;
	private final boolean disableGameRendertype;
	private final JsonObject custom;
	private ShaderRegistryEntry(Identifier id, boolean translatable, boolean description, boolean disableGameRendertype, JsonObject custom) {
		this.id = id;
		this.translatable = translatable;
		this.description = description;
		this.disableGameRendertype = disableGameRendertype;
		this.custom = custom;
	}
	public static Builder builder(Identifier id) {
		return new Builder(id);
	}
	public static class Builder {
		private final Identifier id;
		private boolean translatable;
		private boolean description;
		private boolean disableGameRendertype;
		private JsonObject custom;
		private Builder(Identifier id) {
			this.id = id;
			this.translatable = false;
			this.description = false;
			this.disableGameRendertype = false;
			this.custom = new JsonObject();
		}
		public Builder translatable(boolean translatable) {
			this.translatable = translatable;
			return this;
		}
		public Builder hasDescription(boolean description) {
			this.description = description;
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
		public ShaderRegistryEntry build() {
			return new ShaderRegistryEntry(this.id, this.translatable, this.description, this.disableGameRendertype, this.custom);
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
	public boolean getDescription() {
		return this.description;
	}
	public boolean getDisableGameRendertype() {
		return this.disableGameRendertype;
	}
	public JsonObject getCustom() {
		return this.custom;
	}
}
