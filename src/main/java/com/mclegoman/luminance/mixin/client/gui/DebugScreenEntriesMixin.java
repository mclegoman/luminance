/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.client.gui;

import com.mclegoman.luminance.client.events.ProfiledDebugEntries;
import net.minecraft.client.gui.components.debug.*;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(DebugScreenEntryList.class)
public abstract class DebugScreenEntriesMixin {
    @Shadow private Map<Identifier, DebugScreenEntryStatus> allStatuses;

    @Inject(method = "loadDefaultProfile", at = @At("RETURN"))
    private void luminance$loadDefaultProfile(CallbackInfo ci) {
        this.allStatuses.putAll(ProfiledDebugEntries.getProfile(DebugScreenProfile.DEFAULT));
    }

    @Inject(method = "loadProfile", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/debug/DebugScreenEntryList;rebuildCurrentList()V"))
    private void luminance$loadProfile(DebugScreenProfile debugScreenProfile, CallbackInfo ci) {
        this.allStatuses.putAll(ProfiledDebugEntries.getProfile(debugScreenProfile));
    }
}
