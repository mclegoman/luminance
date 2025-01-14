#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D OverlaySampler;

in vec2 texCoord;
out vec4 fragColor;

uniform vec2 InSize;
uniform float luminance_alpha_smooth;
uniform float luminance_time;
uniform float xAmount;
uniform float yAmount;
uniform float speed;

void main() {
    vec4 color = texture(DiffuseSampler, texCoord);
    vec4 overlay = texture(OverlaySampler, fract(vec2(texCoord.x, -texCoord.y) + (vec2(xAmount, yAmount) * ((vec2(luminance_time * InSize.x, luminance_time * InSize.y) * speed)))));
    fragColor = vec4(mix(color.rgb, mix(color.rgb, overlay.rgb, overlay.a), luminance_alpha_smooth), 1.0);
}
