#version 150

uniform sampler2D InSampler;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

uniform vec2 Distance;
uniform vec4 Amounts;

vec4 scale(vec4 col, float amount) {
     return amount*col - amount+1;
}

void main(){
    vec2 texel = oneTexel*Distance;

    vec4 u  = scale(texture(InSampler, texCoord + vec2(        0.0, -texel.y)), Amounts.z);
    vec4 d  = scale(texture(InSampler, texCoord + vec2(        0.0,  texel.y)), Amounts.z);
    vec4 l  = scale(texture(InSampler, texCoord + vec2(-texel.x,         0.0)), Amounts.z);
    vec4 r  = scale(texture(InSampler, texCoord + vec2( texel.x,         0.0)), Amounts.z);

    vec4 v1 = min(l, r);
    vec4 v2 = min(u, d);
    vec4 v3 = min(v1, v2);

    vec4 ul = scale(texture(InSampler, texCoord + vec2(-texel.x, -texel.y)), Amounts.y);
    vec4 dr = scale(texture(InSampler, texCoord + vec2( texel.x,  texel.y)), Amounts.y);
    vec4 dl = scale(texture(InSampler, texCoord + vec2(-texel.x,  texel.y)), Amounts.y);
    vec4 ur = scale(texture(InSampler, texCoord + vec2( texel.x, -texel.y)), Amounts.y);

    vec4 v4 = min(ul, dr);
    vec4 v5 = min(ur, dl);
    vec4 v6 = min(v4, v5);

    vec4 v7 = min(v3, v6);

    vec4 uu = scale(texture(InSampler, texCoord + vec2(              0.0, -texel.y * 2.0)), Amounts.x);
    vec4 dd = scale(texture(InSampler, texCoord + vec2(              0.0,  texel.y * 2.0)), Amounts.x);
    vec4 ll = scale(texture(InSampler, texCoord + vec2(-texel.x * 2.0,               0.0)), Amounts.x);
    vec4 rr = scale(texture(InSampler, texCoord + vec2( texel.x * 2.0,               0.0)), Amounts.x);

    vec4 v8 = min(uu, dd);
    vec4 v9 = min(ll, rr);
    vec4 v10 = min(v8, v9);

    vec4 v11 = min(v7, v10);

    vec4 c  = scale(texture(InSampler, texCoord), Amounts.w);
    vec4 color = min(c, v11);
    fragColor = vec4(color.rgb, 1.0);
}
