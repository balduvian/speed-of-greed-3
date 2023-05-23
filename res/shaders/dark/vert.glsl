#version 330 core

uniform mat4 mvp;

layout (location = 0) in vec3 vertex;
layout (location = 1) in vec2 texCoord;

out vec2 outTexCoord;

void main() {
	outTexCoord = texCoord;
	gl_Position = mvp * vec4(vertex, 1.0);
}