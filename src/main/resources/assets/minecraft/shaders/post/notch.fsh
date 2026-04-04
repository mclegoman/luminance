#version 330

uniform sampler2D InSampler;
uniform sampler2D DitherSampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
    vec2 DitherSize;
};

layout(std140) uniform NotchConfig {
    vec3 Mix;
    float Scale;
};

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec2 halfSize = InSize/Scale;

    vec2 steppedCoord = texCoord;
    steppedCoord.x = float(int(steppedCoord.x*halfSize.x)) / halfSize.x;
    steppedCoord.y = float(int(steppedCoord.y*halfSize.y)) / halfSize.y;

    vec4 noise = texture(DitherSampler, fract(steppedCoord * halfSize / DitherSize));
    vec4 col = texture(InSampler, mix(texCoord, steppedCoord, Mix.g)) + (noise * vec4(1.0/12.0, 1.0/12.0, 1.0/6.0, 1.0) * Mix.b);
    float r = float(int(col.r*8.0))/8.0;
    float g = float(int(col.g*8.0))/8.0;
    float b = float(int(col.b*4.0))/4.0;
    fragColor = mix(col, vec4(r, g, b, 1.0), Mix.r);
}
