#version 150

uniform sampler2D InSampler;

in vec2 texCoord;
out vec4 fragColor;

uniform vec2 Curvature;
uniform vec2 Rotation;
uniform float Scale;

uniform float Wrapping;

vec4 wrapTexture(sampler2D tex, vec2 coord) {
    return texture(tex, mix(coord, fract(coord), Wrapping));
}

void main(){
    vec2 coord = (texCoord-vec2(0.5)) / Scale;
    float dist = length(coord);
    float radius = (atan(dist, sqrt(1.0 + dist * dist * Curvature.x)) / 3.14159) * Curvature.y;
    float atanCoord = atan(coord.y, coord.x) * Rotation.y + Rotation.x;
    fragColor = vec4(wrapTexture(InSampler, vec2(radius*cos(atanCoord), radius*sin(atanCoord)) + vec2(0.5)).rgb, 1.0);
}
