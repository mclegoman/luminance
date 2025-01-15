package com.mclegoman.luminance.client.shaders.uniforms;

import com.mclegoman.luminance.client.shaders.ShaderTime;
import com.mclegoman.luminance.client.shaders.uniforms.config.EmptyConfig;
import com.mclegoman.luminance.client.shaders.uniforms.config.UniformConfig;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class TreeUniform implements Uniform {
    public final String name;
    public final List<TreeUniform> children;
    @Nullable
    public TreeUniform parent;

    public boolean useConfig;
    public TreeUniform root;

    protected TreeUniform(String name, boolean useConfig) {
        this.name = name;
        this.useConfig = useConfig;
        this.children = new ArrayList<>();
    }

    @Override
    public void update(ShaderTime shaderTime) {
        if (!useConfig && parent == null) {
            updateRecursively(EmptyConfig.INSTANCE, shaderTime);
        }
    }

    @Override
    public UniformValue get(UniformConfig config, ShaderTime shaderTime) {
        if (useConfig) {
            root.updateRecursively(config, shaderTime);
        }
        return getCache(config, shaderTime);
    }

    protected void updateRecursively(UniformConfig config, ShaderTime shaderTime) {
        for (TreeUniform child : children) {
            child.beforeParentCacheUpdate(config, shaderTime);
        }

        calculateCache(config, shaderTime);

        for (TreeUniform child : children) {
            child.updateRecursively(config, shaderTime);
        }
    }

    public abstract UniformValue getCache(UniformConfig config, ShaderTime shaderTime);
    public abstract void beforeParentCacheUpdate(UniformConfig config, ShaderTime shaderTime);
    public abstract void calculateCache(UniformConfig config, ShaderTime shaderTime);

    public final TreeUniform addChildren(TreeUniform... children) {
        this.children.addAll(List.of(children));
        for (TreeUniform child : children) {
            child.parent = this;
        }
        return this;
    }

    public void onRegister(String fullName) {
        root = this;
        while (root.parent != null) {
            root = root.parent;
        }
        useConfig = root.useConfig;
    }

    @Override
    public void tick() {}
}
