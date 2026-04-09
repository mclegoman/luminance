#version 330

uniform sampler2D InSampler;

layout(std140) uniform PixelatedConfig {
    uniform vec2 Amount;
    uniform float AmountMultiplier;
    uniform vec2 Offset;
};

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec2 outputAmount = Amount * AmountMultiplier;
    if (outputAmount.x == 0) outputAmount.x = 0.001;
    if (outputAmount.y == 0) outputAmount.y = 0.001;
    fragColor = texture(InSampler, floor(texCoord / (outputAmount / textureSize(InSampler, 0)) + Offset) * (outputAmount / textureSize(InSampler, 0)));
}