/*
    Luminance
    Contributor(s): dannytaylor, Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders;

import com.google.gson.JsonObject;
import com.mclegoman.luminance.client.events.Events;
import com.mclegoman.luminance.client.events.Runnables;
import com.mclegoman.luminance.client.shaders.interfaces.PostEffectProcessorInterface;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.LogType;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.Callable;

public class Shaders {
	protected static final Map<Identifier, List<ShaderRegistryEntry>> registries = new HashMap<>();

	public static void init() {
		Events.ClientResourceReloaders.register(Identifier.fromNamespaceAndPath(Data.getVersion().getID(), "shaders"), new ShaderReloader());
		ShaderStacks.init();
		Uniforms.init();
		Events.BeforeGameRender.register(Identifier.fromNamespaceAndPath(Data.getVersion().getID(), "update"), Uniforms::update);

		Events.AfterVanillaPostEffectRender.register(Identifier.fromNamespaceAndPath(Data.getVersion().getID(), "main"),
				(framebuffer, objectAllocator) -> RenderTypes.render(RenderTypes.WORLD, framebuffer, objectAllocator));
		Events.AfterUiRender.register(Identifier.fromNamespaceAndPath(Data.getVersion().getID(), "main"),
				(framebuffer, objectAllocator) -> RenderTypes.render(RenderTypes.UI, framebuffer, objectAllocator));
		Events.AfterUiBackgroundRender.register(Identifier.fromNamespaceAndPath(Data.getVersion().getID(), "main"),
				(framebuffer, objectAllocator) -> RenderTypes.render(RenderTypes.UI_BACKGROUND, framebuffer, objectAllocator));
		Events.AfterPanoramaRender.register(Identifier.fromNamespaceAndPath(Data.getVersion().getID(), "main"),
				(framebuffer, objectAllocator) -> RenderTypes.render(RenderTypes.PANORAMA, framebuffer, objectAllocator));
	}

	public static Identifier getMainRegistryId() {
		return Identifier.fromNamespaceAndPath(Data.getVersion().getID(), "main");
	}

	public static List<Identifier> getRegistries() {
		return registries.keySet().stream().toList();
	}

	public static List<Identifier> getShaderIds(Identifier registry) {
		List<Identifier> entries = new ArrayList<>();
		for (ShaderRegistryEntry entry : getRegistry(registry)) entries.add(entry.getID());
		return entries;
	}

	public static List<Identifier> getOrderedShaderIds(Identifier registry) {
		List<Identifier> shaderIds = new ArrayList<>(getShaderIds(registry));
		shaderIds.sort(Comparator.comparing((identifier) -> Shaders.getShaderName(registry, identifier).getString()));
		return shaderIds;
	}

	public static List<ShaderRegistryEntry> getRegistry() {
		return getRegistry(getMainRegistryId());
	}

	public static List<ShaderRegistryEntry> getRegistry(boolean disablePhotosensitive) {
		return getRegistry(getMainRegistryId(), disablePhotosensitive);
	}

	public static List<ShaderRegistryEntry> getRegistry(Identifier registry) {
		if (!registries.containsKey(registry)) registries.put(registry, new ArrayList<>());
		return registries.get(registry);
	}

	public static List<ShaderRegistryEntry> getRegistry(Identifier registry, boolean disablePhotosensitive) {
		List<ShaderRegistryEntry> shaders = new ArrayList<>(getRegistry(registry));
		if (disablePhotosensitive) shaders.removeIf(ShaderRegistryEntry::isPhotosensitive);
		return shaders;
	}

	private static void renderUsingFramebufferSet(Identifier id, Shader.Data shader, FrameGraphBuilder builder, int textureWidth, int textureHeight, PostChain.TargetBundle framebufferSet) {
		try {
			if (shader != null && shader.shader() != null && shader.shader().getShaderData() != null) {
				if (shader.shader().getShouldRender()) {
					if (shader.shader().getPostProcessor() == null) {
						try {
							shader.shader().setPostProcessor();
						} catch (Exception error) {
							Data.getVersion().sendToLog(LogType.ERROR, "Failed to set \"{}:{}:{}\" post processor: {}", id, shader.id(), shader.shader().getShaderData().getID(), error);
							Events.ShaderRender.Shaders.remove(id, shader.id());
						}
					}
					renderProcessorUsingFramebufferSet(shader.shader(), builder, textureWidth, textureHeight, framebufferSet, null);
				}
			}
		} catch (Exception error) {
			Data.getVersion().sendToLog(LogType.ERROR, "Failed to render \"{}:{}\" using framebuffer set, shader: {}: {}", id, shader.id(), shader.shader().getShaderData().getID(), error);
		}
	}

	public static void renderProcessorUsingFramebufferSet(Shader shader, FrameGraphBuilder builder, int textureWidth, int textureHeight, PostChain.TargetBundle framebufferSet, @Nullable Identifier customPasses) {
		try {
			if (shader.getPostProcessor() != null) {
				try {
					// the depth masking done in renderUsingAllocator is instead done for everything already before this method is called
					// this is because FrameGraphBuilder delays calls, so any rendersystem methods wont work with their intended timing
					((PostEffectProcessorInterface)shader.getPostProcessor()).luminance$render(builder, textureWidth, textureHeight, framebufferSet, customPasses);
				} catch (Exception error) {
					Data.getVersion().sendToLog(LogType.ERROR, "Failed to render processor: {}", error.getLocalizedMessage());
				}
			}
		} catch (Exception error) {
			Data.getVersion().sendToLog(LogType.ERROR, "Failed to render post effect processor: {}", error.getLocalizedMessage());
		}
	}

	public static void renderUsingAllocator(Identifier id, Shader.Data shader, RenderTarget framebuffer, GraphicsResourceAllocator objectAllocator) {
		try {
			if (shader != null && shader.shader() != null && shader.shader().getShaderData() != null) {
				if (shader.shader().getShouldRender()) {
					if (shader.shader().getPostProcessor() == null) {
						try {
							shader.shader().setPostProcessor();
						} catch (Exception error) {
							Data.getVersion().sendToLog(LogType.ERROR, "Failed to set \"{}:{}:{}\" post processor: {}", id, shader.id(), shader.shader().getShaderData().getID(), error);
							Events.ShaderRender.Shaders.remove(id, shader.id());
						}
					}
					renderShaderUsingAllocator(shader.shader(), framebuffer, objectAllocator, null);
				}
			}
		} catch (Exception error) {
			Data.getVersion().sendToLog(LogType.ERROR, "Failed to render \"{}:{}\" using allocator, shader: {}: {}", id, shader.id(), shader.shader().getShaderData().getID(), error);
		}
	}

	@Nullable
	public static ShaderRegistryEntry get(int shaderIndex) {
		return get(getMainRegistryId(), shaderIndex);
	}

	@Nullable
	public static ShaderRegistryEntry get(Identifier registry, int shaderIndex) {
		return isValidIndex(registry, shaderIndex) ? getRegistry(registry).get(shaderIndex) : null;
	}

	@Nullable
	public static ShaderRegistryEntry get(Identifier shaderId) {
		return get(getMainRegistryId(), shaderId);
	}

	@Nullable
	public static ShaderRegistryEntry get(Identifier registry, Identifier shaderId) {
		for (ShaderRegistryEntry entry : getRegistry(registry)) {
			if (entry.getID().equals(shaderId)) return entry;
		}
		return null;
	}

	public static Shader get(ShaderRegistryEntry shaderData, Callable<Identifier> renderType, Callable<Boolean> shouldRender) {
		return new Shader(shaderData, renderType, shouldRender);
	}

	public static Shader get(ShaderRegistryEntry shaderData, Callable<Identifier> renderType) {
		return new Shader(shaderData, renderType);
	}

	public static Identifier getPostShader(Identifier post_effect, boolean full) {
		return Identifier.fromNamespaceAndPath(post_effect.getNamespace(), ((full ? "post_effect/" : "") + post_effect.getPath() + (full ? ".json" : "")));
	}

	public static int getShaderIndex(Identifier shaderId) {
		return getShaderIndex(getMainRegistryId(), shaderId);
	}

	public static int getShaderIndex(Identifier registry, Identifier shaderId) {
		if (shaderId != null) {
			for (ShaderRegistryEntry data : getRegistry(registry)) {
				if (data.getID().equals(shaderId)) return getRegistry(registry).indexOf(data);
			}
		}
		return -1;
	}

	public static JsonObject getCustom(int shaderIndex, String namespace) {
		return getCustom(getMainRegistryId(), shaderIndex, namespace);
	}

	public static JsonObject getCustom(Identifier registry, int shaderIndex, String namespace) {
		ShaderRegistryEntry shader = get(registry, shaderIndex);
		if (shader != null) {
			JsonObject customData = shader.getCustom();
			if (customData != null) {
				if (customData.has(namespace)) {
					return GsonHelper.getAsJsonObject(customData, namespace);
				}
			}
		}
		return null;
	}

	public static Component getShaderName(int shaderIndex, boolean shouldShowNamespace) {
		return getShaderName(getMainRegistryId(), shaderIndex, shouldShowNamespace);
	}

	public static Component getShaderName(Identifier registry, int shaderIndex, boolean shouldShowNamespace) {
		ShaderRegistryEntry shader = get(registry, shaderIndex);
		if (shader != null) return Translation.getShaderText(shader.getID(), shouldShowNamespace);
		return Translation.getErrorTranslation(Data.getVersion().getID());
	}

	public static Component getShaderName(int shaderIndex) {
		return getShaderName(getMainRegistryId(), shaderIndex);
	}

	public static Component getShaderName(Identifier registry, int shaderIndex) {
		return getShaderName(registry, shaderIndex, true);
	}

	public static Component getShaderName(Identifier registryId, Identifier shaderId) {
		return getShaderName(registryId, getShaderIndex(registryId, shaderId));
	}

	public static Component getShaderDescription(int shaderIndex, boolean shouldShowNamespace) {
		return getShaderDescription(getMainRegistryId(), shaderIndex, shouldShowNamespace);
	}

	public static Component getShaderDescription(Identifier registry, int shaderIndex, boolean shouldShowNamespace) {
		ShaderRegistryEntry shader = get(registry, shaderIndex);
		if (shader != null) return Translation.getShaderText(shader.getID(), shouldShowNamespace, true, new ChatFormatting[]{});
		return Translation.getErrorTranslation(Data.getVersion().getID());
	}

	public static Component getShaderDescription(int shaderIndex) {
		return getShaderDescription(getMainRegistryId(), shaderIndex);
	}

	public static Component getShaderDescription(Identifier registry, int shaderIndex) {
		return getShaderDescription(registry, shaderIndex, true);
	}

	public static Component getShaderDescription(Identifier registryId, Identifier shaderId) {
		return getShaderDescription(registryId, getShaderIndex(registryId, shaderId));
	}

	public static Optional<ShaderRegistryEntry> guessPostShader(@NotNull String id) {
		return guessPostShader(getMainRegistryId(), id);
	}

	public static Optional<ShaderRegistryEntry> guessPostShader(@NotNull Identifier registry, @NotNull String id) {
		// If the shader registry contains at least one shader with the name, the first detected instance will be used.
		id = id.toLowerCase(Locale.ROOT);

		if (id.contains(":")) {
			Identifier identifier = Identifier.tryParse(id);
			if (identifier == null) {
				return Optional.empty();
			}

			ShaderRegistryEntry entry = get(registry, identifier);
			if (entry != null) {
				return Optional.of(entry);
			}

			id = identifier.getPath();
		}

		for (ShaderRegistryEntry entry : getRegistry(registry)) {
			if (entry.getID().getPath().equals(id)) {
				return Optional.of(entry);
			}
		}

		return Optional.empty();
	}

	// This is identical to the deprecated `PostEffectProcessor.render(framebuffer, objectAllocator);` function.
	public static void renderShaderUsingAllocator(Shader shader, RenderTarget framebuffer, GraphicsResourceAllocator objectAllocator, @Nullable Identifier customPasses) {
		try {
			if (shader.getPostProcessor() != null) {
				Runnables.WorldRender.fromGameRender((builder, width, height, set) -> ((PostEffectProcessorInterface)shader.getPostProcessor()).luminance$render(builder, width, height, set, customPasses), framebuffer, objectAllocator);
			}
		} catch (Exception error) {
			Data.getVersion().sendToLog(LogType.ERROR, "Failed to render processor: {}", error.getLocalizedMessage());
		}
	}

	public static int getShaderAmount() {
		return getShaderAmount(getMainRegistryId());
	}

	public static int getShaderAmount(Identifier registry) {
		return getRegistry(registry).size();
	}

	public static boolean isValidIndex(int index) {
		return isValidIndex(getMainRegistryId(), index);
	}

	public static boolean isValidIndex(Identifier registry, int index) {
		return index <= getShaderAmount(registry) && index >= 0;
	}
}
