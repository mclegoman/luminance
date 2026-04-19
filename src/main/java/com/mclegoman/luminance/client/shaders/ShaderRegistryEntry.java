/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders;

import com.google.gson.JsonObject;
import net.minecraft.resources.Identifier;

public class ShaderRegistryEntry {
	private final Identifier id;
	private final boolean fallbackWhenOverUi;
	private final boolean fallbackWhenUnderUi;
	private final boolean photosensitive;
	private final JsonObject custom;

	private ShaderRegistryEntry(Identifier id, boolean fallbackWhenOverUi, boolean fallbackWhenUnderUi, boolean photosensitive, JsonObject custom) {
		this.id = id;
		this.fallbackWhenOverUi = fallbackWhenOverUi;
		this.fallbackWhenUnderUi = fallbackWhenUnderUi;
		this.photosensitive = photosensitive;
		this.custom = custom;
	}

	public static Builder builder(Identifier id) {
		return new Builder(id);
	}

	public static class Builder {
		private final Identifier id;
		private boolean fallbackWhenOverUi;
		private boolean fallbackWhenUnderUi;
		private boolean photosensitive;
		private JsonObject custom;

		private Builder(Identifier id) {
			this.id = id;
			this.fallbackWhenOverUi = false;
			this.fallbackWhenUnderUi = false;
			this.photosensitive = false;
			this.custom = new JsonObject();
		}

		public Builder fallbackWhenOverUi(boolean fallbackWhenOverUi) {
			this.fallbackWhenOverUi = fallbackWhenOverUi;
			return this;
		}

		public Builder fallbackWhenUnderUi(boolean fallbackWhenUnderUi) {
			this.fallbackWhenUnderUi = fallbackWhenUnderUi;
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
			return new ShaderRegistryEntry(this.id, this.fallbackWhenOverUi, this.fallbackWhenUnderUi, this.photosensitive, this.custom);
		}
	}

	public Identifier getID() {
		return this.id;
	}

	public Identifier getPostEffectIdentifier(boolean full) {
		return Shaders.getPostEffectIdentifier(this.id, full);
	}

	public boolean useFallbackWhenOverUi() {
		return this.fallbackWhenOverUi;
	}

	public boolean useFallbackWhenUnderUi() {
		return this.fallbackWhenUnderUi;
	}

	public boolean isPhotosensitive() {
		return this.photosensitive;
	}

	public JsonObject getCustom() {
		return this.custom;
	}
}
