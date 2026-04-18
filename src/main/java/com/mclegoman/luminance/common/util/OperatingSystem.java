/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.common.util;

import java.util.Locale;

public enum OperatingSystem {
    LINUX("linux"),
    WINDOWS("win"),
    MAC_OS("mac"),
    UNSUPPORTED(null);

    private final String identifier;

    OperatingSystem(String identifier) {
        this.identifier = identifier;
    }

    private static OperatingSystem checkOs() {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        for (OperatingSystem os : values()) {
            if (os.identifier == null) continue;
            if (osName.contains(os.identifier)) return os;
        }
        return UNSUPPORTED;
    }

    private static final OperatingSystem OS = checkOs();

    public static OperatingSystem getOs() {
        return OS;
    }
}
