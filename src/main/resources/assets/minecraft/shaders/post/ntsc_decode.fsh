#version 330

uniform sampler2D InSampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform NtscDecodeConfig {
    vec4 Zero;
    vec4 One;
    float Tau;
    vec4 A2;
    vec4 B;
    float CCFrequency;
    float NotchWidth;
    float YFrequency;
    float IFrequency;
    float QFrequency;
    float ScanTime;
    vec3[3] YIQ2;
    vec4 MinC;
    vec4 CRange;
    float TauLengthDivider;
    vec4 NotchOffset;
};

in vec2 texCoord;

out vec4 fragColor;

vec4 getW() {
    return vec4(Tau * CCFrequency * ScanTime);
}

float getNotchFrequency(bool upper) {
    return CCFrequency + (upper ? NotchWidth : -NotchWidth);
}

float getTauLength() {
    return Tau / TauLengthDivider;
}

void main() {
    vec2 oneTexel = 1.0 / InSize;
    vec4 YAccum = Zero;
    vec4 IAccum = Zero;
    vec4 QAccum = Zero;
    float QuadXSize = InSize.x * 4.0;
    float TimePerSample = ScanTime / QuadXSize;

    // Frequency cutoffs for the individual portions of the signal that we extract.
    // Y1 and Y2 are the positive and negative frequency limits of the notch filter on Y.
    // Y3 is the center of the frequency response of the Y filter.
    // I is the center of the frequency response of the I filter.
    // Q is the center of the frequency response of the Q filter.
    float Fc_y1 = getNotchFrequency(false) * TimePerSample;
    float Fc_y2 = getNotchFrequency(true) * TimePerSample;
    float Fc_y3 = YFrequency * TimePerSample;
    float Fc_i = IFrequency * TimePerSample;
    float Fc_q = QFrequency * TimePerSample;
    float TauFc_y1 = Fc_y1 * Tau;
    float TauFc_y2 = Fc_y2 * Tau;
    float TauFc_y3 = Fc_y3 * Tau;
    float TauFc_i = Fc_i * Tau;
    float TauFc_q = Fc_q * Tau;
    float Fc_y1_2 = Fc_y1 * 2.0;
    float Fc_y2_2 = Fc_y2 * 2.0;
    float Fc_y3_2 = Fc_y3 * 2.0;
    float Fc_i_2 = Fc_i * 2.0;
    float Fc_q_2 = Fc_q * 2.0;
    vec4 CoordY = vec4(texCoord.y);

    // 83 composite samples wide, 4 composite pixels per texel
    for (float n = -41.0; n < 42.0; n += 4.0)
    {
        vec4 n4 = n + NotchOffset;
        vec4 CoordX = texCoord.x + oneTexel.x * n4 * 0.25;
        vec2 TexCoord = vec2(CoordX.x, CoordY.y);
        vec4 C = texture(InSampler, TexCoord) * CRange + MinC;
        vec4 WT = getW() * (CoordX + A2 * CoordY * InSize.y + B);
        vec4 Cosine = 0.54 + 0.46 * cos(getTauLength() * n4);

        vec4 SincYIn1 = TauFc_y1 * n4;
        vec4 SincYIn2 = TauFc_y2 * n4;
        vec4 SincYIn3 = TauFc_y3 * n4;
        vec4 SincY1 = sin(SincYIn1) / SincYIn1;
        vec4 SincY2 = sin(SincYIn2) / SincYIn2;
        vec4 SincY3 = sin(SincYIn3) / SincYIn3;

        // These zero-checks could be made more efficient, but we are trying to support
        // downlevel GLSL
        if(SincYIn1.x == 0.0) SincY1.x = 1.0;
        if(SincYIn1.y == 0.0) SincY1.y = 1.0;
        if(SincYIn1.z == 0.0) SincY1.z = 1.0;
        if(SincYIn1.w == 0.0) SincY1.w = 1.0;
        if(SincYIn2.x == 0.0) SincY2.x = 1.0;
        if(SincYIn2.y == 0.0) SincY2.y = 1.0;
        if(SincYIn2.z == 0.0) SincY2.z = 1.0;
        if(SincYIn2.w == 0.0) SincY2.w = 1.0;
        if(SincYIn3.x == 0.0) SincY3.x = 1.0;
        if(SincYIn3.y == 0.0) SincY3.y = 1.0;
        if(SincYIn3.z == 0.0) SincY3.z = 1.0;
        if(SincYIn3.w == 0.0) SincY3.w = 1.0;
        vec4 IdealY = (Fc_y1_2 * SincY1 - Fc_y2_2 * SincY2) + Fc_y3_2 * SincY3;
        vec4 FilterY = Cosine * IdealY;

        vec4 SincIIn = TauFc_i * n4;
        vec4 SincI = sin(SincIIn) / SincIIn;
        if(SincIIn.x == 0.0) SincI.x = 1.0;
        if(SincIIn.y == 0.0) SincI.y = 1.0;
        if(SincIIn.z == 0.0) SincI.z = 1.0;
        if(SincIIn.w == 0.0) SincI.w = 1.0;
        vec4 IdealI = Fc_i_2 * SincI;
        vec4 FilterI = Cosine * IdealI;

        vec4 SincQIn = TauFc_q * n4;
        vec4 SincQ = sin(SincQIn) / SincQIn;
        if(SincQIn.x == 0.0) SincQ.x = 1.0;
        if(SincQIn.y == 0.0) SincQ.y = 1.0;
        if(SincQIn.z == 0.0) SincQ.z = 1.0;
        if(SincQIn.w == 0.0) SincQ.w = 1.0;
        vec4 IdealQ = Fc_q_2 * SincQ;
        vec4 FilterQ = Cosine * IdealQ;

        YAccum += C * FilterY;
        IAccum += C * cos(WT) * FilterI;
        QAccum += C * sin(WT) * FilterQ;
    }

    float Y = dot(YAccum, One);
    float I = dot(IAccum, One) * 2.0;
    float Q = dot(QAccum, One) * 2.0;

    vec3 YIQ = vec3(Y, I, Q);
    vec3 OutRGB = vec3(dot(YIQ, YIQ2[0]), dot(YIQ, YIQ2[1]), dot(YIQ, YIQ2[2]));

    fragColor = vec4(OutRGB, 1.0);
}
