package com.mclegoman.luminance.client.shaders.uniforms;

import com.mclegoman.luminance.client.shaders.ShaderTime;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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
        for (TreeUniform child : children) {
            child.preParentUpdate(shaderTime);
        }

        updateValue(shaderTime);

        for (TreeUniform child : children) {
            child.updateRecursively(shaderTime);
        }
    }


    public abstract void preParentUpdate(ShaderTime shaderTime);
    public abstract void updateValue(ShaderTime shaderTime);

    public final TreeUniform addChildren(TreeUniform... children) {
        this.children.addAll(List.of(children));
        for (TreeUniform child : children) {
            child.parent = this;
        }
        return this;
    }

    public abstract void onRegister(String fullName);

    @Override
    public void tick() {}
}
