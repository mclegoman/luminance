#version 150

uniform sampler2D InSampler;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

void main(){
    vec4 inColor = texture(InSampler, texCoord);
    fragColor = vec4(mix(mix(mix(inColor, mix(texture(InSampler, texCoord + vec2(-oneTexel.x, 0.0)), texture(InSampler, texCoord + vec2(-oneTexel.x * 2.0, 0.0)), 0.667), 0.75), mix(inColor, mix(texture(InSampler, texCoord + vec2(oneTexel.x, 0.0)), texture(InSampler, texCoord + vec2(oneTexel.x * 2.0, 0.0)), 0.667), 0.75), 0.5), mix(mix(inColor, mix(texture(InSampler, texCoord + vec2(0.0, -oneTexel.y)), texture(InSampler, texCoord + vec2(0.0, -oneTexel.y * 2.0)), 0.667), 0.75), mix(inColor, mix(texture(InSampler, texCoord + vec2(0.0, oneTexel.y)), texture(InSampler, texCoord + vec2(0.0, oneTexel.y * 2.0)), 0.667), 0.75), 0.5), 0.5).rgb, 1.0);
}
