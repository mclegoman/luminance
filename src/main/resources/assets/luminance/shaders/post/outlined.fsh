#version 330

// Improved Transparency changes depth, so this shader needs to be ran in the LEVEL RenderLocation when that is enabled
uniform sampler2D InSampler;
uniform sampler2D InDepthSampler;
uniform sampler2D TranslucentDepthSampler;
uniform sampler2D ItemEntityDepthSampler;
uniform sampler2D ParticlesDepthSampler;
uniform sampler2D WeatherDepthSampler;
uniform sampler2D CloudsDepthSampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
    vec2 InDepthSize;
    vec2 TranslucentDepthSize;
    vec2 ItemEntityDepthSize;
    vec2 ParticlesDepthSize;
    vec2 WeatherDepthSize;
    vec2 CloudsDepthSize;
};

layout(std140) uniform OutlinedConfig {
    float Transparency;
    float Thickness;
    int Outline;
    vec3 OutlineColor;
    vec3 OutlinePow;
    float OutlineColorMultiplier;
    float Distance;
};

in vec2 texCoord;

out vec4 fragColor;

float total(vec4 c) {
    return c.r + c.g + c.b;
}

float getDepth(sampler2D tex, sampler2D depth, vec2 coord) {
    if (total(texture(tex, coord)) > 0) {
        float d = texture(depth, coord).r;
        return d == 0 ? 1 : d;
    }
    return 1;
}

float getDepth(vec2 coord) {
    float depth0 = getDepth(InDepthSampler, InDepthSampler, coord);
    float depth1 = getDepth(TranslucentDepthSampler, TranslucentDepthSampler, coord);
    float depth2 = getDepth(ItemEntityDepthSampler, ItemEntityDepthSampler, coord);
    float depth3 = getDepth(ParticlesDepthSampler, ParticlesDepthSampler, coord);
    float depth4 = getDepth(WeatherDepthSampler, WeatherDepthSampler, coord);
    float depth5 = getDepth(CloudsDepthSampler, CloudsDepthSampler, coord);
    return min(min(min(depth0, depth1), min(depth2, depth3)), min(depth4, depth5));
}

vec4 outline( vec4 color, sampler2D DepthSampler ) {
    float depth = getDepth(texCoord);
    float outlineDepth = 2.0 * 0.025 * 1000.0 / (1000.0 + 0.025 - (depth * 2.0 - 1.0) * (1000.0 - 0.025));
    float offset = max(Thickness * max((32.0 - outlineDepth) / 32.0, 0.0), 1.0 / OutSize.y);
    float depth0 = getDepth(texCoord + vec2(-offset * OutSize.y / OutSize.x, -offset));
    float depth1 = getDepth(texCoord + vec2(-offset * OutSize.y / OutSize.x, +offset));
    float depth2 = getDepth(texCoord + vec2(+offset * OutSize.y / OutSize.x, +offset));
    float depth3 = getDepth(texCoord + vec2(+offset * OutSize.y / OutSize.x, -offset));
    float amount = clamp(pow(max(2.0 * 0.025 * 1000.0 / (1000.0 + 0.025 - (max(max(depth0, depth1), max(depth2, depth3)) * 2.0 - 1.0) * (1000.0 - 0.025)) - outlineDepth, 0.0) * 0.15, 2.0), 0.0, 1.0) * exp(-outlineDepth * 0.025);

    vec3 outlineColor;
    if (Outline == 0) outlineColor = color.rgb;
    else outlineColor = OutlineColor;

    vec4 outputColor = vec4(mix(color.rgb, pow((pow(outlineColor, OutlinePow) * OutlineColorMultiplier) + Transparency, vec3(2.0)), amount), color.a);
    float depth4;
    if (Distance < 0) depth4 = min(max(1.0 - depth, 0.0), 1.0);
    else depth4 = min(max(1.0 - (1.0 - depth) * ((Distance * 16) * 0.64), 0.0), 1.0);
    return vec4(mix(outputColor.rgb, color.rgb, smoothstep(0.9, 0.91, depth4)), outputColor.a);
}

void main() {
    vec4 baseColor = texture(InSampler, texCoord);
    vec4 color = outline(baseColor, InDepthSampler);
    fragColor = vec4(color.rgb, baseColor.a);
}
