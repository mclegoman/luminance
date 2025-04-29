#version 150

in vec2 texCoord;

uniform sampler2D InSampler;
uniform sampler2D HandSampler;

void main() {
    // set depth to 0 if its part of the hand - this matches iris's behaviour
    gl_FragDepth = texture(HandSampler, texCoord).r == 1.0 ? texture(InSampler, texCoord).r : 0;
}
