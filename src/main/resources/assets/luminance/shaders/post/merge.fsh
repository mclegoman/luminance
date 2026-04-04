#version 330

uniform sampler2D InSampler;
uniform sampler2D MergeSampler;

layout(std140) uniform MergeConfig {
    float Alpha;
};

in vec2 texCoord;

out vec4 fragColor;

void main(){
    vec4 color = mix(texture(MergeSampler, texCoord), texture(InSampler, texCoord), Alpha);
    fragColor = vec4(color.rgb, 1.0);
}
