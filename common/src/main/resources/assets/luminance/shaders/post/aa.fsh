#version 150

uniform sampler2D InSampler;
in vec2 texCoord;
uniform vec2 InSize;
out vec4 fragColor;

uniform vec3 ColorDiff;

void main() {
    vec2 texel = 1.0 / InSize;
    vec2 offset[9] = vec2[](
        vec2(-texel.x, -texel.y),
        vec2(0.0, -texel.y),
        vec2(texel.x, -texel.y),
        vec2(-texel.x, 0.0),
        vec2(0.0, 0.0),
        vec2(texel.x, 0.0),
        vec2(-texel.x, texel.y),
        vec2(0.0, texel.y),
        vec2(texel.x, texel.y)
    );
    vec3 tl = texture(InSampler, texCoord + offset[0]).xyz;
    vec3 tc = texture(InSampler, texCoord + offset[1]).xyz;
    vec3 tr = texture(InSampler, texCoord + offset[2]).xyz;
    vec3 ml = texture(InSampler, texCoord + offset[3]).xyz;
    vec3 mc = texture(InSampler, texCoord + offset[4]).xyz;
    vec3 mr = texture(InSampler, texCoord + offset[5]).xyz;
    vec3 bl = texture(InSampler, texCoord + offset[6]).xyz;
    vec3 bc = texture(InSampler, texCoord + offset[7]).xyz;
    vec3 br = texture(InSampler, texCoord + offset[8]).xyz;
    float diagDist1 = dot(abs(tl - br), ColorDiff) + 0.0001;
    float diagDist2 = dot(abs(tr - bl), ColorDiff) + 0.0001;
    float horDist = dot(abs(ml - mr), ColorDiff) + 0.0001;
    float verDist = dot(abs(tc - bc), ColorDiff) + 0.0001;
    float weight1 = 0.5 * (horDist + verDist);
    float weight2 = 0.5 * (diagDist1 + diagDist2);
    vec3 blend1 = (horDist * (tc + bc) + verDist * (ml + mr) + weight1 * mc) / (2.5 * (horDist + verDist));
    vec3 blend2 = (diagDist1 * (tr + bl) + diagDist2 * (tl + br) + weight2 * mc) / (2.5 * (diagDist1 + diagDist2));
    weight1 = dot(abs(blend1 - mc), ColorDiff) + 0.0001;
    weight2 = dot(abs(blend2 - mc), ColorDiff) + 0.0001;
    fragColor = vec4((weight1 * blend2 + weight2 * blend1) / (weight1 + weight2), 1.0);
}