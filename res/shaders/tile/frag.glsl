#version 330 core

uniform sampler2D sampler;

in vec2 outTexCoord;

out vec4 color;

void main() {
	color = texture(sampler, outTexCoord);
}