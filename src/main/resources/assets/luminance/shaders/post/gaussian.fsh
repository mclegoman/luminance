#version 330

uniform sampler2D InSampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform GaussianConfig {
    vec2 BlurDir;
    float Radius;
    float Deviation;
    float Min;
    float Wrapping;
};

in vec2 texCoord;

out vec4 fragColor;


vec4 wrapTexture(sampler2D tex, vec2 coord) {
    return texture(tex, mix(coord, fract(coord), Wrapping));
}

float sigma = max(abs(Radius*Deviation),0.01);
float exponent = 2.0*sigma*sigma;
float factor = 0.398942280401432/sigma;

float gaussian(float x) {
    return exp(-(x * x)/exponent)*factor;
}

void main(){
    int kernelRadius = int(max(abs(ceil(Radius * 3.0)),1));
    vec2 oneTexel = 1.0 / InSize;

    vec4 blurred = vec4(0.0);
    float totalStrength = 0.0;

    for (int r = int(-kernelRadius*clamp(Min,0,1)); r <= kernelRadius; r++) {
        //                                        should this v also be multiplied by moj globals MenuBlurRadius? prob not
        vec4 sampleValue = wrapTexture(InSampler, texCoord + (r) * oneTexel * BlurDir);
        float gauss = gaussian(r);
        blurred += sampleValue * gauss;
        totalStrength += gauss;
    }

    fragColor = blurred / totalStrength;
}
