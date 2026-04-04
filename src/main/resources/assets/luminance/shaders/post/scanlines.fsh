#version 330

uniform sampler2D InSampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform ScanlinesConfig {
    float Amount;
    float Strength;
};

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec3 inputColor = texture(InSampler, texCoord).rgb;
    fragColor = vec4(inputColor - sin((texCoord.y * InSize.y) * Amount) * Strength, 1.0);
}
