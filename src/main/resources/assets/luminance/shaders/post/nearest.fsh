#version 330

uniform sampler2D InSampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform NearestConfig {
    // this is inputted as 4 seperate vec3s, but the bytes can be directly interpretted as an array!
    uniform vec3[4] Palette;
    uniform vec4 ColorModulate;
};

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec3 c = texture(InSampler, texCoord).rgb;
    float d = distance(c, Palette[0]);
    vec3 q = Palette[0];
    for (int i = 1; i < 4; i++) {
        float dtc = distance(c, Palette[i]);
        if (dtc < d) {
            d = dtc;
            q = Palette[i];
        }
    }
    fragColor = vec4(q * ColorModulate.rgb, 1.0);
}
