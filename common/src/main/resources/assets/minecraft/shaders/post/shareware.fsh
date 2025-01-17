#version 150

uniform sampler2D InSampler;

in vec2 texCoord;
in vec2 oneTexel;

uniform vec2 InSize;

uniform float ColorResolution;
uniform float Saturation;

out vec4 fragColor;

uniform float Scale;
uniform vec3 Gray;

void main() {
    vec2 scaleFactors = InSize / Scale;
    vec2 truncPos = floor(texCoord * scaleFactors) / scaleFactors;
    vec4 baseTexel = texture2D(InSampler, truncPos);
    vec3 truncTexel = floor(baseTexel.rgb * ColorResolution) / ColorResolution;

    float luma = dot(truncTexel, Gray);
    vec3 chroma = (truncTexel - luma) * Saturation;
    fragColor = vec4(luma + chroma, 1.0);
}