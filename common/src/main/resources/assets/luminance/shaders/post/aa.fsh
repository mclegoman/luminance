#version 150

uniform sampler2D InSampler;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

uniform vec3 ColorDiff;
uniform vec2 SampleDistance;
uniform vec4 Weights;
uniform float Bias;

void main() {
    vec2 texel = oneTexel*SampleDistance;
    vec3 tl = texture(InSampler, texCoord + vec2(-texel.x, -texel.y)).xyz;
    vec3 tc = texture(InSampler, texCoord + vec2(0.0,      -texel.y)).xyz;
    vec3 tr = texture(InSampler, texCoord + vec2( texel.x, -texel.y)).xyz;
    vec3 ml = texture(InSampler, texCoord + vec2(-texel.x,  0.0    )).xyz;
    vec3 mc = texture(InSampler, texCoord + vec2(0.0,       0.0    )).xyz;
    vec3 mr = texture(InSampler, texCoord + vec2( texel.x,  0.0    )).xyz;
    vec3 bl = texture(InSampler, texCoord + vec2(-texel.x,  texel.y)).xyz;
    vec3 bc = texture(InSampler, texCoord + vec2( 0.0,      texel.y)).xyz;
    vec3 br = texture(InSampler, texCoord + vec2( texel.x,  texel.y)).xyz;

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