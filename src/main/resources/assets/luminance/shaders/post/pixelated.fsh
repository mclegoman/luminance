#version 150

uniform sampler2D InSampler;
in vec2 texCoord;
out vec4 fragColor;
uniform vec2 Amount;
uniform float AmountMultiplier;
uniform vec2 Offset;

void main() {
    vec2 outputAmount = Amount * AmountMultiplier;
    if (outputAmount.x == 0) outputAmount.x = 0.001;
    if (outputAmount.y == 0) outputAmount.y = 0.001;
    fragColor = texture(InSampler, floor(texCoord / (outputAmount / textureSize(InSampler, 0)) + Offset) * (outputAmount / textureSize(InSampler, 0)));
}