package com.mclegoman.luminance.client.shaders;

import com.mclegoman.luminance.client.shaders.interfaces.pipeline.UniformValueInterface;
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
import java.util.function.Supplier;

public class LuminanceUniformBuffer {
    private final Supplier<MappableRingBuffer> bufferSupplier;

    private final List<List<Number>> defaultValues;

    private MappableRingBuffer ringBuffer;
    private GpuBuffer bufferCache;
    private boolean changes;

    public LuminanceUniformBuffer(@NotNull List<UniformValue> uniformValues, int bufferSize) {
        // TODO: store uniform names from the uniform values

        bufferSupplier = () -> new MappableRingBuffer(() -> "Luminance Shader UBO", 130, bufferSize);

        defaultValues = new ArrayList<>();
        for (UniformValue uniformValue : uniformValues) {
            UniformValueInterface uniformValueInterface = (UniformValueInterface)uniformValue;
            defaultValues.add(uniformValueInterface.luminance$getValue());
        }
    }

    public void updateBuffer(@NotNull List<UniformInstance> uniformInstances) {
        if (ringBuffer == null) {
            ringBuffer = bufferSupplier.get();
        }

        changes = false;

        bufferCache = ringBuffer.getBlocking();
        GpuBuffer.MappedView mappedView = RenderSystem.getDevice().createCommandEncoder().mapBuffer(bufferCache, false, true);
        ByteBuffer buffer = mappedView.data();
        buffer.position(0);
        Std140Builder builder = Std140Builder.intoBuffer(buffer);

        for (int i = 0; i < defaultValues.size(); i++) {
            List<Number> numbers = defaultValues.get(i);

            UniformInstance instance = uniformInstances.get(i);
            List<Float> values = instance.getValues();

            if (values == null) {
                for (Number number : numbers) {
                    write(builder, number, null);
                }
            } else {
                for (int j = 0; j < numbers.size(); j++) {
                    Float value = values.get(i);
                    write(builder, numbers.get(i), value);
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
