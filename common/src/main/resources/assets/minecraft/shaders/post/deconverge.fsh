#version 150

uniform sampler2D InSampler;

in vec2 texCoord;
in vec2 oneTexel;

uniform vec3 ConvergeX;
uniform vec3 ConvergeY;
uniform vec3 RadialConvergeX;
uniform vec3 RadialConvergeY;

out vec4 fragColor;

void main() {
    vec3 CoordX = (texCoord.x * RadialConvergeX) + ConvergeX * oneTexel.x - (RadialConvergeX - 1.0) * 0.5;
    vec3 CoordY = (texCoord.y * RadialConvergeY) + ConvergeY * oneTexel.y - (RadialConvergeY - 1.0) * 0.5;
    fragColor = vec4(texture(InSampler, vec2(CoordX.x, CoordY.x)).r, texture(InSampler, vec2(CoordX.y, CoordY.y)).g, texture(InSampler, vec2(CoordX.z, CoordY.z)).b, texture(InSampler, texCoord).a);
}
