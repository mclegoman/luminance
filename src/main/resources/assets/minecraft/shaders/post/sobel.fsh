#version 330

uniform sampler2D InSampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform SobelConfig {
    vec4 Distance;
    vec3 Mix;
    float MixAmount;
};

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec2 oneTexel = 1.0 / InSize;
    vec4 center = texture(InSampler, texCoord);
    vec4 left   = texture(InSampler, texCoord - vec2(oneTexel.x*Distance.z, 0.0)) * Mix.z;
    vec4 right  = texture(InSampler, texCoord + vec2(oneTexel.x*Distance.x, 0.0)) * Mix.x;
    vec4 up     = texture(InSampler, texCoord - vec2(0.0, oneTexel.y*Distance.y)) * Mix.y;
    vec4 down   = texture(InSampler, texCoord + vec2(0.0, oneTexel.y*Distance.w)) * ((MixAmount+3)-(Mix.x+Mix.y+Mix.z));
    vec4 leftDiff  = center - left;
    vec4 rightDiff = center - right;
    vec4 upDiff    = center - up;
    vec4 downDiff  = center - down;
    vec4 total = leftDiff + rightDiff + upDiff + downDiff;
    fragColor = vec4(total.rgb, 1.0);
}
