/*
    Luminance
    Author: dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.compat.modmenu;

import com.mclegoman.luminance.client.util.CompatHelper;
import com.mclegoman.luminance.common.data.Data;
import com.terraformersmc.modmenu.util.mod.fabric.FabricIconHandler;
import com.terraformersmc.modmenu.util.mod.fabric.FabricMod;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.texture.NativeImageBackedTexture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(priority = 100, value = FabricMod.class, remap = false)
public abstract class FabricModMixin {
	@Shadow @Final protected ModMetadata metadata;
	@Inject(method = "getIcon", at = @At("RETURN"), cancellable = true)
	private void luminance$getIcon(FabricIconHandler iconHandler, int i, CallbackInfoReturnable<NativeImageBackedTexture> cir) {
		if (CompatHelper.shouldOverrideModMenuIcon(metadata.getId())) {
			String iconPath = CompatHelper.getOverrideModMenuIcon(this.metadata.getId());
			if (iconPath != null) cir.setReturnValue(iconHandler.createIcon(Data.getModContainer(this.metadata.getId()), iconPath));
		}
	}
}
