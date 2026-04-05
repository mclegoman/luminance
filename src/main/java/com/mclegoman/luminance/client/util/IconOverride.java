/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.util;

import net.minecraft.resources.Identifier;

import java.util.concurrent.Callable;

public class IconOverride {
    private Identifier iconId;
    private Callable<Boolean> shouldOverride;

    public IconOverride(Identifier iconId, Callable<Boolean> shouldOverride) {
        this.iconId = iconId;
        this.shouldOverride = shouldOverride;
    }

    public Identifier getIconId() {
        return this.iconId;
    }

    public void setIconId(Identifier iconId) {
        this.iconId = iconId;
    }

    public String getIconLocation() {
        return "assets/" + getIconId().getNamespace() + "/" + getIconId().getPath();
    }

    public boolean shouldOverride() throws Exception {
        return this.shouldOverride.call();
    }

    public void setShouldOverride(Callable<Boolean> shouldOverride) {
        this.shouldOverride = shouldOverride;
    }
}
