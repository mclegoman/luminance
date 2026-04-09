#version 330

uniform sampler2D InSampler;

layout(std140) uniform OnDamageConfig {
    float CurrentHurtTime;
    float MaxHurtTime;
    vec3 OverlayColor;
};

in vec2 texCoord;

out vec4 fragColor;

float getHurt() {
    return 1.0 - (CurrentHurtTime / MaxHurtTime);
}

void main() {
    vec4 color = texture(InSampler, texCoord);
    fragColor = vec4(mix(mix(color.rgb, OverlayColor, smoothstep(0.0, 0.5, distance(texCoord, vec2(0.5, 0.5)))), color.rgb, min(getHurt(), 1.0)), color.a);
}