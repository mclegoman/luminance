#version 330

uniform sampler2D InSampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform SharewareConfig {
    float ColorResolution;
    float Saturation;
    float Scale;
    vec3 Gray;
};

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec2 scaleFactors = InSize / Scale;
    vec2 truncPos = floor(texCoord * scaleFactors) / scaleFactors;
    vec4 baseTexel = texture(InSampler, truncPos);
    vec3 truncTexel = floor(baseTexel.rgb * ColorResolution) / ColorResolution;

    float luma = dot(truncTexel, Gray);
    vec3 chroma = (truncTexel - luma) * Saturation;
    fragColor = vec4(luma + chroma, 1.0);
}