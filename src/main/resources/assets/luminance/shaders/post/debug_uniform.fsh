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
    vec4 color = texture(InSampler, texCoord);

    if (texCoord.x < Thickness || texCoord.x > 1.0 - Thickness || texCoord.y < Thickness || texCoord.y > 1.0 - Thickness) {
        color.rgb = vec3(clamp(((Value - Min) / (Max - Min)), 0.0, 1.0));
    }

    fragColor = color;
}