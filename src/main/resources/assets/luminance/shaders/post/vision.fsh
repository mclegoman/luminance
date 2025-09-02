#version 150

// If you want to make a normal double vision shader, update both ShiftL and ShiftR to 31.5.
// luminance:vision2 uses different values as these are based on my actual eyes. :p

uniform sampler2D InSampler, InDepthSampler;

uniform float SphL, CylL, AxisL, AddNearL, AddInterL, ShiftL, SphR, CylR, AxisR, AddNearR, AddInterR, ShiftR, FoveaAngle, Mode, Reverse, PI, ToRadianDenominator, SampleRange;

uniform vec3 luminance_crosshair_target_smooth, luminance_cam_smooth;
uniform float luminance_fov_smooth;

in vec2 texCoord, oneTexel;
out vec4 fragColor;

float toRadians(float degrees) {
    return degrees * PI / ToRadianDenominator;
}

float getAngle(vec2 uv) {
    vec2 pos = uv * 2.0 - 1.0;
    pos.x *= oneTexel.y / oneTexel.x;
    pos.y *= -1.0;
    return acos(clamp(dot(normalize(vec3(pos, -1.0)), normalize(luminance_crosshair_target_smooth - luminance_cam_smooth)), -1.0, 1.0));
}

float getWeight(vec2 uv) {
    float angle = getAngle(uv);
    float fov = toRadians(FoveaAngle);
    return mix(clamp(1.0 - texture(InDepthSampler, uv).r, 0.0, 1.0), exp(-angle * angle / (2.0 * fov * fov)), 0.9);
}

float getRadius(float value, float weight) {
    return clamp(abs(value) * (1.0 - weight) * 5.0 + 0.5, 0.5, 20.0);
}

vec4 blur(vec2 uv, float sph, float cyl, float axis, float nearAdd, float interAdd) {
    float weight = getWeight(uv);
    float sphRadius = getRadius(mix(-sph, sph, Reverse) + mix(interAdd * Reverse, nearAdd * Reverse, weight), weight);
    float cylRadius = getRadius(mix(-cyl, cyl, Reverse), weight);

    int samples = max(1, int(float(int(floor(SampleRange + 0.5))) * pow(1.0 - weight, 1.5)));

    vec4 sphBlur = vec4(0.0);
    float sphWeightSum = 0.0;
    for (int x = -samples; x <= samples; x++) {
        for (int y = -samples; y <= samples; y++) {
            float sampleWeight = exp(-float(x*x + y*y) / (2.0 * sphRadius * sphRadius));
            sphBlur += texture(InSampler, uv + vec2(x, y) * oneTexel * sphRadius) * sampleWeight;
            sphWeightSum += sampleWeight;
        }
    }
    sphBlur /= sphWeightSum;

    float axisRad = toRadians(axis);
    vec2 axisDir = vec2(cos(axisRad), sin(axisRad));
    vec4 cylBlur = vec4(0.0);
    float cylWeightSum = 0.0;
    for (int k = -samples; k <= samples; k++) {
        float sampleWeight = exp(-float(k*k) / (2.0 * cylRadius * cylRadius)) * (0.5 + 0.5 * weight);
        cylBlur += texture(InSampler, uv + axisDir * float(k) * oneTexel * cylRadius) * sampleWeight;
        cylWeightSum += sampleWeight;
    }
    cylBlur /= cylWeightSum;

    return mix(cylBlur, sphBlur, 0.6);
}

void main() {
    vec2 uvRight = clamp(texCoord + vec2(ShiftR * oneTexel.x * 0.5, 0.0), 0.0, 1.0);
    vec2 uvLeft  = clamp(texCoord - vec2(ShiftL * oneTexel.x * 0.5, 0.0), 0.0, 1.0);

    vec4 colorRight = blur(uvRight, SphR, CylR, AxisR, AddNearR, AddInterR);
    vec4 colorLeft  = blur(uvLeft,  SphL, CylL, AxisL, AddNearL, AddInterL);

    float mode = floor(Mode + 0.5);

    if (mode == 0.0) {
        fragColor = colorLeft;
    } else if (mode == 1.0) {
        fragColor = colorRight;
    } else if (mode == 2.0) {
        float halfFoveaUV = FoveaAngle / luminance_fov_smooth * 0.5;
        fragColor = mix(colorLeft, colorRight, smoothstep(0.5 - halfFoveaUV, 0.5 + halfFoveaUV, texCoord.x));
    } else if (mode == 3.0) {
        fragColor = texCoord.x < 0.5 ? colorLeft : colorRight;
    } else {
        fragColor = mix(colorLeft, colorRight, 0.5);
    }
}