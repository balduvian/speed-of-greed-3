#version 330 core

uniform vec4 color;

in vec2 outTexCoord;

out vec4 outColor;

void main() {
	outColor = mix(color, color * vec4(1.0, 1.0, 1.0, 0.0), outTexCoord.x);
}