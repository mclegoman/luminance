#version 150

uniform sampler2D InSampler;
uniform sampler2D InDepthSampler;

uniform float SphR;
uniform float CylR;
uniform float AxisR;
uniform float AddNearR;
uniform float AddInterR;
uniform float PdR;
uniform float SphL;
uniform float CylL;
uniform float AxisL;
uniform float AddNearL;
uniform float AddInterL;
uniform float PdL;
uniform float Mode;
uniform float Reverse;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

vec4 apply(vec2 uv, float sph, float cyl, float axis, float addNear, float addInter) {
    float effectiveSph = mix(-sph, sph, Reverse);
    float effectiveCyl = mix(-cyl, cyl, Reverse);
    float effectiveAddNear = addNear * Reverse;
    float effectiveAddInter = addInter * Reverse;

    float depth = texture(InDepthSampler, uv).r;
    float focusFactor = clamp(1.0 - depth, 0.0, 1.0);
    float addEffect = mix(effectiveAddInter, effectiveAddNear, focusFactor);

    float sphRadius = max(1.0, abs(effectiveSph + addEffect) * 2.0);
    vec4 sphBlur = vec4(0.0);
    int samples = 5;
    for(int i=-samples; i<=samples; i++) {
        for(int j=-samples; j<=samples; j++) {
            sphBlur += texture(InSampler, uv + vec2(i,j)*oneTexel*sphRadius);
        }
    }
    sphBlur /= float((samples*2+1)*(samples*2+1));

    float rad = radians(axis);
    vec2 dir = vec2(cos(rad), sin(rad));
    float cylRadius = max(1.0, abs(effectiveCyl) * 2.0);
    vec4 cylBlur = vec4(0.0);
    for(int k=-samples; k<=samples; k++) {
        cylBlur += texture(InSampler, uv + dir*float(k)*oneTexel*cylRadius);
    }
    cylBlur /= float(samples*2+1);

    return mix(sphBlur, cylBlur, 0.5);
}

void main() {
    vec2 uvR = texCoord + vec2(PdR * oneTexel.x / 2.0, 0.0);
    vec2 uvL = texCoord - vec2(PdL * oneTexel.x / 2.0, 0.0);

    uvR = clamp(uvR, 0.0, 1.0);
    uvL = clamp(uvL, 0.0, 1.0);

    vec4 colorR = apply(uvR, SphR, CylR, AxisR, AddNearR, AddInterR);
    vec4 colorL = apply(uvL, SphL, CylL, AxisL, AddNearL, AddInterL);

    float mode = floor(Mode + 0.5);
    if (mode == 0.0) {
        fragColor = colorL;
    } else if(mode == 1.0) {
        fragColor = colorR;
    } else if(mode == 2.0) {
        float blend = step(0.5, texCoord.x);
        fragColor = mix(colorL, colorR, blend);
    } else {
        fragColor = (colorL + colorR) * 0.5;
    }
}