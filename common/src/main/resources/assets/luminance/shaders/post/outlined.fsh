#version 150

uniform sampler2D InSampler;
uniform sampler2D InDepthSampler;
uniform sampler2D TranslucentSampler;
uniform sampler2D TranslucentDepthSampler;
uniform sampler2D ItemEntitySampler;
uniform sampler2D ItemEntityDepthSampler;
uniform sampler2D ParticlesSampler;
uniform sampler2D ParticlesDepthSampler;
uniform sampler2D WeatherSampler;
uniform sampler2D WeatherDepthSampler;
uniform sampler2D CloudsSampler;
uniform sampler2D CloudsDepthSampler;

in vec2 texCoord;

uniform vec2 OutSize;
uniform float Transparency;
uniform float Thickness;
uniform float Outline;
uniform vec3 OutlineColor;
uniform vec3 OutlinePow;
uniform float OutlineColorMultiplier;
uniform float Silhouette;
uniform vec3 SilhouetteColor;
uniform float luminance_viewDistance;
uniform float luminance_alpha_smooth;

//vec4 color_layers[6];
//float depth_layers[6];
//int active_layers = 0;

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
    float depth = getDepth( texCoord );
    float outlineDepth = 2.0 * 0.025 * 1000.0 / (1000.0 + 0.025 - (depth * 2.0 - 1.0) * (1000.0 - 0.025));
    float offset = max(Thickness * max((32.0 - outlineDepth) / 32.0, 0.0), 1.0 / OutSize.y);
    float depth0 = getDepth(texCoord + vec2(-offset * OutSize.y / OutSize.x, -offset));
    float depth1 = getDepth(texCoord + vec2(-offset * OutSize.y / OutSize.x, +offset));
    float depth2 = getDepth(texCoord + vec2(+offset * OutSize.y / OutSize.x, +offset));
    float depth3 = getDepth(texCoord + vec2(+offset * OutSize.y / OutSize.x, -offset));
    float amount = clamp(pow(max(2.0 * 0.025 * 1000.0 / (1000.0 + 0.025 - (max(max(depth0, depth1), max(depth2, depth3)) * 2.0 - 1.0) * (1000.0 - 0.025)) - outlineDepth, 0.0) * 0.15, 2.0), 0.0, 1.0) * exp(-outlineDepth * 0.025);

    vec3 outlineColor;
    if (Outline == 0) {
        outlineColor = color.rgb;
    } else if (Outline == 1) {
        outlineColor = OutlineColor;
    }

    vec4 outputColor = vec4(mix(color.rgb, pow((pow(outlineColor, OutlinePow) * OutlineColorMultiplier) + Transparency, vec3(2.0)), amount), color.a);
    float depth4 = min(max(1.0 - (1.0 - depth) * ((luminance_viewDistance * 16) * 0.64), 0.0), 1.0);
    return vec4(mix(color.rgb, mix(outputColor.rgb, color.rgb, smoothstep(0.9, 0.91, depth4)), luminance_alpha_smooth), outputColor.a);
}

//void try_insert( vec4 color, sampler2D DepthSampler ) {
//    if ( color.a == 0.0 ) {
//        return;
//    }
//    float depth = texture( DepthSampler, texCoord ).r;
//
//    if (Silhouette != 0) color = vec4(mix(color.rgb, SilhouetteColor, luminance_alpha_smooth), 1.0);
//
//    color = outline(color, DepthSampler);
//
//    color_layers[active_layers] = color;
//    depth_layers[active_layers] = depth;
//
//    int next_layer = active_layers++;
//    int current_layer = next_layer - 1;
//    while ( next_layer > 0 && depth_layers[next_layer] > depth_layers[current_layer] ) {
//        float depthTemp = depth_layers[current_layer];
//        depth_layers[current_layer] = depth_layers[next_layer];
//        depth_layers[next_layer] = depthTemp;
//
//        vec4 colorTemp = color_layers[current_layer];
//        color_layers[current_layer] = color_layers[next_layer];
//        color_layers[next_layer] = colorTemp;
//
//        next_layer = current_layer--;
//    }
//}

//vec3 blend( vec3 dst, vec4 src ) {
//    return ( dst * ( 1.0 - src.a ) ) + src.rgb;
//}

void main() {
    vec4 color;
    vec4 baseColor = texture(InSampler, texCoord);
    if (Silhouette != 0) {
        color = vec4(SilhouetteColor, 1.0);
    } else {
        color = baseColor;
    }

    color = outline(color, InDepthSampler);

    //color_layers[0] = vec4(mix(texture(InSampler, texCoord).rgb, outline(color, InDepthSampler).rgb, luminance_alpha_smooth), 1.0);
    //depth_layers[0] = texture(InDepthSampler, texCoord).r;
    //active_layers = 1;

    //try_insert( texture( TranslucentSampler, texCoord ), TranslucentDepthSampler );
    //try_insert( texture( ItemEntitySampler, texCoord ), ItemEntityDepthSampler );
    //try_insert( texture( ParticlesSampler, texCoord ), ParticlesDepthSampler );
    //try_insert( texture( WeatherSampler, texCoord ), WeatherDepthSampler );
    //try_insert( texture( CloudsSampler, texCoord ), CloudsDepthSampler );

    //vec3 texelAccum = color_layers[0].rgb;
    //for ( int ii = 1; ii < active_layers; ++ii ) {
    //    texelAccum = blend( texelAccum, color_layers[ii] );
    //}

    fragColor = vec4(mix(baseColor.rgb, color.rgb, luminance_alpha_smooth), 1.0);
}
