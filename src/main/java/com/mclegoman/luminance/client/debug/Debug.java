/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.debug;

import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.events.Events;
import com.mclegoman.luminance.client.shaders.RenderTypes;
import com.mclegoman.luminance.client.shaders.Shader;
import com.mclegoman.luminance.client.shaders.ShaderRegistryEntry;
import com.mclegoman.luminance.client.shaders.Shaders;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.Couple;
import com.mclegoman.luminance.common.util.LogType;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Debug {
	private static boolean setup;

	private static final Couple<Identifier, Identifier> debugShader;
	private static boolean debugShaderEnabled;
	public static Identifier debugRenderType;
	private static boolean disablePhotosensitive;

	public static void tick() {
		if (ClientData.isDevelopment()) {
			// This will select the top-most shader in the sorted shader list on boot.
			if (!setup) {
				resetDebugShader();
				applyDebugShader();
				setup = true;
			}
		}
	}

	public static boolean isDebugShaderEnabled() {
		return debugShaderEnabled;
	}

	public static void setDebugShaderEnabled(boolean value) {
		debugShaderEnabled = value;
	}

	public static Couple<Identifier, Identifier> getDebugShader() {
		return debugShader;
	}

	public static Optional<Identifier> cycleDebugRenderType(boolean backwards) {
		List<Identifier> renderTypes = new ArrayList<>(Events.RenderType.registry.keySet());
		if (!renderTypes.isEmpty()) {
			renderTypes.sort(Comparator.comparing(Identifier::toString));
			int prevIndex = renderTypes.indexOf(debugRenderType);
			if (prevIndex == -1) prevIndex = 0;
			int size = renderTypes.size();
			int index;
			if (backwards) index = (prevIndex - 1 + size) % size;
			else index = (prevIndex + 1 + size) % size;
			return Optional.of(debugRenderType = renderTypes.get(index));
		}
		return Optional.empty();
	}

	public static void applyDebugShader() {
		if (ClientData.isDevelopment()) {
			Events.ShaderRender.register(getDebugId(), new Events.ShaderRenderData(new ArrayList<>(), Debug::getDisablePhotosensitive));
			modifyDebugShader(Shaders.get(Debug.debugShader.getFirst(), Debug.debugShader.getSecond()));
		}
	}

	public static void modifyDebugShader(ShaderRegistryEntry shaderData) {
		Events.ShaderRender.modify(getDebugId(), new Events.ShaderRenderData(List.of(new Shader.Data(getDebugId(0), new Shader(shaderData, () -> Debug.debugRenderType, Debug::isDebugShaderEnabled))), Debug::getDisablePhotosensitive));
	}

	public static void setDebugShader(Identifier registry, Identifier shader) {
		if (ClientData.isDevelopment()) {
			try {
				Debug.getDebugShader().setFirst(registry);
				Debug.getDebugShader().setSecond(shader);
				applyDebugShader();
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, Translation.getString("Failed to set debug shader: {}", error));
				resetDebugShader();
				applyDebugShader();
			}
		}
	}

	public static void resetDebugShader() {
		Debug.getDebugShader().setFirst(Shaders.getMainRegistryId());
		Debug.getDebugShader().setSecond(Shaders.getOrderedShaderIds(Shaders.getMainRegistryId()).getFirst());
	}

	public static Identifier getDebugId() {
		return Identifier.of(Data.getVersion().getID(), "debug");
	}

	public static Identifier getDebugId(int index) {
		return Identifier.of(Data.getVersion().getID() + "_debug", String.valueOf(index));
	}

	public static boolean getRawDisablePhotosensitive() {
		return disablePhotosensitive;
	}

	public static boolean getDisablePhotosensitive(ShaderRegistryEntry shaderRegistryEntry) {
		return getRawDisablePhotosensitive();
	}

	public static void setDisablePhotosensitive(boolean value) {
		disablePhotosensitive = value;
	}

	static {
		debugShader = new Couple<>(Shaders.getMainRegistryId(), Shaders.getOrderedShaderIds(Shaders.getMainRegistryId()).getFirst());
		debugRenderType = RenderTypes.WORLD.identifier();
	}
}
