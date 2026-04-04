#version 330

uniform sampler2D InSampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform QuantizeConfig {
    vec3 Amount;
    vec3 RoundAt;
};

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec4 color = texture(InSampler, texCoord);
    fragColor = vec4(floor(color.rgb * Amount + RoundAt) / Amount, color.a);
}