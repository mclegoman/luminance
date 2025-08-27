#version 150

in vec2 texCoord;
in vec2 oneTexel;
out vec4 fragColor;

uniform sampler2D InSampler;
uniform vec3 SepiaR;
uniform vec3 SepiaG;
uniform vec3 SepiaB;
uniform float Intensity;

void main() {
    vec4 color = texture(InSampler, texCoord);
    fragColor = vec4(mix(color.rgb, vec3(color.r * SepiaR.r + color.g * SepiaR.g + color.b * SepiaR.b, color.r * SepiaG.r + color.g * SepiaG.g + color.b * SepiaG.b, color.r * SepiaB.r + color.g * SepiaB.g + color.b * SepiaB.b), Intensity), color.a);
}
