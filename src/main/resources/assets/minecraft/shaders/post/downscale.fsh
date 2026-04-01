#version 330

uniform sampler2D InSampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform DownscaleConfig {
    float Scale;
};

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec2 oneTexel = 1.0 / InSize;
    fragColor = vec4(( texture(InSampler, texCoord).rgb + texture(InSampler, texCoord + vec2(oneTexel.x*Scale, 0.0)).rgb + texture(InSampler, texCoord + vec2(0.0, oneTexel.y*Scale)).rgb + texture(InSampler, texCoord + oneTexel*Scale).rgb) * 0.25, 1.0);
}
