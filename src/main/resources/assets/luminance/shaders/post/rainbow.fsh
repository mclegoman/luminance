#version 150

uniform sampler2D InSampler;
in vec2 texCoord;
out vec4 fragColor;

uniform float Colors;
uniform vec3 Scale;

uniform vec3 Color1;
uniform vec3 Color2;
uniform vec3 Color3;
uniform vec3 Color4;
uniform vec3 Color5;
uniform vec3 Color6;
uniform vec3 Color7;
uniform vec3 Color8;

vec3 colors[8] = vec3[](
    Color1,
    Color2,
    Color3,
    Color4,
    Color5,
    Color6,
    Color7,
    Color8
);

void main() {
    float color = mod(Scale.x*texCoord.x + Scale.y*texCoord.y + Scale.z, Colors);
    fragColor = vec4(colors[clamp(int(color), 0, 7)] * texture(InSampler, texCoord).rgb, 1.0);
}