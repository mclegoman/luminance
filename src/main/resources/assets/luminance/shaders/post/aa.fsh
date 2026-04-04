#version 330

uniform sampler2D InSampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform AaConfig {
    vec3 ColorDiff;
    vec4 SampleDistance;
    vec4 Weights;
    float Bias;
};

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec2 oneTexel = 1.0 / InSize;
    vec2 aTexel = oneTexel*SampleDistance.xy;
    vec2 bTexel = oneTexel*SampleDistance.z;
    vec2 cTexel = oneTexel*SampleDistance.w;
    vec3 tl = texture(InSampler, texCoord + vec2(-bTexel.x, -bTexel.y)).xyz;
    vec3 tc = texture(InSampler, texCoord + vec2(0.0,       -aTexel.y)).xyz;
    vec3 tr = texture(InSampler, texCoord + vec2( cTexel.x, -cTexel.y)).xyz;
    vec3 ml = texture(InSampler, texCoord + vec2(-aTexel.x,  0.0    )).xyz;
    vec3 mc = texture(InSampler, texCoord + vec2(0.0,        0.0    )).xyz;
    vec3 mr = texture(InSampler, texCoord + vec2( aTexel.x,  0.0    )).xyz;
    vec3 bl = texture(InSampler, texCoord + vec2(-cTexel.x,  cTexel.y)).xyz;
    vec3 bc = texture(InSampler, texCoord + vec2( 0.0,       aTexel.y)).xyz;
    vec3 br = texture(InSampler, texCoord + vec2( bTexel.x,  bTexel.y)).xyz;

    float bias = 1.0/Bias;
    float di1Dist = dot(abs(tl - br), ColorDiff) + bias;
    float di2Dist = dot(abs(tr - bl), ColorDiff) + bias;
    float horDist = dot(abs(ml - mr), ColorDiff) + bias;
    float verDist = dot(abs(tc - bc), ColorDiff) + bias;
    float weight1 = Weights.x * (horDist + verDist);
    float weight2 = Weights.y * (di1Dist + di2Dist);

    vec3 blend1 = (horDist * (tc + bc) + verDist * (ml + mr) + weight1 * mc) / (Weights.z * (horDist + verDist));
    vec3 blend2 = (di1Dist * (tr + bl) + di2Dist * (tl + br) + weight2 * mc) / (Weights.w * (di1Dist + di2Dist));

    weight1 = dot(abs(blend1 - mc), ColorDiff) + bias;
    weight2 = dot(abs(blend2 - mc), ColorDiff) + bias;

    fragColor = vec4((weight1 * blend2 + weight2 * blend1) / (weight1 + weight2), 1.0);
}