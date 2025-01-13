package com.mclegoman.luminance.client.shaders.uniforms;

import com.mclegoman.luminance.client.shaders.ShaderTime;
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
    public void update(ShaderTime shaderTime) {
        if (parent == null) {
            updateRecursively(shaderTime);
        }
    }

    protected void updateRecursively(ShaderTime shaderTime) {
        updateValue(shaderTime);

        for (TreeUniform child : children) {
            child.updateRecursively(shaderTime);
        }
    }

    public abstract void updateValue(ShaderTime shaderTime);

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
