#version 330

uniform sampler2D InSampler;
uniform sampler2D OverlaySampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform OverlayConfig {
    float MosaicSize;
    vec3 RedMatrix;
    vec3 GreenMatrix;
    vec3 BlueMatrix;
};

in vec2 texCoord;

out vec4 fragColor;

void main(){
    vec2 mosaicInSize = InSize / MosaicSize;
    vec2 fractPix = fract(texCoord * mosaicInSize) / mosaicInSize;

    vec4 baseTexel = texture(InSampler, texCoord - fractPix);
    float red = dot(baseTexel.rgb, RedMatrix);
    float green = dot(baseTexel.rgb, GreenMatrix);
    float blue = dot(baseTexel.rgb, BlueMatrix);

    vec4 overlayTexel = texture(OverlaySampler, vec2(texCoord.x, 1.0 - texCoord.y));
    overlayTexel.a = 1.0;
    fragColor = mix(vec4(red, green, blue, 1.0), overlayTexel, overlayTexel.a);
}
