#version 330

uniform sampler2D InSampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform KuwaharaConfig {
    float Radius;
    float Intensity;
};

in vec2 texCoord;

out vec4 fragColor;

vec4 kuwahara(vec2 uv, vec2 oneTexel, vec2 min, vec2 max) {
    vec3 mean = vec3(0.0);
    vec3 sqMean = vec3(0.0);
    int count = 0;

    for (int x = int(min.x); x <= int(max.x); x++) {
        for (int y = int(min.y); y <= int(max.y); y++) {
            vec2 offset = vec2(x, y) * oneTexel;
            vec3 colour = texture(InSampler, uv + offset).rgb;

            mean += colour;
            sqMean += (colour * colour);
            count++;
        }
    }

    mean /= float(count);
    sqMean /= float(count);

    vec3 variance = sqMean - (mean * mean);
    float sigma = variance.r + variance.g + variance.b;

    return vec4(mean, sigma);
}

void main() {
    vec2 oneTexel = 1.0 / InSize;

    vec4 r1 = kuwahara(texCoord, oneTexel, vec2(-Radius, -Radius), vec2(0, 0));
    vec4 r2 = kuwahara(texCoord, oneTexel, vec2(0, -Radius), vec2(Radius, 0));
    vec4 r3 = kuwahara(texCoord, oneTexel, vec2(-Radius, 0), vec2(0, Radius));
    vec4 r4 = kuwahara(texCoord, oneTexel, vec2(0, 0), vec2(Radius, Radius));

    vec4 lowestVariance = r1;
    if (r2.a < lowestVariance.a) lowestVariance = r2;
    if (r3.a < lowestVariance.a) lowestVariance = r3;
    if (r4.a < lowestVariance.a) lowestVariance = r4;

    vec4 colour = texture(InSampler, texCoord);
    fragColor = vec4(mix(colour.rgb, lowestVariance.rgb, Intensity), colour.a);
}