#version 330

uniform sampler2D InSampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform RainbowConfig {
    vec3 Scale;
    float Colors;
    // this is inputted as 8 seperate vec3s, but the bytes can be directly interpretted as an array!
    vec3[8] Palette;
};

in vec2 texCoord;

out vec4 fragColor;

void main() {
    float color = mod(Scale.x*texCoord.x + Scale.y*texCoord.y + Scale.z, Colors);
    fragColor = vec4(Palette[clamp(int(color), 0, 7)] * texture(InSampler, texCoord).rgb, 1.0);
}