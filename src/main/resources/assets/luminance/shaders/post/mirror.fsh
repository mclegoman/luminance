#version 150

in vec2 texCoord;
in vec2 oneTexel;
uniform vec2 InSize;
uniform vec2 OutSize;

uniform sampler2D InSampler;
uniform sampler2D InDepthSampler;
out vec4 fragColor;
uniform float luminance_viewDistance;

void main() {
    vec4 inputColor = texture(InSampler, texCoord);
    vec2 uv = texCoord.xy;
    uv *= InSize;
    uv.x = InSize.x - uv.x;
    uv /= InSize;
    vec4 color = texture(InSampler, uv);
    float depth = min(max(1.0 - (1.0 - texture(InDepthSampler, texCoord).r) * ((luminance_viewDistance * 16) * 0.64), 0.0), 1.0);
    vec3 outputColor = inputColor.rgb;
    if (depth > 0.9) outputColor = mix(inputColor.rgb, color.rgb, smoothstep(0.9, 0.91, depth));
    fragColor = vec4(outputColor, inputColor.a);
}
