#version 330

uniform sampler2D InSampler;
uniform sampler2D OverlaySampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
    vec2 OverlaySize;
};

layout(std140) uniform OverlayConfig {
    vec2 InSize;
    float Alpha;
    float Speed;
    float Time;
    float XAmount;
    float YAmount;
};

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec4 inColor = texture(InSampler, texCoord);
    vec4 overlayColor = texture(OverlaySampler, fract(vec2(texCoord.x, -texCoord.y) + (vec2(XAmount, YAmount) * ((vec2(Time * InSize.x, Time * InSize.y) * Speed)))));
    fragColor = vec4(mix(inColor.rgb, mix(inColor.rgb, overlayColor.rgb, overlayColor.a), Alpha), 1.0);
}
