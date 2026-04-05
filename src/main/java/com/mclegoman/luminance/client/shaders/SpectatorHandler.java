/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/mclegoman/luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.shaders;

import com.mclegoman.luminance.client.events.Callables;
import com.mclegoman.luminance.client.events.Events;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public interface SpectatorHandler extends Callables.PriorityHandler<@NotNull Entity> {
    List<SpectatorHandler> activeHandlers = new ArrayList<>();

    static void onSpectate(@NotNull Entity entity, Mode mode) {
        clearActive();

        int highest = -1;
        for (SpectatorHandler handler : Events.SpectatorHandlers.registry.values()) {
            int priority = handler.getPriority(entity);

            if (priority < 0) {
                continue;
            }

            if (mode == Mode.ALL) {
                activeHandlers.add(handler);
                continue;
            }

            if (priority > highest) {
                highest = priority;
                activeHandlers.clear();
                activeHandlers.add(handler);
            } else if (priority == highest && mode == Mode.EQUAL) {
                activeHandlers.add(handler);
            }
        }

        applyActive(entity);
    }

    static void applyActive(@NotNull Entity entity) {
        for (SpectatorHandler handler : activeHandlers) {
            handler.apply(entity);
        }
    }

    static void clearActive() {
        for (SpectatorHandler handler : activeHandlers) {
            handler.clear();
        }
        activeHandlers.clear();
    }

    enum Mode {
        FIRST,
        EQUAL,
        ALL
    }
}
