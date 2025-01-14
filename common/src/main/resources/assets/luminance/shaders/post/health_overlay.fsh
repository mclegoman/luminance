#version 150

in vec2 texCoord;
out vec4 fragColor;

uniform sampler2D InSampler;
uniform float luminance_currentHealth_smooth;
uniform float luminance_maxHealth_smooth;
uniform float luminance_currentAbsorption_smooth;
uniform float luminance_maxAbsorption_smooth;
uniform float luminance_currentHurtTime_smooth;
uniform float luminance_maxHurtTime_smooth;
uniform float luminance_isWithered_smooth;
uniform float luminance_isPoisoned_smooth;

void main() {
    vec3 inputColor = texture(InSampler, texCoord).rgb;
    float health = (luminance_currentHealth_smooth + luminance_currentAbsorption_smooth) / ((luminance_maxHealth_smooth + luminance_maxAbsorption_smooth) * 0.5);
    float hurt = 1.0 - (luminance_currentHurtTime_smooth / luminance_maxHurtTime_smooth);
    float withered = luminance_isWithered_smooth * 0.5;
    float poisoned = luminance_isPoisoned_smooth * 0.5;

    vec3 color = inputColor;
    vec3 overlay = vec3(0.87843137254, 0.03921568627, 0.05882352941);
    vec3 wither = vec3(0.23921568627, 0.03137254901, 0.03137254901);
    vec3 poison = vec3(0.45882352941, 0.87843137254, 0.03921568627);

    color = mix(color, mix(color, wither, smoothstep(0.0, 0.5, distance(texCoord, vec2(0.5, 0.5)))), min(withered, 1.0));
    color = mix(color, mix(color, poison, smoothstep(0.0, 0.5, distance(texCoord, vec2(0.5, 0.5)))), min(poisoned, 1.0));
    color = mix(mix(color, overlay, smoothstep(0.0, 0.5, distance(texCoord, vec2(0.5, 0.5)))), color, min(health, 1.0));
    color = mix(mix(color, overlay, smoothstep(0.0, 0.5, distance(texCoord, vec2(0.5, 0.5)))), color, min(hurt, 1.0));

    fragColor = vec4(color, 1.0);
}