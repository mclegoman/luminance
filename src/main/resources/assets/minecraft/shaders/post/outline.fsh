#version 330

uniform sampler2D InSampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform OutlineConfig {
    vec4 Kernel;
    vec4 Distance;
    float Amount;
};

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec2 oneTexel = 1.0 / InSize;
    vec4 center = texture(InSampler, texCoord);
    vec4 up     = texture(InSampler, texCoord + vec2(        0.0, -oneTexel.y*Distance.x)) * Kernel.x;
    vec4 down   = texture(InSampler, texCoord + vec2( oneTexel.x*Distance.y,         0.0)) * Kernel.y;
    vec4 left   = texture(InSampler, texCoord + vec2(-oneTexel.x*Distance.z,         0.0)) * Kernel.z;
    vec4 right  = texture(InSampler, texCoord + vec2(        0.0,  oneTexel.y*Distance.w)) * Kernel.w;
    vec4 uDiff = center - up;
    vec4 dDiff = center - down;
    vec4 lDiff = center - left;
    vec4 rDiff = center - right;
    vec4 sum = uDiff + dDiff + lDiff + rDiff;
    vec3 clamped = clamp(center.rgb - sum.rgb * Amount, 0.0, 1.0);
    fragColor = vec4(clamped, 1.0);
}
