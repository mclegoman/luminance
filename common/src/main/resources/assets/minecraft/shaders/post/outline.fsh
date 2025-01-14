#version 150

uniform sampler2D InSampler;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

uniform vec4 Kernel;
uniform vec4 Distance;
uniform float Amount;

void main(){
    vec4 center = texture(InSampler, texCoord);
    vec4 up     = texture(InSampler, texCoord + vec2(        0.0, -oneTexel.y*Distance.x)) * Kernel.x;
    vec4 down   = texture(InSampler, texCoord + vec2( oneTexel.x*Distance.y,         0.0)) * Kernel.y;
    vec4 left   = texture(InSampler, texCoord + vec2(-oneTexel.x*Distance.z,         0.0)) * Kernel.z;
    vec4 right  = texture(InSampler, texCoord + vec2(        0.0,  oneTexel.y*Distance.w)) * Kernel.w;
    vec4 uDiff = center - up;
    vec4 dDiff = center - down;
    vec4 lDiff = center - left;
    vec4 rDiff = center - right;
    vec4 sum = uDiff + dDiff + lDiff + rDiff;
    vec3 clamped = clamp(center.rgb - sum.rgb * Amount, 0.0, 1.0);
    fragColor = vec4(clamped, 1.0);
}
