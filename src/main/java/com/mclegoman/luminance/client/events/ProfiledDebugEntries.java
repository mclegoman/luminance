/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.events;

import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.client.gui.components.debug.DebugScreenEntryStatus;
import net.minecraft.client.gui.components.debug.DebugScreenProfile;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public class ProfiledDebugEntries {
    private static final Map<Identifier, ProfiledEntry> entries = new HashMap<>();

    public static Map<Identifier, DebugScreenEntryStatus> getProfile(DebugScreenProfile profile) {
        Map<Identifier, DebugScreenEntryStatus> profiles = new HashMap<>();
        entries.forEach((id, entry) -> {
            if (entry.profile().equals(profile)) profiles.put(id, entry.status());
        });
        return profiles;
    }

    public static void register(Identifier identifier, DebugScreenEntry debugScreenEntry, DebugScreenProfile profile, DebugScreenEntryStatus status) {
        entries.put(identifier, new ProfiledEntry(profile, status));
        DebugScreenEntries.register(identifier, debugScreenEntry);
    }

    public record ProfiledEntry(DebugScreenProfile profile, DebugScreenEntryStatus status) {
    }
}