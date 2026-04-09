#version 330

uniform sampler2D InSampler;

layout(std140) uniform OnEffectConfig {
    float IsActive;
    vec3 OverlayColor;
};

in vec2 texCoord;

out vec4 fragColor;

float getActive(float multiplier) {
    return IsActive * multiplier;
}

void main() {
    vec4 color = texture(InSampler, texCoord);
    fragColor = vec4(mix(color.rgb, mix(color.rgb, OverlayColor, smoothstep(0.0, 0.5, distance(texCoord, vec2(0.5, 0.5)))), min(getActive(0.5), 1.0)), color.a);
}