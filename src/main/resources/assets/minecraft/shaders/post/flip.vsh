#version 330

layout(std140) uniform FlipConfig {
    vec2 X;
    vec2 Y;
    vec2 Offset;
};

in vec4 Position;

out vec2 texCoord;

void main(){
    vec2 uv = vec2((gl_VertexID << 1) & 2, gl_VertexID & 2);
    vec4 pos = vec4(uv * vec2(2, 2) + vec2(-1, -1), 0, 1);

    gl_Position = pos;
    texCoord = X * uv.x + Y * uv.y + Offset;
}
