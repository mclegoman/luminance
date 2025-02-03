#version 150

uniform sampler2D InSampler;
in vec2 texCoord;
out vec4 fragColor;

uniform vec3 Amount;

float quantize(float value, float amount) {
    return floor(value * amount + 0.5) / amount;
}

void main() {
    vec4 color = texture(InSampler, texCoord);
    fragColor = vec4(quantize(color.r, Amount.r), quantize(color.g, Amount.g), quantize(color.b, Amount.b), color.a);
}