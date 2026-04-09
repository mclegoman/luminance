#version 330

uniform sampler2D InSampler;

layout(std140) uniform DominantChannelConfig {
    int Mode;
    float Channels;
};

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec4 color = texture(InSampler, texCoord);
    float r = color.r;
    float g = color.g;
    float b = color.b;
    vec3 outColor = vec3(0.0);
    float minVal = min(r, min(g, b));
    float maxVal = max(r, max(g, b));
    float midVal = r + g + b - minVal - maxVal;

    // interpolation to slowly add in the second channel as Channels goes from 1 to 2
    float extra = clamp(Channels - 1.0, 0.0, 1.0);

    if (Mode == 0.0) {
        // do the extra channel before the first, so that if the first channel ties, the first channel will overwrite it (so that it doesnt interpolate)
        if (extra > 0.0) {
            if (r == midVal) outColor.r = r * extra;
            if (g == midVal) outColor.g = g * extra;
            if (b == midVal) outColor.b = b * extra;
        }
        if (r == maxVal) outColor.r = r;
        if (g == maxVal) outColor.g = g;
        if (b == maxVal) outColor.b = b;
    }
    else if (Mode == 1.0) {
        if (extra > 0.0) {
            float dMin = abs(midVal - minVal);
            float dMax = abs(maxVal - midVal);
            float refVal = (dMin < dMax) ? minVal : maxVal;
            if (r == refVal) outColor.r = r * extra;
            if (g == refVal) outColor.g = g * extra;
            if (b == refVal) outColor.b = b * extra;
        }
        if (r == midVal) outColor.r = r;
        if (g == midVal) outColor.g = g;
        if (b == midVal) outColor.b = b;
    }
    else if (Mode == 2.0) {
        if (extra > 0.0) {
            if (r == midVal) outColor.r = r * extra;
            if (g == midVal) outColor.g = g * extra;
            if (b == midVal) outColor.b = b * extra;
        }
        if (r == minVal) outColor.r = r;
        if (g == minVal) outColor.g = g;
        if (b == minVal) outColor.b = b;
    }

    if (Channels > 2.0) {
        // lerp towards default when channels is between 2 and 3. let it do whatever it wants above 3
        outColor = mix(outColor, color.rgb, Channels-2.0);
    }
    else if (Channels < 1.0) {
        // lerp towards black when channels is between 0 and 1
        outColor *= Channels;
    }

    fragColor = vec4(outColor, color.a);
}
