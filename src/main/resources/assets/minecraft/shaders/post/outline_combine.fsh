#version 330

uniform sampler2D InSampler;
uniform sampler2D OutlineSampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform OutlineCombineConfig {
    vec3 Base;
    vec3 Amount;
};

in vec2 texCoord;

out vec4 fragColor;

void main(){
    vec2 oneTexel = 1.0 / InSize;
    vec4 diffuseTexel = texture(InSampler, texCoord);
    vec4 outlineTexel = texture(OutlineSampler, texCoord);
    fragColor = vec4(diffuseTexel.rgb * Base + diffuseTexel.rgb * outlineTexel.rgb * Amount, 1.0);
}
