package com.mclegoman.luminance.client.shaders;

import com.google.common.collect.ImmutableList;
import com.mclegoman.luminance.client.shaders.interfaces.pipeline.UniformValueInterface;
import com.mclegoman.luminance.client.shaders.overrides.LuminanceUniformOverride;
import com.mclegoman.luminance.client.shaders.uniforms.config.MapConfig;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.LogType;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.MappableRingBuffer;
import net.minecraft.client.renderer.UniformValue;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UniformBlock {
    public final ImmutableList<@NotNull UniformInstance> uniforms;
    private final int bufferSize;

    private MappableRingBuffer ringBuffer;
    private boolean pass;

    public UniformBlock(@NotNull List<UniformValue> uniformValues, int bufferSize) {
        ImmutableList.Builder<UniformInstance> builder = ImmutableList.builder();

        for (UniformValue uniform : uniformValues) {
            UniformValueInterface uniformInterface = (UniformValueInterface)uniform;
            UniformInstance instance = new UniformInstance(uniformInterface.luminance$getName().orElse(uniform.type().getSerializedName()), uniformInterface.luminance$getValue());

            uniformInterface.luminance$getOverride().ifPresent((override) -> {
                int length = uniformInterface.luminance$getLength();
                int overrideValues = override.size();

                if (overrideValues == 1 && override.getFirst().startsWith("auto#")) {
                    // automatically populate override with _x etc if needed
                    String name = override.getFirst().substring(5);
                    override = new ArrayList<>(length);
                    if (length == 1) {
                        override.add(name);
                    } else {
                        for (int i = 0; i < length; i++) {
                            override.add(name + "_" + ("xyzw".charAt(i)));
                        }
                    }
                } else {
                    // make sure overrides are the same length as the type
                    if (overrideValues > length) {
                        override = override.subList(0, length);
                    }
                    if (overrideValues < length) {
                        // copy list since the list from getOverride() isnt mutable
                        for (override = new ArrayList<>(override); overrideValues < length; overrideValues++) {
                            override.add(null);
                        }
                    }
                }
                Data.getVersion().sendToLog(LogType.INFO, uniformInterface.luminance$getName().orElse("null")+" "+Arrays.toString(override.toArray()));
                instance.override = new LuminanceUniformOverride(override);
            });

            uniformInterface.luminance$getConfig().ifPresent((config) -> instance.config = new MapConfig(config));

            builder.add(instance);
        }

        uniforms = builder.build();
        this.bufferSize = bufferSize;
    }

    public void updateBuffer() {
        pass = true;

        for (UniformInstance instance : uniforms) {
            if (instance.override != null) {
                pass = false;
                break;
            }
        }

        // avoid creating buffer if the shader has no overrides
        // the default buffer is cached, so skipping on writing to the gpu again should be a little more efficient
        // and in the case that a shader *never* has an override, it slightly reduces vram usage (ring buffers are slightly larger than the cache as well!)
        if (pass) {
            return;
        }

        if (ringBuffer == null) {
            ringBuffer = new MappableRingBuffer(() -> "Luminance Shader UBO", 130, bufferSize);
        }

        GpuBuffer gpuBuffer = ringBuffer.currentBuffer();
        GpuBuffer.MappedView mappedView = RenderSystem.getDevice().createCommandEncoder().mapBuffer(gpuBuffer, false, true);
        Std140Builder builder = Std140Builder.intoBuffer(mappedView.data());

        for (UniformInstance instance : uniforms) {
            instance.putValues(builder);
        }

        mappedView.close();
    }

    public GpuBuffer replaceBuffer(GpuBuffer original) {
        return pass ? original : ringBuffer.currentBuffer();
    }

    public void rotateBuffer() {
        // buffer only needs to be rotated if it was used this call
        if (!pass) {
            ringBuffer.rotate();
        }
    }

    public void close() {
        if (ringBuffer != null) {
            ringBuffer.close();
        }
    }
}
