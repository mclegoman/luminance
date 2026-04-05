/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.mixin.client.shaders;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import com.mojang.blaze3d.resource.CrossFrameResourcePool;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {
	@Invoker("getFov")
	float invokeGetFov(Camera camera, float tickProgress, boolean changingFov);
	@Accessor("resourcePool")
    CrossFrameResourcePool getResourcePool();
	@Accessor("random")
    RandomSource getRandom();
	@Accessor("mainCamera")
    Camera getMainCamera();
}