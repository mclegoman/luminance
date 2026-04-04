// TODO: any usage of this can instead use minecraft:flip
#version 330

uniform sampler2D InSampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec2 uv = texCoord.xy;
    uv *= InSize;
    uv.x = InSize.x - uv.x;
    uv /= InSize;
    fragColor = texture(InSampler, uv);
}
