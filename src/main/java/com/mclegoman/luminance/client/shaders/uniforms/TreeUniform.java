package com.mclegoman.luminance.client.shaders.uniforms;

import com.mclegoman.luminance.client.shaders.ShaderTime;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class TreeUniform<V,P> implements Uniform<V> {
    public final String name;
    public final List<TreeUniform<?,V>> children;
    @Nullable
    public TreeUniform<P,?> parent;

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

        for (TreeUniform<?,V> child : children) {
            child.updateRecursively(shaderTime);
        }
    }

    public abstract void updateValue(ShaderTime shaderTime);

    @SafeVarargs
    public final TreeUniform<V,P> addChildren(TreeUniform<?, V>... children) {
        this.children.addAll(List.of(children));
        for (TreeUniform<?,V> child : children) {
            child.parent = this;
        }
        return this;
    }

    @Override
    public void tick() {}
}
