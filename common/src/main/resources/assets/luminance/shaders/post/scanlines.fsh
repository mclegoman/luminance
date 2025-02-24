#version 150

uniform sampler2D InSampler;

in vec2 texCoord;
out vec4 fragColor;

uniform vec2 InSize;
uniform float Amount;
uniform float Strength;

void main() {
    vec3 inputColor = texture(InSampler, texCoord).rgb;
    fragColor = vec4(inputColor - sin((texCoord.y * InSize.y) * Amount) * Strength, 1.0);
}
