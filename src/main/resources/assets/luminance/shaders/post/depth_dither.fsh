#version 330

uniform sampler2D InSampler;
uniform sampler2D InDepthSampler;
uniform sampler2D DitherSampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
    vec2 InDepthSize;
    vec2 DitherSize;
};

layout(std140) uniform DepthDitherConfig {
    float ViewDistance;
    float Scale;
};

in vec2 texCoord;

out vec4 fragColor;

void main() {
    float depth = min(max(1.0 - (1.0 - texture(InDepthSampler, texCoord).r) * ((ViewDistance * 16) * (ViewDistance * 0.1)), 0.0), 1.0);
    vec2 halfSize = InSize/Scale;

    vec2 steppedCoord = texCoord;
    vec4 color = texture(InSampler, steppedCoord);

    steppedCoord.x = float(int(steppedCoord.x*halfSize.x)) / halfSize.x;
    steppedCoord.y = float(int(steppedCoord.y*halfSize.y)) / halfSize.y;

    vec4 noise = texture(DitherSampler, fract(steppedCoord * halfSize / DitherSize));
    vec4 col = color + noise * vec4(1.0/12.0, 1.0/12.0, 1.0/6.0, 1.0);
    float r = float(int(col.r*8.0))/8.0;
    float g = float(int(col.g*8.0))/8.0;
    float b = float(int(col.b*4.0))/4.0;
    fragColor = vec4(mix(color.rgb, vec3(r, g, b), depth), 1.0);

    // temporary depth output for testing
    fragColor = vec4(vec3(texture(InDepthSampler, texCoord).r), 1.0);
}
