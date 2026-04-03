#version 330

uniform sampler2D InSampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform ScanPincushionConfig {
    float PincushionAmount;
    float CurvatureAmount;
    float ScanlineAmount;
    float ScanlineScale;
    vec3 Floor;
    vec3 Power;
};

in vec2 texCoord;

out vec4 fragColor;

const vec4 Zero = vec4(0.0);
const vec4 Half = vec4(0.5);
const vec4 One = vec4(1.0);
const vec4 Two = vec4(2.0);

const float Pi = 3.1415926535;
const float ScanlineHeight = 1.0;
const float ScanlineBrightScale = 1.0;
const float ScanlineBrightOffset = 0.0;
const float ScanlineOffset = 0.0;

void main() {
    vec4 InTexel = texture(InSampler, texCoord);

    vec2 PinUnitCoord = texCoord * Two.xy - One.xy;
    float PincushionR2 = pow(length(PinUnitCoord), 2.0);
    vec2 PincushionCurve = PinUnitCoord * PincushionAmount * PincushionR2;
    vec2 ScanCoord = texCoord;

    ScanCoord *= One.xy - PincushionAmount * 0.2;
    ScanCoord += PincushionAmount * 0.1;
    ScanCoord += PincushionCurve;

    vec2 CurvatureClipCurve = PinUnitCoord * CurvatureAmount * PincushionR2;
    vec2 ScreenClipCoord = texCoord;
    ScreenClipCoord -= Half.xy;
    ScreenClipCoord *= One.xy - CurvatureAmount * 0.2;
    ScreenClipCoord += Half.xy;
    ScreenClipCoord += CurvatureClipCurve;

    if (ScanCoord.x < 0.0 || ScanCoord.y < 0.0 || ScanCoord.x > 1.0 || ScanCoord.y > 1.0) {
        // -- Alpha Clipping --
        // this used to use the discard keyword, but how that works is a little inconsistent
        fragColor = vec4(0.0, 0.0, 0.0, 1.0);
    } else {
        // -- Scanline Simulation --
        float InnerSine = ScanCoord.y * InSize.y * ScanlineScale * 0.25;
        float ScanBrightMod = sin(InnerSine * Pi + ScanlineOffset * InSize.y * 0.25);
        float ScanBrightness = mix(1.0, (pow(ScanBrightMod * ScanBrightMod, ScanlineHeight) * ScanlineBrightScale + 1.0) * 0.5, ScanlineAmount);
        vec3 ScanlineTexel = InTexel.rgb * ScanBrightness;

        // -- Color Compression (increasing the floor of the signal without affecting the ceiling) --
        ScanlineTexel = Floor + (One.xyz - Floor) * ScanlineTexel;

        ScanlineTexel.rgb = pow(ScanlineTexel.rgb, Power);

        fragColor = vec4(ScanlineTexel.rgb, 1.0);
    }
}
