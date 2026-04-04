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
	private final boolean disableOverUi;
	private final boolean disableUnderUi;
	private final boolean photosensitive;
	private final JsonObject custom;

	private ShaderRegistryEntry(Identifier id, boolean disableOverUi, boolean disableUnderUi, boolean photosensitive, JsonObject custom) {
		this.id = id;
		this.disableOverUi = disableOverUi;
		this.disableUnderUi = disableUnderUi;
		this.photosensitive = photosensitive;
		this.custom = custom;
	}

	public static Builder builder(Identifier id) {
		return new Builder(id);
	}

	public static class Builder {
		private final Identifier id;
		private boolean disableOverUi;
		private boolean disableUnderUi;
		private boolean photosensitive;
		private JsonObject custom;

		private Builder(Identifier id) {
			this.id = id;
			this.disableOverUi = false;
			this.disableUnderUi = false;
			this.photosensitive = false;
			this.custom = new JsonObject();
		}

		public Builder disableOverUi(boolean disableOverUi) {
			this.disableOverUi = disableOverUi;
			return this;
		}

		public Builder disableUnderUi(boolean disableUnderUi) {
			this.disableUnderUi = disableUnderUi;
			return this;
		}

		public Builder photosensitive(boolean photosensitive) {
			this.photosensitive = photosensitive;
			return this;
		}

		public Builder custom(JsonObject custom) {
			this.custom = custom;
			return this;
		}

		public ShaderRegistryEntry build() {
			return new ShaderRegistryEntry(this.id, this.disableOverUi, this.disableUnderUi, this.photosensitive, this.custom);
		}
	}

	public Identifier getID() {
		return this.id;
	}

	public Identifier getPostEffect(boolean full) {
		return Shaders.getPostShader(this.id, full);
	}

	public boolean shouldDisableOverUi() {
		return this.disableOverUi;
	}

	public boolean shouldDisableUnderUi() {
		return this.disableUnderUi;
	}

	public boolean isPhotosensitive() {
		return this.photosensitive;
	}

	public JsonObject getCustom() {
		return this.custom;
	}
}
