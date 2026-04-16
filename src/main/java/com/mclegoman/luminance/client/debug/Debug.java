/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.debug;

import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.events.Events;
import com.mclegoman.luminance.client.shaders.*;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.Couple;
import com.mclegoman.luminance.common.util.LogType;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Debug {
	private static final Couple<Identifier, Identifier> debugShader;
	private static boolean debugShaderEnabled;
	public static RenderLocations.RenderLocation<?> debugRenderLocation;
	private static boolean disablePhotosensitive;

	public static boolean isDebugShaderEnabled() {
		return debugShaderEnabled;
	}

	public static void setDebugShaderEnabled(boolean value) {
		debugShaderEnabled = value;
	}

	public static Couple<Identifier, Identifier> getDebugShader() {
		return debugShader;
	}

	public static Optional<RenderLocations.RenderLocation<?>> cycleDebugRenderLocation(boolean backwards) {
		List<Identifier> renderLocations = new ArrayList<>(Events.RenderLocation.registry.keySet().stream().sorted().toList());
		if (!renderLocations.isEmpty()) {
			renderLocations.sort(Comparator.comparing(Identifier::toString));
			int prevIndex = renderLocations.indexOf(debugRenderLocation.identifier());
			if (prevIndex == -1) prevIndex = 0;
			int size = renderLocations.size();
			int index;
			if (backwards) index = (prevIndex - 1 + size) % size;
			else index = (prevIndex + 1 + size) % size;
			return Optional.of(debugRenderLocation = Events.RenderLocation.get(renderLocations.get(index)));
		} else {
			debugRenderLocation = RenderLocations.GAME;
			return Optional.empty();
		}
	}

	public static void applyDebugShader() {
		if (ClientData.isDevelopment()) {
			Events.ShaderRender.register(getDebugId(), new Events.ShaderRenderData(new ArrayList<>(), Debug::getDisablePhotosensitive));
			modifyDebugShader(ShaderStacks.getStack(Debug.debugShader.getFirst(), Debug.debugShader.getSecond()));
		}
	}

	public static void modifyDebugShader(ShaderStacks.Entry stack) {
		Events.ShaderRender.modify(getDebugId(), ShaderStacks.getShaders(getDebugId(0), stack, () -> Debug.debugRenderLocation, Debug::isDebugShaderEnabled, Debug::getDisablePhotosensitive));
	}

	public static void setDebugShader(Identifier registry, Identifier shader) {
		if (ClientData.isDevelopment()) {
			try {
				Debug.getDebugShader().setFirst(registry);
				Debug.getDebugShader().setSecond(shader);
				applyDebugShader();
			} catch (Exception error) {
				Data.getVersion().sendToLog(LogType.ERROR, "Failed to set debug shader: {}", error);
				resetDebugShader();
				applyDebugShader();
			}
		}
	}

	public static void resetDebugShader() {
		Debug.getDebugShader().setFirst(ShaderStacks.getMainRegistryId());
		Debug.getDebugShader().setSecond(ShaderStacks.getShaderStacks(ShaderStacks.getMainRegistryId()).getFirst());
	}

	public static Identifier getDebugId() {
		return Identifier.fromNamespaceAndPath(Data.getVersion().getID(), "debug");
	}

	public static Identifier getDebugId(int index) {
		return Identifier.fromNamespaceAndPath(Data.getVersion().getID() + "_debug", String.valueOf(index));
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
		debugShader = new Couple<>(ShaderStacks.getMainRegistryId(), ShaderStacks.getShaderStacks(ShaderStacks.getMainRegistryId()).getFirst());
		debugRenderLocation = RenderLocations.GAME;
	}
}
