#version 150

uniform sampler2D InSampler;
uniform sampler2D MergeSampler;

uniform float luminance_alpha_smooth;

in vec2 texCoord;

out vec4 fragColor;

void main(){
    vec4 color = mix(texture(MergeSampler, texCoord), texture(InSampler, texCoord), luminance_alpha_smooth);
    fragColor = vec4(color.rgb, 1.0);
}
