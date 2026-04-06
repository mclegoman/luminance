#version 330

uniform sampler2D InDepthSampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InDepthSize;
};

in vec2 texCoord;

out vec4 fragColor;

void main() {
    float depth = texture(InDepthSampler, texCoord).r;
    fragColor = vec4(vec3(depth * depth), 1.0);
}
