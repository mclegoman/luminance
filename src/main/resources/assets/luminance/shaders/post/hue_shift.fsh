#version 330

uniform sampler2D InSampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform HueShiftConfig {
    vec3 Amount;
};

in vec2 texCoord;

out vec4 fragColor;

vec3 shift(vec3 color, vec3 shift) {
    const vec3 v = vec3(0.57735);
    vec3 proj = v * dot(v, color);
    vec3 rem = color - proj;
    vec3 rot = cross(v, rem);
    return clamp(rem * cos(shift * 6.2832) + rot * sin(shift * 6.2832) + proj, 0.0, 1.0);
}

void main() {
    vec4 color = texture(InSampler, texCoord);
    fragColor = vec4(shift(color.rgb, Amount), color.a);
}