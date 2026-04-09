#version 330

uniform sampler2D InSampler;

layout(std140) uniform ColorOverlayConfig {
    vec4 Color;
};

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec4 c = texture(InSampler, texCoord);
    fragColor = vec4(mix(c.rgb, Color.rgb, Color.a), c.a);
}