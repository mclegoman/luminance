/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.api.entrypoint;

import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.LogType;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.loader.api.entrypoint.EntrypointContainer;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class LuminanceEntrypoint {
	public static <T> void init(String key, Class<T> initClass, BiConsumer<T, String> function) {
		initModContainer(key, initClass, (container) -> function.accept(container.getEntrypoint(), container.getProvider().metadata().id()));
	}
	public static <T> void initModContainer(String key, Class<T> initClass, Consumer<EntrypointContainer<T>> entrypointContainerConsumer) {
		try {
			initModContainer(entrypointContainerConsumer, QuiltLoader.getEntrypointContainers(key, initClass));
		} catch (Exception error) {
			Data.getVersion().sendToLog(LogType.ERROR, error.getLocalizedMessage());
		}
	}
	private static <T> void initModContainer(Consumer<EntrypointContainer<T>> entrypointContainerConsumer, List<EntrypointContainer<T>> entrypointContainers) {
		for (EntrypointContainer<T> container : entrypointContainers) entrypointContainerConsumer.accept(container);
	}
}