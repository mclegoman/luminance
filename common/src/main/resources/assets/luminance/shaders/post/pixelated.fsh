#version 150

uniform sampler2D InSampler;
in vec2 texCoord;
out vec4 fragColor;
uniform float Amount;
uniform float AmountMultiplier;

void main() {
    float outputAmount = Amount * AmountMultiplier;
    if (outputAmount == 0) outputAmount = 0.001;
    fragColor = texture(InSampler, floor(texCoord / (outputAmount / textureSize(InSampler, 0)) + 0.5) * (outputAmount / textureSize(InSampler, 0)));
}