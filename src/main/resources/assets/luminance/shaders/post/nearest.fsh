#version 150

uniform sampler2D InSampler;

in vec2 texCoord;
out vec4 fragColor;

uniform vec3 Color1;
uniform vec3 Color2;
uniform vec3 Color3;
uniform vec3 Color4;
uniform vec4 ColorModulate;

vec3 palette[4] = vec3[4](
    Color1,
    Color2,
    Color3,
    Color4
);

void main() {
    vec3 c = texture(InSampler, texCoord).rgb;
    float d = distance(c, palette[0]);
    vec3 q = palette[0];
    for (int i = 1; i < 4; i++) {
        float dtc = distance(c, palette[i]);
        if (dtc < d) {
            d = dtc;
            q = palette[i];
        }
    }
    fragColor = vec4(q * ColorModulate.rgb, 1.0);
}
