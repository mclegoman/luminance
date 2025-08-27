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
    vec3 mixColor = texture(MixSampler, texCoord).rgb;

    float depth = clamp(1.0 - (1.0 - texture(InDepthSampler, texCoord).r) * ((luminance_viewDistance * 16) * 0.64), 0.0, 1.0);

    vec3 outputColor = inputColor.rgb;
    vec2 amount;
    if (Amount.x < Amount.y) {
        amount = Amount;
    } else {
        amount = Amount.yx;
        inputColor.rgb = mixColor;
        mixColor = outputColor;
        outputColor = inputColor.rgb;
    }

    if (depth > amount.x) outputColor = mix(inputColor.rgb, mixColor, smoothstep(amount.x, amount.y, depth));

    fragColor = vec4(outputColor, inputColor.a);
}
