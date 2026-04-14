#version 330

uniform sampler2D InSampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform NtscEncodeConfig {
    float Tau;
    vec4 A2;
    vec4 B;
    float P;
    float CCFrequency;
    float ScanTime;
    vec4[3] YIQTransform;
    vec4 MinC;
    vec4 InvCRange;
};

in vec2 texCoord;

out vec4 fragColor;

float getTauScanTime() {
    return Tau * ScanTime;
}

void main() {
    vec2 oneTexel = 1.0 / InSize;
    vec2 InverseP = vec2(P, 0.0) * oneTexel;
    
    // UVs for four linearly-interpolated samples spread 0.25 texels apart
    vec2 C0 = texCoord;
    vec2 C1 = texCoord + InverseP * 0.25;
    vec2 C2 = texCoord + InverseP * 0.50;
    vec2 C3 = texCoord + InverseP * 0.75;
    vec4 Cx = vec4(C0.x, C1.x, C2.x, C3.x);
    vec4 Cy = vec4(C0.y, C1.y, C2.y, C3.y);
    
    vec4 Texel0 = texture(InSampler, C0);
    vec4 Texel1 = texture(InSampler, C1);
    vec4 Texel2 = texture(InSampler, C2);
    vec4 Texel3 = texture(InSampler, C3);

    // Calculate the expected time of the sample.
    vec4 T = A2 * Cy * vec4(InSize.y) + B + Cx;
    vec4 W = vec4(getTauScanTime() * CCFrequency);
    vec4 TW = T * W;
    vec4 Y = vec4(dot(Texel0, YIQTransform[0]), dot(Texel1, YIQTransform[0]), dot(Texel2, YIQTransform[0]), dot(Texel3, YIQTransform[0]));
    vec4 I = vec4(dot(Texel0, YIQTransform[1]), dot(Texel1, YIQTransform[1]), dot(Texel2, YIQTransform[1]), dot(Texel3, YIQTransform[1]));
    vec4 Q = vec4(dot(Texel0, YIQTransform[2]), dot(Texel1, YIQTransform[2]), dot(Texel2, YIQTransform[2]), dot(Texel3, YIQTransform[2]));
    
    vec4 Encoded = Y + I * cos(TW) + Q * sin(TW);
    fragColor = (Encoded - MinC) * InvCRange;
}
