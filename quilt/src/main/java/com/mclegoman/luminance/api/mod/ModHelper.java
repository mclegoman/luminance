/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.api.mod;

import org.quiltmc.loader.api.ModContributor;
import org.quiltmc.loader.api.ModLicense;
import org.quiltmc.loader.api.ModMetadata;
import org.quiltmc.loader.api.QuiltLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class ModHelper {
	public static Optional<ModContainer> getModContainer(String modId) {
		Optional<org.quiltmc.loader.api.ModContainer> modContainer = QuiltLoader.getModContainer(modId);
		if (modContainer.isPresent()) {
			ModMetadata metadata = modContainer.get().metadata();
			Collection<String> licenses = new ArrayList<>();
			for (ModLicense license : metadata.licenses()) licenses.add(license.id());
			Collection<String> contributors = new ArrayList<>();
			for (ModContributor contributor : metadata.contributors()) contributors.add(contributor.name());
			return Optional.of(new ModContainer(new ModContainer.ModMetadata(metadata.id(), metadata.version().raw(), metadata.name(), metadata.description(), licenses, contributors)));
		} else return Optional.empty();
	}
	public static boolean isModLoaded(String modId) {
		return QuiltLoader.isModLoaded(modId);
	}
}
