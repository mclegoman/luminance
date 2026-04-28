/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.util;

import com.mclegoman.luminance.client.LuminanceClient;
import com.mclegoman.luminance.common.util.DateHelper;
import dev.dannytaylor.perspective.seam.client.events.IconOverride;
import dev.dannytaylor.perspective.seam.client.events.SeamClientEvents;
import dev.dannytaylor.perspective.seam.common.data.AbstractMod;
import dev.dannytaylor.perspective.seam.common.events.SeamEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.irisshaders.iris.api.v0.IrisApi;

public class CompatHelper {
	public static void onInitialize(AbstractMod mod) {
		SeamEvents.onInitialize(mod, "Compatibility", () -> {
			SeamClientEvents.registerIconOverride(mod.getId(), new IconOverride(LuminanceClient.getMod().idOf("textures/icons/pride.png"), DateHelper::isPride));
			// Luminance itself doesn't require the badge.
			// Mods that use Luminance can use this in their client initializer to add the badge to their mod.
			// This is mostly to indicate to users that luminance resource packs are compatible.
			//Badges.luminance(mod.getId());
		});
	}

	public static boolean isIrisShadersEnabled() {
		try {
			return FabricLoader.getInstance().isModLoaded("iris") && IrisApi.getInstance().isShaderPackInUse();
		} catch (Exception ignored) {
			// We use a try/catch here just in case Iris ever changes where isShaderPackInUse is located.
		}
		return false;
	}
}
