#version 150

in vec2 texCoord;
out vec4 fragColor;

uniform vec2 OutSize;

uniform sampler2D InSampler;

out float gl_FragDepth;

void main() {
    gl_FragDepth = texture2D(InSampler, texCoord).r;
}
