#version 150

uniform sampler2D InSampler;
in vec2 texCoord;
out vec4 fragColor;
uniform float Amount;

void main() {
    float outputAmount;
    if (Amount == 0) outputAmount = 0.001;
    else outputAmount = Amount;
    fragColor = texture(InSampler, floor(texCoord / (outputAmount / textureSize(InSampler, 0)) + 0.5) * (outputAmount / textureSize(InSampler, 0)));
}