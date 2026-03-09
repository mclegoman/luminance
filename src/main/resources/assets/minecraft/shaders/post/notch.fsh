#version 150

uniform sampler2D InSampler;
uniform sampler2D DitherSampler;

in vec2 texCoord;

uniform vec2 InSize;

out vec4 fragColor;

uniform vec3 Mix;
uniform float Scale;

void main() {
    vec2 halfSize = InSize/Scale;

    vec2 steppedCoord = texCoord;
    steppedCoord.x = float(int(steppedCoord.x*halfSize.x)) / halfSize.x;
    steppedCoord.y = float(int(steppedCoord.y*halfSize.y)) / halfSize.y;

    vec4 noise = texture(DitherSampler, steppedCoord * halfSize / 4.0);
    vec4 col = texture(InSampler, mix(texCoord, steppedCoord, Mix.g)) + (noise * vec4(1.0/12.0, 1.0/12.0, 1.0/6.0, 1.0) * Mix.b);
    float r = float(int(col.r*8.0))/8.0;
    float g = float(int(col.g*8.0))/8.0;
    float b = float(int(col.b*4.0))/4.0;
    fragColor = mix(col, vec4(r, g, b, 1.0), Mix.r);
}
