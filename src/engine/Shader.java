package engine;

import org.joml.Matrix4f;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
	private int program;
	private int mvpLoc;

	private int[] uniformLocs;

	private float[] internalMVP = new float[16];

	public Shader(String vertPath, String fragPath, String[] uniformNames) {
		program = glCreateProgram();
		
		int vert = loadShader(vertPath, GL_VERTEX_SHADER);
		int frag = loadShader(fragPath, GL_FRAGMENT_SHADER);
		
		glAttachShader(program, vert);
		glAttachShader(program, frag);
		
		glLinkProgram(program);
		
		glDetachShader(program, vert);
		glDetachShader(program, frag);
		
		glDeleteShader(vert);
		glDeleteShader(frag);
		
		mvpLoc = glGetUniformLocation(program, "mvp");

		uniformLocs = new int[uniformNames.length];
		for (var i = 0; i < uniformNames.length; ++i) {
			uniformLocs[i] = glGetUniformLocation(program, uniformNames[i]);
		}
	}
	
	private int loadShader(String path, int type) {
		StringBuilder file = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(path));
			String line;
			while((line = reader.readLine()) != null) {
				file.append(line).append('\n');
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String source = file.toString();
		int shader = glCreateShader(type);
		glShaderSource(shader, source);
		glCompileShader(shader);
		if(glGetShaderi(shader, GL_COMPILE_STATUS) != 1)
			throw new RuntimeException("Failed to compile shader: " + path + "! " + glGetShaderInfoLog(shader));
		return shader;
	}

	public Shader setMVP(Matrix4f matrix) {
		glUniformMatrix4fv(mvpLoc, false, matrix.get(internalMVP));
		return this;
	}

	public Shader uniform1f(int index, float v) {
		glUniform1f(uniformLocs[index], v);
		return this;
	}

	public Shader uniform2f(int index, float v0, float v1) {
		glUniform2f(uniformLocs[index], v0, v1);
		return this;
	}

	public Shader uniform3f(int index, float v0, float v1, float v2) {
		glUniform3f(uniformLocs[index], v0, v1, v2);
		return this;
	}

	public Shader uniform4f(int index, float v0, float v1, float v2, float v3) {
		glUniform4f(uniformLocs[index], v0, v1, v2, v3);
		return this;
	}

	public Shader enable() {
		glUseProgram(program);
		return this;
	}
	
	public void disable() {
		glUseProgram(0);
	}
	
	public void destroy() {
		glDeleteProgram(program);
	}
}
