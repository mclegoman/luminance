/*
    Luminance
    Contributor(s): Nettakrim
    Github: https://github.com/mclegoman/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.events;

import com.mclegoman.luminance.client.LuminanceClient;
import com.mclegoman.luminance.client.data.ClientData;
import com.mclegoman.luminance.client.util.CompatHelper;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.resource.RenderTargetDescriptor;
import dev.dannytaylor.perspective.seam.common.data.log.SeamLog;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class DepthFix {
    private static RenderTargetDescriptor targetDescriptor;
    private static RenderTarget worldDepth;

    private static final RenderPipeline depthPipeline = RenderPipeline.builder(RenderPipelines.POST_PROCESSING_SNIPPET)
            .withDepthWrite(true) // post-processing snippet has depth write off
            .withFragmentShader(LuminanceClient.getMod().idOf("depth_fix"))
            .withVertexShader(Identifier.withDefaultNamespace("core/screenquad"))
            .withLocation(LuminanceClient.getMod().idOf("depth_fix"))
            .build();

    protected static void copyDepth(GraphicsResourceAllocator allocator) {
        cleanupDepth(allocator);

        if (CompatHelper.isIrisShadersEnabled()) {
            return;
        }

        RenderTarget target = ClientData.minecraft.getMainRenderTarget();

        targetDescriptor = new RenderTargetDescriptor(target.width, target.height, true, 0);
        worldDepth = allocator.acquire(targetDescriptor);
        worldDepth.copyDepthFrom(target);
    }

    public static void mergeDepth(GraphicsResourceAllocator allocator) {
        if (CompatHelper.isIrisShadersEnabled() || worldDepth == null) {
            return;
        }

        try {
            RenderTarget target = ClientData.minecraft.getMainRenderTarget();

            // temporary fix for depth, just ignoring hand
            target.copyDepthFrom(worldDepth);

            // attempt at a proper fix - it correctly renders the shader
            // but the texture bind doesnt seem to be doing anything
            // and it also cant write to the depth buffer
            // TODO: merge the hand depth nicely

			/*
			CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
			RenderSystem.backupProjectionMatrix();

			CachedOrthoProjectionMatrixBuffer matrixCache = ((ShaderManagerAccessor)ClientData.minecraft.getShaderManager()).getPostChainProjectionMatrixBuffer();
			RenderSystem.setProjectionMatrix(matrixCache.getBuffer(target.width, target.height), ProjectionType.ORTHOGRAPHIC);

			try (RenderPass renderPass = commandEncoder.createRenderPass(() -> "Depth Merge", target.getColorTextureView(), OptionalInt.empty(), target.getDepthTextureView(), OptionalDouble.empty())) {
				renderPass.setPipeline(depthPipeline);

				GpuSampler sampler = RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST);
				renderPass.bindTexture("InSampler", worldDepth.getDepthTextureView(), sampler);
				renderPass.bindTexture("HandSampler", target.getDepthTextureView(), sampler);

				//GL11.glDepthFunc(GL11.GL_ALWAYS);
				//GL11.glEnable(GL11.GL_DEPTH_TEST);

				renderPass.draw(0, 3);

				//GL11.glDisable(GL11.GL_DEPTH_TEST);
				//GL11.glDepthFunc(GL11.GL_LESS);
            }

			RenderSystem.restoreProjectionMatrix();
			*/
        } catch (Exception e) {
            SeamLog.error(LuminanceClient.getMod(), "Failed to merge depth!", e);
        }

        cleanupDepth(allocator);
    }

    private static void cleanupDepth(GraphicsResourceAllocator allocator) {
        if (worldDepth != null) {
            allocator.release(targetDescriptor, worldDepth);
            worldDepth = null;
        }
    }
}