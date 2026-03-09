#version 150

// If you want to make a normal double vision shader, update both ShiftL and ShiftR to 31.5.
// luminance:vision2 uses different values as these are based on my actual eyes. :p

uniform sampler2D InSampler, InDepthSampler;

uniform float SphL, CylL, AxisL, AddNearL, AddInterL, ShiftL, SphR, CylR, AxisR, AddNearR, AddInterR, ShiftR, FoveaAngle, Mode, InvertEyeCorrection, PI, ToRadianDenominator, SampleRange, FovealFalloff, ShiftFactor, DepthFoveaMix, RadiusScale, MinRadius, MaxRadius, CylSphBlend, FoveaCenter, InterpCutoff, Mix, BlurSigmaFactor, CylBias, CylScale, ZPlane;

uniform vec3 luminance_crosshair_target, luminance_cam;
uniform float luminance_fov;

in vec2 texCoord, oneTexel;
out vec4 fragColor;

float toRadians(float degrees) {
    return degrees * PI / ToRadianDenominator;
}

float getRadius(float value, float weight) {
    return clamp(abs(value) * (1.0 - weight) * RadiusScale + 0.5, MinRadius, MaxRadius);
}

vec4 blur(vec2 uv, float sph, float cyl, float axis, float nearAdd, float interAdd) {
    vec2 pos = uv * 2.0 - 1.0;
    pos.x *= oneTexel.y / oneTexel.x;
    pos.y *= -1.0;
    float angle = acos(clamp(dot(normalize(vec3(pos, ZPlane)), normalize(luminance_crosshair_target - luminance_cam)), -1.0, 1.0));
    float fov = toRadians(FoveaAngle);
    float weight = mix(clamp(1.0 - texture(InDepthSampler, uv).r, 0.0, 1.0), exp(-angle * angle / (2.0 * fov * fov)), DepthFoveaMix);
    float sphRadius = getRadius(mix(-sph, sph, InvertEyeCorrection) + mix(interAdd * InvertEyeCorrection, nearAdd * InvertEyeCorrection, weight), weight);
    float cylRadius = getRadius(mix(-cyl, cyl, InvertEyeCorrection), weight);
    int samples = max(1, int(float(int(floor(SampleRange + 0.5))) * pow(1.0 - weight, FovealFalloff)));
    vec4 sphBlur = vec4(0.0);
    float sphWeightSum = 0.0;
    for (int x = -samples; x <= samples; x++) {
        for (int y = -samples; y <= samples; y++) {
            float sampleWeight = exp(-float(x * x + y * y) / (BlurSigmaFactor * sphRadius * sphRadius));
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
        float sampleWeight = exp(-float(k * k) / (BlurSigmaFactor * cylRadius * cylRadius)) * (CylBias + CylScale * weight);
        cylBlur += texture(InSampler, uv + axisDir * float(k) * oneTexel * cylRadius) * sampleWeight;
        cylWeightSum += sampleWeight;
    }
    cylBlur /= cylWeightSum;
    return mix(cylBlur, sphBlur, CylSphBlend);
}

void main() {
    vec4 colorLeft  = blur(clamp(texCoord - vec2(ShiftL * oneTexel.x * ShiftFactor, 0.0), 0.0, 1.0),  SphL, CylL, AxisL, AddNearL, AddInterL);
    vec4 colorRight = blur(clamp(texCoord + vec2(ShiftR * oneTexel.x * ShiftFactor, 0.0), 0.0, 1.0), SphR, CylR, AxisR, AddNearR, AddInterR);
    float mode = floor(Mode + 0.5);
    if (mode == 0.0) fragColor = colorLeft;
    else if (mode == 1.0) fragColor = colorRight;
    else if (mode == 2.0) {
        float halfFoveaUV = FoveaAngle / luminance_fov * 0.5;
        fragColor = mix(colorLeft, colorRight, smoothstep(FoveaCenter - halfFoveaUV, FoveaCenter + halfFoveaUV, texCoord.x));
    } else if (mode == 3.0) fragColor = texCoord.x < InterpCutoff ? colorLeft : colorRight;
    else fragColor = mix(colorLeft, colorRight, Mix);
}