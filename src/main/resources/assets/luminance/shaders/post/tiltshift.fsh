#version 330

uniform sampler2D InSampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform TiltshiftConfig {
    vec2 BlurDir;
    float Radius;
    vec2 Center;
    float Focus;
};

in vec2 texCoord;

out vec4 fragColor;

float gaussian(float x) {
    return exp(-(x * x) / (2.0 * (Radius / 3.0) * (Radius / 3.0))) / (sqrt(2.0 * 3.141592653589793) * (Radius / 3.0));
}

void main() {
    vec2 oneTexel = 1.0 / InSize;
    vec4 blurred = vec4(0.0);
    float totalStrength = 0.0;
    for(float r = -Radius; r <= Radius; r += 1.0) {
        vec4 sampleValue = texture(InSampler, texCoord + oneTexel * r * BlurDir);
        float strength = gaussian(r);
        blurred += sampleValue * strength;
        totalStrength += strength;
    }
    blurred /= totalStrength;
    fragColor = vec4(mix(texture(InSampler, texCoord).rgb, blurred.rgb, clamp((distance(texCoord, Center) * Focus), 0.0, 1.0)), 1.0);
}
