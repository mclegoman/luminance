#version 150

uniform sampler2D InSampler;

in vec2 texCoord;
out vec4 fragColor;

uniform vec4 ColorModulate;

const vec4 palette[16] = vec4[16](
    vec4(0.0, 0.0, 0.0, 1.0),
    vec4(0.5, 0.0, 0.0, 1.0),
    vec4(0.0, 0.5, 0.0, 1.0),
    vec4(0.5, 0.5, 0.0, 1.0),
    vec4(0.0, 0.0, 0.5, 1.0),
    vec4(0.5, 0.0, 0.5, 1.0),
    vec4(0.0, 0.5, 0.5, 1.0),
    vec4(0.75, 0.75, 0.75, 1.0),
    vec4(0.5, 0.5, 0.5, 1.0),
    vec4(1.0, 0.0, 0.0, 1.0),
    vec4(0.0, 1.0, 0.0, 1.0),
    vec4(1.0, 1.0, 0.0, 1.0),
    vec4(0.0, 0.0, 1.0, 1.0),
    vec4(1.0, 0.0, 1.0, 1.0),
    vec4(0.0, 1.0, 1.0, 1.0),
    vec4(1.0, 1.0, 1.0, 1.0)
);

void main() {
    vec4 c = texture(InSampler, texCoord);
    float d = distance(c, palette[0]);
    vec4 q = palette[0];
    for (int i = 1; i < 16; i++) {
        float dtc = distance(c, palette[i]);
        if (dtc < d) {
            d = dtc;
            q = palette[i];
        }
    }
    fragColor = vec4((q * ColorModulate).rgb, 1.0);
}
