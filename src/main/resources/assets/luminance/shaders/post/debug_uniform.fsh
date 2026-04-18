#version 330

// This shader can be modified as much as you like, I just wanted something to test uniforms.

uniform sampler2D InSampler;

layout(std140) uniform DebugUniformConfig {
    float Min;
    float Max;
    float Value;
    float Thickness;
};

in vec2 texCoord;

out vec4 fragColor;

void main() {
    if (texCoord.x < Thickness || texCoord.x > (1.0 - Thickness) || texCoord.y < Thickness || texCoord.y > (1.0 - Thickness)) {
        float normalized = clamp(((Value - Min) / (Max - Min)), 0.0, 1.0);
        fragColor = vec4(normalized, normalized, normalized, 1.0);
    } else {
        fragColor = texture(InSampler, texCoord);
    }
}