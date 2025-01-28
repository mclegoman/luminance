#version 150

uniform sampler2D InSampler;
uniform sampler2D OverlaySampler;

in vec2 texCoord;
out vec4 fragColor;

uniform vec2 InSize;
uniform float Alpha;
uniform float Speed;
uniform float luminance_time;
uniform float XAmount;
uniform float YAmount;

void main() {
    vec4 inColor = texture(InSampler, texCoord);
    vec4 overlayColor = texture(OverlaySampler, fract(vec2(texCoord.x, -texCoord.y) + (vec2(XAmount, YAmount) * ((vec2(luminance_time * InSize.x, luminance_time * InSize.y) * Speed)))));
    fragColor = vec4(mix(inColor.rgb, mix(inColor.rgb, overlayColor.rgb, overlayColor.a), Alpha), 1.0);
}
