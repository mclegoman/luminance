#version 330

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform QuantizeConfig {
    vec3 Levels;
    vec3 RoundAt;
    vec4 ColorModulate;
};

out vec4 fragColor;

void main() {
    fragColor = vec4(floor(texture(InSampler, texCoord).rgb * Levels + RoundAt) / Levels, 1.0) * ColorModulate;
}
