#version 330

uniform sampler2D InSampler;

layout(std140) uniform SixteenColorsConfig {
    vec4 ColorModulate;
    // this is inputted as 16 seperate vec3s, but the bytes can be directly interpretted as an array!
    vec3 Palette[16];
};

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec4 c = texture(InSampler, texCoord);
    float d = distance(c.rgb, Palette[0]);
    vec3 q = Palette[0];
    for (int i = 1; i < 16; i++) {
        float dtc = distance(c.rgb, Palette[i]);
        if (dtc < d) {
            d = dtc;
            q = Palette[i];
        }
    }
    fragColor = vec4(q, 1.0) * ColorModulate;
}
