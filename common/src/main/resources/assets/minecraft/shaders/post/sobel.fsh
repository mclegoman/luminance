#version 150

uniform sampler2D InSampler;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

uniform vec4 Distance;
uniform vec3 Mix;
uniform float MixAmount;

void main(){
    vec4 center = texture(InSampler, texCoord);
    vec4 left   = texture(InSampler, texCoord - vec2(oneTexel.x*Distance.z, 0.0)) * Mix.z;
    vec4 right  = texture(InSampler, texCoord + vec2(oneTexel.x*Distance.x, 0.0)) * Mix.x;
    vec4 up     = texture(InSampler, texCoord - vec2(0.0, oneTexel.y*Distance.y)) * Mix.y;
    vec4 down   = texture(InSampler, texCoord + vec2(0.0, oneTexel.y*Distance.w)) * ((MixAmount+3)-(Mix.x+Mix.y+Mix.z));
    vec4 leftDiff  = center - left;
    vec4 rightDiff = center - right;
    vec4 upDiff    = center - up;
    vec4 downDiff  = center - down;
    vec4 total = leftDiff + rightDiff + upDiff + downDiff;
    fragColor = vec4(total.rgb, 1.0);
}
