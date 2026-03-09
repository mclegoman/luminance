#version 150

uniform sampler2D InSampler;
uniform float ThresholdBrightness;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec4 color = texture(InSampler, texCoord);
    // https://en.wikipedia.org/wiki/Luma_(video)
    float luminance = dot(vec3(0.2126, 0.7152, 0.0722), color.rgb);
    if (luminance > ThresholdBrightness) {
        fragColor = vec4(1.0);
    } else {
        fragColor = vec4(0.0);
    }
}
