#version 150

in vec2 texCoord;
in vec2 oneTexel;
uniform vec2 InSize;
uniform vec2 OutSize;

uniform sampler2D InSampler;
uniform sampler2D InDepthSampler;
out vec4 fragColor;

void main() {
    vec4 inputColor = texture(InSampler, texCoord);
    vec2 uv = texCoord.xy;
    uv *= InSize;
    uv.x = InSize.x - uv.x;
    uv /= InSize;
    fragColor = vec4(texture(InSampler, uv).rgb, inputColor.a);
}
