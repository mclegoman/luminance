package com.mclegoman.luminance.mixin.client.events;

import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.events.DepthFix;
import com.mclegoman.luminance.client.events.Execute;
import com.mclegoman.luminance.client.shaders.RenderLocations;
import com.mclegoman.luminance.client.shaders.ShaderTime;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import dev.dannytaylor.perspective.seam.client.events.SeamClientExecute;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(priority = 100, value = SeamClientExecute.class, remap = false)
public abstract class SeamClientExecuteMixin {
    @Inject(method = "afterClientResourceReload", at = @At("RETURN"))
    private static void luminance$afterClientResourceReload(CallbackInfo ci) {
        Execute.onCameraEntitySet(ClientData.minecraft.getCameraEntity());
    }

    @Inject(method = "beforeGuiRender", at = @At("HEAD"))
    private static void luminance$beforeGuiRender(GuiGraphics guiGraphics, DeltaTracker renderTickCounter, CallbackInfo ci) {
        ShaderTime.currentRenderLocation = RenderLocations.UI;
    }

    @Inject(method = "beforeGameRender", at = @At("HEAD"))
    private static void luminance$beforeGameRender(CallbackInfo ci) {
        ShaderTime.currentRenderLocation = RenderLocations.GAME;
    }

    @Inject(method = "afterVanillaPostEffectRender", at = @At("HEAD"))
    private static void luminance$afterVanillaPostEffectRender(GraphicsResourceAllocator allocator, CallbackInfo ci) {
        DepthFix.mergeDepth(allocator);
    }

    @Unique private static RenderLocations.RenderLocation<?> luminance$afterUiBackgroundRender_previous;

    @Inject(method = "afterUiBackgroundRender", at = @At("HEAD"))
    private static void luminance$afterUiBackgroundRender_start(GraphicsResourceAllocator allocator, CallbackInfo ci) {
        luminance$afterUiBackgroundRender_previous = ShaderTime.currentRenderLocation;
        ShaderTime.currentRenderLocation = RenderLocations.UI_BACKGROUND;
    }

    @Inject(method = "afterUiBackgroundRender", at = @At("RETURN"))
    private static void luminance$afterUiBackgroundRender_finish(GraphicsResourceAllocator allocator, CallbackInfo ci) {
        // this and afterPanoramaRender are a special case, so resetting the RenderLocation it makes sense
        ShaderTime.currentRenderLocation = luminance$afterUiBackgroundRender_previous;
    }

    @Unique private static RenderLocations.RenderLocation<?> luminance$panorama_previous;

    @Inject(method = "afterPanoramaRender", at = @At("HEAD"))
    private static void luminance$afterPanoramaRender_start(GuiGraphics guiGraphics, int width, int height, boolean rotate, GraphicsResourceAllocator allocator, CallbackInfo ci) {
        luminance$panorama_previous = ShaderTime.currentRenderLocation;
        ShaderTime.currentRenderLocation = RenderLocations.PANORAMA;
    }

    @Inject(method = "afterPanoramaRender", at = @At("RETURN"))
    private static void luminance$afterPanoramaRender_finish(GuiGraphics guiGraphics, int width, int height, boolean rotate, GraphicsResourceAllocator allocator, CallbackInfo ci) {
        ShaderTime.currentRenderLocation = luminance$panorama_previous;
    }
}
