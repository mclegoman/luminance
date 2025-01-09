#version 150

uniform sampler2D InSampler;
uniform sampler2D OutlineSampler;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

uniform vec3 Amount;

void main(){
    vec4 diffuseTexel = texture(InSampler, texCoord);
    vec4 outlineTexel = texture(OutlineSampler, texCoord);
    fragColor = vec4(diffuseTexel.rgb + diffuseTexel.rgb * outlineTexel.rgb * Amount, 1.0);
}
