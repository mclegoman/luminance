#version 330

// If you want to make a normal double vision shader, update both ShiftL and ShiftR to 31.5.
// luminance:vision2 uses different values as these are based on my actual eyes. :p

uniform sampler2D InSampler;
uniform sampler2D InDepthSampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform VisionConfig {
    float SphL;
    float CylL;
    float AxisL;
    float AddNearL;
    float AddInterL;
    float ShiftL;
    float SphR;
    float CylR;
    float AxisR;
    float AddNearR;
    float AddInterR;
    float ShiftR;
    float FoveaAngle;
    int Mode;
    float InvertEyeCorrection;
    float PI;
    float ToRadianDenominator;
    float SampleRange;
    float FovealFalloff;
    float ShiftFactor;
    float DepthFoveaMix;
    float RadiusScale;
    float MinRadius;
    float MaxRadius;
    float CylSphBlend;
    float FoveaCenter;
    float InterpCutoff;
    float Mix;
    float BlurSigmaFactor;
    float CylBias;
    float CylScale;
    float ZPlane;
    float Pitch;
    float Yaw;
    float FOV;
};

in vec2 texCoord;

out vec4 fragColor;

float toRadians(float degrees) {
    return degrees * PI / ToRadianDenominator;
}

float getRadius(float value, float weight) {
    return clamp(abs(value) * (1.0 - weight) * RadiusScale + 0.5, MinRadius, MaxRadius);
}

vec3 getGazeDir(float pitch, float yaw) {
    float cosPitch = cos(pitch);
    float sinPitch = sin(pitch);
    float cosYaw = cos(yaw);
    float sinYaw = sin(yaw);
    return normalize(vec3(cosPitch * sinYaw, sinPitch, cosPitch * cosYaw));
}

vec4 blur(vec2 uv, float sph, float cyl, float axis, float nearAdd, float interAdd, vec2 oneTexel) {
    vec2 pos = uv * 2.0 - 1.0;
    pos.x *= oneTexel.y / oneTexel.x;
    pos.y *= -1.0;

    vec3 viewDir = vec3(pos, ZPlane);
    float viewLen2 = dot(viewDir, viewDir);
    if (viewLen2 < 1e-8) viewDir = vec3(0.0, 0.0, 1.0);
    else viewDir /= sqrt(viewLen2);

    vec3 gazeDir = getGazeDir(Pitch, Yaw);
    float gazeLen2 = dot(gazeDir, gazeDir);
    if (gazeLen2 < 1e-8) gazeDir = vec3(0.0, 0.0, 1.0);
    else gazeDir /= sqrt(gazeLen2);

    float angle = acos(clamp(dot(viewDir, gazeDir), -1.0, 1.0));

    float fov = toRadians(FoveaAngle);
    float weight = mix(clamp(1.0 - texture(InDepthSampler, uv).r, 0.0, 1.0), exp(-angle * angle / (2.0 * fov * fov)), DepthFoveaMix);
    float sphRadius = getRadius(mix(-sph, sph, InvertEyeCorrection) + mix(interAdd * InvertEyeCorrection, nearAdd * InvertEyeCorrection, weight), weight);
    float cylRadius = getRadius(mix(-cyl, cyl, InvertEyeCorrection), weight);
    int base = int(floor(SampleRange + 0.5));
    int samples = max(1, int(float(base) * pow(1.0 - weight, FovealFalloff)));
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
    vec2 oneTexel = 1.0 / InSize;
    vec4 colorLeft  = blur(clamp(texCoord - vec2(ShiftL * oneTexel.x * ShiftFactor, 0.0), 0.0, 1.0),  SphL, CylL, AxisL, AddNearL, AddInterL, oneTexel);
    vec4 colorRight = blur(clamp(texCoord + vec2(ShiftR * oneTexel.x * ShiftFactor, 0.0), 0.0, 1.0), SphR, CylR, AxisR, AddNearR, AddInterR, oneTexel);
    // Mode
    // 0: Left eye only
    // 1: Right eye only
    // 2: Smoothed vertical split
    // 3: Hard vertical split
    // 4: Mixed left and right
    if (Mode == 0) fragColor = colorLeft;
    else if (Mode == 1) fragColor = colorRight;
    else if (Mode == 2) {
        float halfFoveaUV = FoveaAngle / FOV * 0.5;
        fragColor = mix(colorLeft, colorRight, smoothstep(FoveaCenter - halfFoveaUV, FoveaCenter + halfFoveaUV, texCoord.x));
    } else if (Mode == 3) fragColor = texCoord.x < InterpCutoff ? colorLeft : colorRight;
    else fragColor = mix(colorLeft, colorRight, Mix);
}