#version 150

in vec2 texCoord;
out vec4 fragColor;

uniform vec2 OutSize;

uniform sampler2D InSampler;
uniform sampler2D HandSampler;

void main() {
    // set depth to 0 if its part of the hand - this matches iris's behaviour
    gl_FragDepth = texture2D(HandSampler, texCoord).r == 1.0 ? texture2D(InSampler, texCoord).r : 0;
}
