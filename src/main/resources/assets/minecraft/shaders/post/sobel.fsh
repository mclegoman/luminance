#version 150

uniform sampler2D InSampler;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

uniform vec4 Kernel;
uniform vec4 Distance;

void main(){
    vec4 center = texture(InSampler, texCoord);
    vec4 left   = texture(InSampler, texCoord - vec2(oneTexel.x*Distance.x, 0.0)) * Kernel.x;
    vec4 right  = texture(InSampler, texCoord + vec2(oneTexel.x*Distance.y, 0.0)) * Kernel.y;
    vec4 up     = texture(InSampler, texCoord - vec2(0.0, oneTexel.y*Distance.z)) * Kernel.z;
    vec4 down   = texture(InSampler, texCoord + vec2(0.0, oneTexel.y*Distance.w)) * Kernel.w;
    vec4 leftDiff  = center - left;
    vec4 rightDiff = center - right;
    vec4 upDiff    = center - up;
    vec4 downDiff  = center - down;
    vec4 total = clamp(leftDiff + rightDiff + upDiff + downDiff, 0.0, 1.0);
    fragColor = vec4(total.rgb, 1.0);
}
