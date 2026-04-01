#version 330

uniform sampler2D InSampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform AntialiasConfig {
    vec2 Distance;
};

in vec2 texCoord;

out vec4 fragColor;

void main(){
    vec2 oneTexel = Distance / InSize;
    vec4 inColor = texture(InSampler, texCoord);
    fragColor = vec4(
        mix(
            mix(
                mix(
                    inColor,
                    mix(
                        texture(InSampler, texCoord + vec2(-oneTexel.x, 0.0)),
                        texture(InSampler, texCoord + vec2(-oneTexel.x * 2.0, 0.0)),
                        0.667
                    ),
                    0.75
                ),
                mix(
                    inColor,
                    mix(
                        texture(InSampler, texCoord + vec2(oneTexel.x, 0.0)),
                        texture(InSampler, texCoord + vec2(oneTexel.x * 2.0, 0.0)),
                        0.667
                    ),
                    0.75
                ),
                0.5
            ),
            mix(
                mix(
                    inColor,
                    mix(
                        texture(InSampler, texCoord + vec2(0.0, -oneTexel.y)),
                        texture(InSampler, texCoord + vec2(0.0, -oneTexel.y * 2.0)),
                        0.667
                    ),
                    0.75
                ),
                mix(
                    inColor,
                    mix(
                        texture(InSampler, texCoord + vec2(0.0, oneTexel.y)),
                        texture(InSampler, texCoord + vec2(0.0, oneTexel.y * 2.0)),
                        0.667
                    ),
                    0.75
                ),
                0.5
            ),
            0.5
        ).rgb,
        1.0
    );
}
