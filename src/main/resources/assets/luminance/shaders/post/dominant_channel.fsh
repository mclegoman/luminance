#version 150

uniform sampler2D InSampler;
uniform float Mode;
uniform float Channels;

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
    if (Channels >= 3.0) outColor = vec3(r, g, b);
    else if (Mode == 0.0) {
        if (r == maxVal) outColor.r = r;
        if (g == maxVal) outColor.g = g;
        if (b == maxVal) outColor.b = b;
        if (Channels > 1.0) {
            if (r == midVal) outColor.r = r;
            if (g == midVal) outColor.g = g;
            if (b == midVal) outColor.b = b;
        }
    }
    else if (Mode == 1.0) {
        if (r == midVal) outColor.r = r;
        if (g == midVal) outColor.g = g;
        if (b == midVal) outColor.b = b;
        if (Channels > 1.0) {
            float dMin = abs(midVal - minVal);
            float dMax = abs(maxVal - midVal);
            float refVal = (dMin < dMax) ? minVal : maxVal;
            if (r == refVal) outColor.r = r;
            if (g == refVal) outColor.g = g;
            if (b == refVal) outColor.b = b;
        }
    }
    else if (Mode == 2.0) {
        if (r == minVal) outColor.r = r;
        if (g == minVal) outColor.g = g;
        if (b == minVal) outColor.b = b;
        if (Channels > 1.0) {
            if (r == midVal) outColor.r = r;
            if (g == midVal) outColor.g = g;
            if (b == midVal) outColor.b = b;
        }
    }
    fragColor = vec4(outColor, color.a);
}
