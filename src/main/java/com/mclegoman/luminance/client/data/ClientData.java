/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.data;

import com.mclegoman.luminance.client.config.LuminanceConfig;
import com.mclegoman.luminance.client.util.CompatHelper;
import com.mclegoman.luminance.client.util.IconOverride;
import com.mclegoman.luminance.common.data.Data;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public class ClientData {
	public static final Minecraft minecraft = Minecraft.getInstance();

	public static boolean isDevelopment() {
		return FabricLoaderImpl.INSTANCE.isDevelopmentEnvironment() || LuminanceConfig.config.debug.value();
	}

	public static IconOverride getOverrideIcon() {
		IconOverride iconOverride = CompatHelper.getIconOverride(Data.getVersion().getID());
		if (iconOverride == null) {
			Optional<ModContainer> modContainer = Data.getVersion().getModContainer();
			if (modContainer.isPresent()) {
				Optional<String> path = modContainer.get().getMetadata().getIconPath(128);
				if (path.isPresent()) {
					iconOverride = new IconOverride(assetPathToIdentifier(path.get()), () -> false); // We set this to false as we'd rather it use the default system, however sodium settings will ignore this :)
				}
			}
		}
		return iconOverride;
	}

	public static Identifier assetPathToIdentifier(String path) { // could possibly add a /data/ check and move this to Data instead?
		if (path == null || path.isBlank()) throw new IllegalArgumentException("Path cannot be null or blank");
		if (!path.startsWith("/")) path = "/" + path;
		if (!path.startsWith("/assets/")) throw new IllegalArgumentException("Invalid asset path: " + path);
		String trimmed = path.substring("/assets/".length());
		int slashIndex = trimmed.indexOf("/");
		if (slashIndex == -1) throw new IllegalArgumentException("Missing path in: " + path);
		return Identifier.fromNamespaceAndPath(trimmed.substring(0, slashIndex), trimmed.substring(slashIndex + 1));
	}
}