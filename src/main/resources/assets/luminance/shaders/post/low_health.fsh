#version 330

uniform sampler2D InSampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform LowHealthConfig {
    float IsActive;
    float CurrentHealth;
    float CurrentAdditionalHealth;
    float MaxHealth;
    float MaxAdditionalHealth;
    vec3 OverlayColor;
};

in vec2 texCoord;

out vec4 fragColor;

float getCurrentHealth() {
    return CurrentHealth + CurrentAdditionalHealth;
}

float getMaxHealth() {
    return MaxHealth + MaxAdditionalHealth;
}

float getHealth(float maxMultiplier) {
    return getCurrentHealth() / (getMaxHealth() * maxMultiplier);
}

void main() {
    vec4 color = texture(InSampler, texCoord);
    fragColor = vec4(mix(color.rgb, mix(mix(color.rgb, OverlayColor, smoothstep(0.0, 0.5, distance(texCoord, vec2(0.5, 0.5)))), color.rgb, min(getHealth(0.5), 1.0)), IsActive), color.a);
}