#version 330

uniform sampler2D InSampler;

layout(std140) uniform SepiaConfig {
    vec3 SepiaR;
    vec3 SepiaG;
    vec3 SepiaB;
    float Intensity;
};

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec4 color = texture(InSampler, texCoord);
    fragColor = vec4(mix(color.rgb, vec3(color.r * SepiaR.r + color.g * SepiaR.g + color.b * SepiaR.b, color.r * SepiaG.r + color.g * SepiaG.g + color.b * SepiaG.b, color.r * SepiaB.r + color.g * SepiaB.g + color.b * SepiaB.b), Intensity), color.a);
}
