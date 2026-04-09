#version 330

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform QuantizeConfig {
    vec3 Levels;
    vec4 ColorModulate;
};

out vec4 fragColor;

void main() {
    vec4 color = texture(InSampler, texCoord);
    fragColor = vec4(vec3(floor(color.r * Levels.r + 0.5) / Levels.r, floor(color.g * Levels.g + 0.5) / Levels.g, floor(color.b * Levels.b + 0.5) / Levels.b), 1.0) * ColorModulate;
}
