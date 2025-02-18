/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.api.mod;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.Person;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class ModHelper {
	public static Optional<ModContainer> getModContainer(String modId) {
		Optional<net.fabricmc.loader.api.ModContainer> modContainer = FabricLoader.getInstance().getModContainer(modId);
		if (modContainer.isPresent()) {
			ModMetadata metadata = modContainer.get().getMetadata();
			Collection<String> contributors = new ArrayList<>();
			for (Person person : metadata.getAuthors()) contributors.add(person.getName());
			for (Person person : metadata.getContributors()) contributors.add(person.getName());
			return Optional.of(new ModContainer(new ModContainer.ModMetadata(metadata.getId(), metadata.getVersion().getFriendlyString(), metadata.getName(), metadata.getDescription(), metadata.getLicense(), contributors)));
		} else return Optional.empty();
	}
	public static boolean isModLoaded(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}
}
