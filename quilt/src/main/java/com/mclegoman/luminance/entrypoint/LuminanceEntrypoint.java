/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.entrypoint;

import org.quiltmc.loader.api.entrypoint.EntrypointContainer;
import org.quiltmc.loader.impl.QuiltLoaderImpl;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class LuminanceEntrypoint {
	// TODO: Add Server, and Pre-Launch Initializers.
	public static final String clientInitKey;
	public static final String commonInitKey;
	public static <T> void init(String key, Class<T> initClass, BiConsumer<T, String> function) {
		initModContainer(key, initClass, (container) -> function.accept(container.getEntrypoint(), container.getProvider().metadata().id()));
	}
	public static <T> void initModContainer(String key, Class<T> initClass, Consumer<EntrypointContainer<T>> entrypointContainerConsumer) {
		QuiltLoaderImpl loader = QuiltLoaderImpl.INSTANCE;
		if (loader.hasEntrypoints(key)) initModContainer(entrypointContainerConsumer, loader.getEntrypointContainers(key, initClass));
	}
	private static <T> void initModContainer(Consumer<EntrypointContainer<T>> entrypointContainerConsumer, List<EntrypointContainer<T>> entrypointContainers) {
		for (EntrypointContainer<T> container : entrypointContainers) entrypointContainerConsumer.accept(container);
	}
	static {
		clientInitKey = "luminance_client";
		commonInitKey = "luminance_common";
	}
}