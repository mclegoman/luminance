#version 330

uniform sampler2D InSampler;
uniform sampler2D OutlineSampler;

layout(std140) uniform OutlineCombineConfig {
    vec3 Base;
    vec3 Amount;
};

in vec2 texCoord;

out vec4 fragColor;

void main(){
    vec4 diffuseTexel = texture(InSampler, texCoord);
    vec4 outlineTexel = texture(OutlineSampler, texCoord);
    fragColor = vec4(diffuseTexel.rgb * Base + diffuseTexel.rgb * outlineTexel.rgb * Amount, 1.0);
}
