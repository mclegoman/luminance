#version 330

uniform sampler2D InSampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform AspectRatioConfig {
    sampler2D InSampler;
    vec2 AspectRatio;
    vec3 BorderColor;
    vec2 Scale;
    float Squish;
};

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec2 oneTexel = 1.0 / InSize;
    vec2 coord = texCoord - vec2(0.5);

    float ratio = (AspectRatio.x/AspectRatio.y)/(oneTexel.y/oneTexel.x);
    if (ratio > 1.0) {
        coord.y *= ratio;
    } else {
        coord.x /= ratio;
    }

    coord /= Scale;

    vec3 color;
    if (coord.x > 0.5 || coord.x < -0.5 || coord.y > 0.5 || coord.y < -0.5) {
        color = BorderColor;
    } else {
        color = texture(InSampler, mix(texCoord, coord + vec2(0.5), Squish)).rgb;
    }

    fragColor = vec4(color, 1.0);
}
