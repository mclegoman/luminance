#version 150

uniform sampler2D InSampler;
in vec2 texCoord;
out vec4 fragColor;
uniform float Amount;

void main() {
    fragColor = texture(InSampler, floor(texCoord / (Amount / textureSize(InSampler, 0)) + 0.5) * (Amount / textureSize(InSampler, 0)));
}