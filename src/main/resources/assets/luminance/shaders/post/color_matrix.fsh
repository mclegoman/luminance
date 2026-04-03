#version 330

uniform sampler2D InSampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform ColorConfig {
    vec3 RedMatrix;
    vec3 GreenMatrix;
    vec3 BlueMatrix;
    vec3 Offset;
};

layout(std140) uniform SaturationConfig {
    vec3 Gray;
    float Saturation;
};

in vec2 texCoord;

out vec4 fragColor;

void main() {
    // more powerful version of minecraft:color_convolve, more closely matching the old 1.21.4 version

    vec4 InTexel = texture(InSampler, texCoord);

    // Color Matrix
    float RedValue = dot(InTexel.rgb, RedMatrix);
    float GreenValue = dot(InTexel.rgb, GreenMatrix);
    float BlueValue = dot(InTexel.rgb, BlueMatrix);
    vec3 OutColor = vec3(RedValue, GreenValue, BlueValue) + Offset;

    // Saturation
    float Luma = dot(OutColor, Gray);
    vec3 Chroma = OutColor - Luma;
    OutColor = (Chroma * Saturation) + Luma;

    fragColor = vec4(OutColor, 1.0);
}