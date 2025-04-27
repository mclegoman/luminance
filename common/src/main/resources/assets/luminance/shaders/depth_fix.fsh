#version 150

in vec2 texCoord;
out vec4 fragColor;

uniform vec2 OutSize;

uniform sampler2D InSampler;

void main() {
    fragColor = vec4(1.0, texCoord.x, texCoord.y, 1.0);

    gl_FragDepth = fract(texCoord.x*10)*100*texCoord.y;//texture2D(InSampler, texCoord).r;
}
