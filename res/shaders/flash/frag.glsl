#version 330 core

uniform vec4 color;

in vec2 outTexCoord;

out vec4 outColor;

void main() {
	vec2 centerCoord = (outTexCoord - vec2(0.5, 0.5)) * vec2(2.0, 2.0);

	outColor = mix(
		color,
		color * vec4(1.0f, 1.0f, 1.0f, 0.0f),
		centerCoord.x * centerCoord.x + centerCoord.y * centerCoord.y
	);
}