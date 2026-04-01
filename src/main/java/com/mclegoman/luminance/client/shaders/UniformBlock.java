package com.mclegoman.luminance.client.shaders;

import com.google.common.collect.ImmutableList;
import com.mclegoman.luminance.client.shaders.interfaces.pipeline.UniformValueInterface;
import com.mclegoman.luminance.client.shaders.overrides.LuminanceUniformOverride;
import com.mclegoman.luminance.client.shaders.uniforms.config.MapConfig;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.MappableRingBuffer;
import net.minecraft.client.gl.UniformValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class UniformBlock {
    public final ImmutableList<@NotNull UniformInstance> uniforms;
    private final int bufferSize;

    private MappableRingBuffer ringBuffer;
    private GpuBuffer bufferCache;
    private boolean changes;

    public UniformBlock(@NotNull List<UniformValue> uniformValues, int bufferSize) {
        ImmutableList.Builder<UniformInstance> builder = ImmutableList.builder();

        for (UniformValue uniform : uniformValues) {
            UniformValueInterface uniformInterface = (UniformValueInterface)uniform;
            UniformInstance instance = new UniformInstance(uniformInterface.luminance$getName().orElse(uniform.getType().asString()), uniformInterface.luminance$getValue());

            uniformInterface.luminance$getOverride().ifPresent((override) -> {
                int length = uniformInterface.luminance$getLength();
                int overrideValues = override.size();

                // make sure overrides are the same length as the type
                if (overrideValues > length) {
                    override = override.subList(0, length);
                }
                if (overrideValues < length) {
                    // copy list so changes arent destructive (although it shouldnt matter if they were)
                    for (override = new ArrayList<>(override); overrideValues < length; overrideValues++) {
                        override.add(null);
                    }
                }
                instance.override = new LuminanceUniformOverride(override);
            });

            uniformInterface.luminance$getConfig().ifPresent((config) -> instance.config = new MapConfig(config));

            builder.add(instance);
        }

        uniforms = builder.build();
        this.bufferSize = bufferSize;
    }

    public void updateBuffer() {
        if (ringBuffer == null) {
            // TODO: does this need to be a ring buffer? we never rotate() it
            ringBuffer = new MappableRingBuffer(() -> "Luminance Shader UBO", 130, bufferSize);
        }

        changes = false;

        bufferCache = ringBuffer.getBlocking();
        GpuBuffer.MappedView mappedView = RenderSystem.getDevice().createCommandEncoder().mapBuffer(bufferCache, false, true);
        ByteBuffer buffer = mappedView.data();
        buffer.position(0);
        Std140Builder builder = Std140Builder.intoBuffer(buffer);

        for (UniformInstance instance : uniforms) {
            List<Float> values = instance.getValues();

            if (values == null) {
                for (Number number : instance.defaultValue) {
                    write(builder, number, null);
                }
            } else {
                for (int i = 0; i < instance.defaultValue.size(); i++) {
                    Float value = values.get(i);
                    write(builder, instance.defaultValue.get(i), value);
                    changes |= value != null;
                }
            }
        }
        mappedView.close();
    }

    public GpuBuffer replaceBuffer(GpuBuffer original) {
        return changes ? bufferCache : original;
    }

    private void write(Std140Builder builder, Number defaultValue, @Nullable Float overrideValue) {
        if (overrideValue != null) {
            // match type
            if (defaultValue instanceof Float) {
                builder.putFloat(overrideValue);
            } else if (defaultValue instanceof Integer) {
                builder.putInt(Math.round(overrideValue));
            }
            return;
        }

        if (defaultValue instanceof Float f) {
            builder.putFloat(f);
        } else if (defaultValue instanceof Integer i) {
            builder.putInt(i);
        }
    }

    public void close() {
        if (ringBuffer != null) {
            ringBuffer.close();
        }
    }
}
