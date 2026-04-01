#version 330

uniform sampler2D InSampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform DeconvergeConfig {
    vec3 ConvergeX;
    vec3 ConvergeY;
    vec3 RadialConvergeX;
    vec3 RadialConvergeY;
};

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec2 oneTexel = 1.0 / InSize;
    vec3 CoordX = (texCoord.x * RadialConvergeX) + ConvergeX * oneTexel.x - (RadialConvergeX - 1.0) * 0.5;
    vec3 CoordY = (texCoord.y * RadialConvergeY) + ConvergeY * oneTexel.y - (RadialConvergeY - 1.0) * 0.5;
    fragColor = vec4(texture(InSampler, vec2(CoordX.x, CoordY.x)).r, texture(InSampler, vec2(CoordX.y, CoordY.y)).g, texture(InSampler, vec2(CoordX.z, CoordY.z)).b, texture(InSampler, texCoord).a);
}
