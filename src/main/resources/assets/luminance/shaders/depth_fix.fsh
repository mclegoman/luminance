#version 330

uniform sampler2D InSampler;
uniform sampler2D HandSampler;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    // set depth to 0 if its part of the hand - this matches iris's behaviour
    gl_FragDepth = texCoord.x;

    //float depth = texture(HandSampler, texCoord).r == 1.0 ? texture(InSampler, texCoord).r : 0;
    //fragColor = vec4(texture(InSampler, texCoord).r,  texture(HandSampler, texCoord).r, 0.5, 1.0);
    fragColor = vec4(texture(HandSampler, texCoord).rgb, 1.0);

    //texture(HandSampler, texCoord).r == 1.0 ? texture(InSampler, texCoord).r : 0;
}
