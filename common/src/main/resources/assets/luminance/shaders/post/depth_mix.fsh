#version 150

in vec2 texCoord;
in vec2 oneTexel;
uniform vec2 InSize;
uniform vec2 OutSize;

uniform sampler2D InSampler;
uniform sampler2D InDepthSampler;
uniform sampler2D MixSampler;
out vec4 fragColor;

uniform vec2 Amount;
uniform float luminance_viewDistance;

void main() {
    vec4 inputColor = texture(InSampler, texCoord);
    vec4 mixColor = texture(MixSampler, texCoord);

    vec3 outputColor = inputColor.rgb;
    float depth = min(max(1.0 - (1.0 - texture(InDepthSampler, texCoord).r) * ((luminance_viewDistance * 16) * 0.64), 0.0), 1.0);
    if (depth > Amount.x) outputColor = mix(inputColor.rgb, mixColor.rgb, smoothstep(Amount.x, Amount.y, depth));
    fragColor = vec4(outputColor, inputColor.a);
}
