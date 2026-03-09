#version 150

uniform sampler2D InSampler;
in vec2 texCoord;
out vec4 fragColor;

uniform vec3 Amount;
uniform vec3 RoundAt;

void main() {
    vec4 color = texture(InSampler, texCoord);
    fragColor = vec4(floor(color.rgb * Amount + RoundAt) / Amount, color.a);
}