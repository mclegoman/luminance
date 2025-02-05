#version 150

uniform sampler2D InSampler;

in vec2 texCoord;
in vec2 oneTexel;

uniform vec2 InSize;

out vec4 fragColor;

uniform vec2 BlurDir;
uniform float Radius;
uniform float RadiusMultiplier;
uniform float Deviation;

uniform float Wrapping;

vec4 wrapTexture(sampler2D tex, vec2 coord) {
    return texture2D(tex, mix(coord, fract(coord), Wrapping));
}

float sigma = Radius*Deviation;
float exponent = 2.0*sigma*sigma;
float factor = 0.398942280401432/sigma;

float gaussian(float x) {
    return exp(-(x * x)/exponent)*factor;
}

void main(){
    int kernelRadius = int(max(abs(ceil(Radius * 3.0)),1));

    vec4 blurred = vec4(0.0);
    float totalStrength = 0.0;

    for (int r = -kernelRadius; r <= kernelRadius; r++) {
        vec4 sampleValue = wrapTexture(InSampler, texCoord + (r * RadiusMultiplier) * oneTexel * BlurDir);
        float gauss = gaussian(r);
        blurred += sampleValue * gauss;
        totalStrength += gauss;
    }

    fragColor = blurred / totalStrength;
}
