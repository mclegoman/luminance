#version 330

in vec4 Position;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform FxaaConfig {
    float SubPixelShift;
    float SpanMax;
    float ReduceMul;
};

out vec2 texCoord;
out vec4 posPos;

void main() {
    vec2 uv = vec2((gl_VertexID << 1) & 2, gl_VertexID & 2);
    vec4 pos = vec4(uv * vec2(2, 2) + vec2(-1, -1), 0, 1);

    gl_Position = pos;
    texCoord = uv;

    posPos.xy = texCoord;
    posPos.zw = texCoord - (1.0/OutSize * vec2(0.5 + SubPixelShift));
}
