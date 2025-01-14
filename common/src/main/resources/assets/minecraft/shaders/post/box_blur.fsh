#version 150

uniform sampler2D InSampler;

in vec2 texCoord;
in vec2 sampleStep;

uniform float Radius;
uniform float RadiusMultiplier;

out vec4 fragColor;

// This shader relies on GL_LINEAR sampling to reduce the amount of texture samples in half.
// Instead of sampling each pixel position with a step of 1 we sample between pixels with a step of 2.
// In the end we sample the last pixel with a half weight, since the amount of pixels to sample is always odd (actualRadius * 2 + 1).
void main() {
    vec3 blurred = vec3(0.0);
    float actualRadius = round(Radius * RadiusMultiplier);
    for (float a = -actualRadius + 0.5; a <= actualRadius; a += 2.0) {
        blurred += texture(InSampler, texCoord + sampleStep * a).rgb;
    }
    blurred += texture(InSampler, texCoord + sampleStep * actualRadius).rgb / 2.0;
    fragColor = vec4(blurred / (actualRadius + 0.5), 1.0);

    // Below is vanilla's version for comparison
    // The difference is that this one is forced to have an alpha of 1.0
    // This change fixes the lower part of the sky going black when the shader is rendered at a slightly different time to when it normally is
    // And it should have no effect when blur is used elsewhere
    // Alternatively shaders should be encouraged to use a "luminance:box_blur" that works like this and vanillas is left alone -
    // - and then we sneakily make the spider that luminance registers actually be a copy of spider that uses the fixed version
    // But that isnt really ideal either, so as much as I (Nettakrim) dont like touching anything vanilla, this is perhaps the best solution
    // My theory for why the alpha does this is that the lower bit of the sky renders with an alpha of 0, and then normally a later pass puts everything to 1

    //vec4 blurred = vec4(0.0);
    //float actualRadius = round(Radius * RadiusMultiplier);
    //for (float a = -actualRadius + 0.5; a <= actualRadius; a += 2.0) {
    //    blurred += texture(InSampler, texCoord + sampleStep * a);
    //}
    //blurred += texture(InSampler, texCoord + sampleStep * actualRadius) / 2.0;
    //fragColor = blurred / (actualRadius + 0.5);
}
