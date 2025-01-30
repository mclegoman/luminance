#version 150

uniform sampler2D InSampler;
uniform sampler2D PrevSampler;

in vec2 texCoord;
in vec2 oneTexel;

uniform vec2 InSize;

uniform vec3 Phosphor;
uniform vec2 Offset;

out vec4 fragColor;

void main() {
    vec4 CurrTexel = texture(InSampler, texCoord);
    vec4 PrevTexel = texture(PrevSampler, texCoord + Offset*oneTexel);

    fragColor = vec4(max(PrevTexel.rgb * Phosphor, CurrTexel.rgb), 1.0);
}
