/*
    Luminance
    Contributor(s): dannytaylor, Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.debug.DebugEntryDebugShader;
import com.mclegoman.luminance.client.events.Events;
import com.mclegoman.luminance.client.events.ProfiledDebugEntries;
import com.mclegoman.luminance.client.events.Runnables;
import com.mclegoman.luminance.client.shaders.interfaces.PostChainInterface;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.LogType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import net.minecraft.client.gui.components.debug.DebugScreenEntryStatus;
import net.minecraft.client.gui.components.debug.DebugScreenProfile;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.PostPass;
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
		Events.ClientResourceReloaders.register(Data.idOf("shaders"), new ShaderReloader());
		ShaderStacks.init();
		Uniforms.init();
		Events.BeforeGameRender.register(Data.idOf("update"), Uniforms::update);

		Events.AfterFabulousRender.register(Data.idOf("main"),
				(data) -> RenderLocations.render(RenderLocations.LEVEL, data));
		Events.AfterVanillaPostEffectRender.register(Data.idOf("main"),
				(data) -> RenderLocations.render(RenderLocations.GAME, data));
		Events.AfterUiRender.register(Data.idOf("main"),
				(data) -> RenderLocations.render(RenderLocations.UI, data));
		Events.AfterUiBackgroundRender.register(Data.idOf("main"),
				(data) -> RenderLocations.render(RenderLocations.UI_BACKGROUND, data));
		Events.AfterPanoramaRender.register(Data.idOf("main"),
				(data) -> RenderLocations.render(RenderLocations.PANORAMA, data));

		ProfiledDebugEntries.register(Data.idOf("debug_shader"), new DebugEntryDebugShader(), DebugScreenProfile.DEFAULT, DebugScreenEntryStatus.IN_OVERLAY);
	}

	public static Identifier getMainRegistryId() {
		return Data.idOf("main");
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

	public static void renderFromLevelData(Identifier id, Shader.Data shader, Runnables.LevelRender.Data data) {
		try {
			if (shader != null && shader.shader() != null && shader.shader().getShaderData() != null) {
				if (shader.shader().getShouldRender()) {
					if (shader.shader().getPostChain() == null) {
						try {
							shader.shader().loadPostChain();
						} catch (Exception error) {
							Data.getVersion().sendToLog(LogType.ERROR, "Failed to set \"{}:{}:{}\" post processor: {}", id, shader.id(), shader.shader().getShaderData().getID(), error);
							Events.ShaderRender.Shaders.remove(id, shader.id());
						}
					}
					renderShaderFromLevelData(shader.shader(), data, null);
				}
			}
		} catch (Exception error) {
			Data.getVersion().sendToLog(LogType.ERROR, "Failed to render \"{}:{}\" using target bundle, shader: {}: {}", id, shader.id(), shader.shader().getShaderData().getID(), error);
		}
	}

	public static void renderShaderFromLevelData(Shader shader, Runnables.LevelRender.Data data, @Nullable Identifier chain) {
		try {
			if (shader.getPostChain() != null) {
				try {
					// the depth masking done in renderUsingAllocator is instead done for everything already before this method is called
					// this is because FrameGraphBuilder delays calls, so any rendersystem methods wont work with their intended timing
					((PostChainInterface)shader.getPostChain()).luminance$render(data.builder(), data.textureWidth(), data.textureHeight(), data.targetBundle(), chain);
				} catch (Exception error) {
					Data.getVersion().sendToLog(LogType.ERROR, "Failed to render processor: {}", error.getLocalizedMessage());
				}
			}
		} catch (Exception error) {
			Data.getVersion().sendToLog(LogType.ERROR, "Failed to render post effect processor: {}", error.getLocalizedMessage());
		}
	}

	public static void renderFromGameData(Identifier id, Shader.Data shader, Runnables.GameRender.Data data) {
		try {
			if (shader != null && shader.shader() != null && shader.shader().getShaderData() != null) {
				if (shader.shader().getShouldRender()) {
					if (shader.shader().getPostChain() == null) {
						try {
							shader.shader().loadPostChain();
						} catch (Exception error) {
							Data.getVersion().sendToLog(LogType.ERROR, "Failed to set \"{}:{}:{}\" post processor: {}", id, shader.id(), shader.shader().getShaderData().getID(), error);
							Events.ShaderRender.Shaders.remove(id, shader.id());
						}
					}
					renderShaderFromGameData(shader.shader(), data, null);
				}
			}
		} catch (Exception error) {
			Data.getVersion().sendToLog(LogType.ERROR, "Failed to render \"{}:{}\" using allocator, shader: {}: {}", id, shader.id(), shader.shader().getShaderData().getID(), error);
		}
	}

	// This is identical to the deprecated `PostChain.process(renderTarget, resourceAllocator);` function.
	public static void renderShaderFromGameData(Shader shader, Runnables.GameRender.Data data, @Nullable Identifier chain) {
		try {
			if (shader.getPostChain() != null) {
				Runnables.LevelRender.fromGameData((worldData) -> shader.getPostChain().luminance$render(worldData.builder(), worldData.textureWidth(), worldData.textureHeight(), worldData.targetBundle(), chain), data);
			}
		} catch (Exception error) {
			Data.getVersion().sendToLog(LogType.ERROR, "Failed to render processor: {}", error.getLocalizedMessage());
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

	public static Shader get(ShaderRegistryEntry shaderData, Callable<RenderLocations.RenderLocation<?>> renderLocation, Callable<Boolean> shouldRender) {
		return new Shader(shaderData, renderLocation, shouldRender);
	}

	public static Shader get(ShaderRegistryEntry shaderData, Callable<RenderLocations.RenderLocation<?>> renderLocation) {
		return new Shader(shaderData, renderLocation);
	}

	public static Identifier getPostEffectIdentifier(Identifier post_effect, boolean full) {
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

	public static boolean preventRegister(JsonObject reader, Identifier id, String type) throws VersionParsingException {
		if (reader.has("dependencies")) {
			JsonObject dependencies = reader.get("dependencies").getAsJsonObject();
			if (dependencies.has("depends")) {
				for (Map.Entry<String, JsonElement> dependency : dependencies.get("depends").getAsJsonObject().entrySet()) {
					Optional<ModContainer> dependencyMod = FabricLoader.getInstance().getModContainer(dependency.getKey());
					if (dependencyMod.isEmpty()) {
						Data.getVersion().sendToLog(LogType.WARN, "'{}' is required for {} '{}', but mod couldn't be found!", dependency.getKey(), type, id);
						return true;
					}

					List<String> matcherStringList = new ArrayList<>();
					if (dependency.getValue().isJsonPrimitive()) matcherStringList.add(dependency.getValue().getAsString());
					else for (JsonElement version : dependency.getValue().getAsJsonArray()) matcherStringList.add(version.getAsString());

					if (!versionMatches(dependencyMod.get().getMetadata().getVersion(), VersionPredicate.parse(matcherStringList))) {
						Data.getVersion().sendToLog(LogType.WARN, "'{}' with version '{}' is required for {} '{}', but a compatible version couldn't be found!", dependency.getKey(), matcherStringList.toString(), type, id);
						return true;
					}
				}
			}
			if (dependencies.has("breaks")) {
				for (Map.Entry<String, JsonElement> dependency : dependencies.get("breaks").getAsJsonObject().entrySet()) {
					Optional<ModContainer> dependencyMod = FabricLoader.getInstance().getModContainer(dependency.getKey());
					if (dependencyMod.isPresent()) {
						List<String> matcherStringList = new ArrayList<>();
						if (dependency.getValue().isJsonPrimitive()) matcherStringList.add(dependency.getValue().getAsString());
						else for (JsonElement version : dependency.getValue().getAsJsonArray()) matcherStringList.add(version.getAsString());

						if (versionMatches(dependencyMod.get().getMetadata().getVersion(), VersionPredicate.parse(matcherStringList))) {
							Data.getVersion().sendToLog(LogType.WARN, "'{}' with version '{}' breaks {} '{}'!", dependency.getKey(), matcherStringList.toString(), type, id);
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public static boolean versionMatches(Version version, Collection<VersionPredicate> ranges) {
		for (VersionPredicate predicate : ranges) if (predicate.test(version)) return true;
		return false;
	}

	public static PostChainInterface getPostChain(Identifier identifier) {
		PostChainInterface postChainInterface = Events.CustomPostChains.get(identifier);
		if (postChainInterface != null) {
			return postChainInterface;
		}
		return (PostChainInterface) ClientData.minecraft.getShaderManager().getPostChain(identifier, LevelTargetBundle.SORTING_TARGETS);
	}

	public static boolean targetsUseDepth(List<Identifier> targetIds, List<PostPass.TargetInput> targets) {
		return targetsUseDepth(targets.stream().filter((target) -> targetIds.contains(target.targetId())).toList());
	}

	public static boolean targetsUseDepth(List<PostPass.TargetInput> targets) {
		return targets.stream().anyMatch(PostPass.TargetInput::depthBuffer);
	}
}
