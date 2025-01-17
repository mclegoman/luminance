/*
    Luminance
    Author: dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.compat.modmenu;

import com.mclegoman.luminance.client.util.CompatHelper;
import com.terraformersmc.modmenu.util.mod.fabric.FabricIconHandler;
import com.terraformersmc.modmenu.util.mod.fabric.FabricMod;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.texture.NativeImageBackedTexture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(priority = 100, value = FabricMod.class, remap = false)
public abstract class FabricModMixin {
	@Shadow @Final protected ModMetadata metadata;
	@Inject(method = "getIcon", at = @At("RETURN"), cancellable = true)
	private void luminance$getIcon(FabricIconHandler iconHandler, int i, CallbackInfoReturnable<NativeImageBackedTexture> cir) {
		if (CompatHelper.shouldOverrideModMenuIcon(metadata.getId())) {
			String iconPath = CompatHelper.getOverrideModMenuIcon(this.metadata.getId());
			Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(this.metadata.getId());
			if (modContainer.isPresent() && iconPath != null) cir.setReturnValue(iconHandler.createIcon(modContainer.get(), iconPath));
		}
	}
}
