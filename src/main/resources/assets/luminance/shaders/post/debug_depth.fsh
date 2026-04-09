#version 330

uniform sampler2D InDepthSampler;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    float depth = texture(InDepthSampler, texCoord).r;
    fragColor = vec4(vec3(depth * depth), 1.0);
}
