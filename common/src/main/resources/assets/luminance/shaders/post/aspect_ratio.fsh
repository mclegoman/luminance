#version 150

in vec2 texCoord;
in vec2 oneTexel;
out vec4 fragColor;

uniform sampler2D InSampler;
uniform vec2 AspectRatio;
uniform vec3 BorderColor;
uniform vec2 Scale;
uniform float Squish;

void main() {
    vec2 coord = texCoord - vec2(0.5);

    float ratio = (AspectRatio.x/AspectRatio.y)/(oneTexel.y/oneTexel.x);
    if (ratio > 1.0) {
        coord.y *= ratio;
    } else {
        coord.x /= ratio;
    }

    coord /= Scale;

    vec3 color;
    if (coord.x > 0.5 || coord.x < -0.5 || coord.y > 0.5 || coord.y < -0.5) {
        color = BorderColor;
    } else {
        color = texture(InSampler, mix(texCoord, coord + vec2(0.5), Squish)).rgb;
    }

    fragColor = vec4(color, 1.0);
}
