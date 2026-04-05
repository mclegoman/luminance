#version 330

uniform sampler2D InDepthSampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InDepthSize;
};

in vec2 texCoord;

out vec4 fragColor;

void main() {
    fragColor = vec4(vec3(texture(InDepthSampler, texCoord).r), 1.0);
}
