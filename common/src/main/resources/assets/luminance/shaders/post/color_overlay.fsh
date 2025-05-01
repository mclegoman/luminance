#version 150

in vec2 texCoord;
uniform sampler2D InSampler;
uniform vec4 Color;
out vec4 fragColor;

void main() {
    vec4 c = texture(InSampler, texCoord);
    fragColor = vec4(mix(c.rgb, Color.rgb, Color.a), c.a);
}