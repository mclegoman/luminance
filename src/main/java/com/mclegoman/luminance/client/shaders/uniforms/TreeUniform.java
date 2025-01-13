package com.mclegoman.luminance.client.shaders.uniforms;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class TreeUniform implements Uniform {
    public final String name;
    public final List<TreeUniform> children;
    @Nullable
    public TreeUniform parent;

    protected TreeUniform(String name) {
        this.name = name;
        this.children = new ArrayList<>();
    }

    @Override
    public void update(float tickDelta, float deltaTime) {
        if (parent == null) {
            updateRecursively(tickDelta, deltaTime);
        }
    }

    protected void updateRecursively(float tickDelta, float deltaTime) {
        updateValue(tickDelta, deltaTime);

        for (TreeUniform child : children) {
            child.updateRecursively(tickDelta, deltaTime);
        }
    }

    public abstract void updateValue(float tickDelta, float deltaTime);

    public TreeUniform addChildren(TreeUniform... children) {
        this.children.addAll(List.of(children));
        for (TreeUniform child : children) {
            child.parent = this;
        }
        return this;
    }

    @Override
    public Optional<Float> getMin() {
        if (parent == null) {
            return Optional.empty();
        }
        return parent.getMin();
    }

    @Override
    public Optional<Float> getMax() {
        if (parent == null) {
            return Optional.empty();
        }
        return parent.getMax();
    }

    @Override
    public void tick() {}
}
