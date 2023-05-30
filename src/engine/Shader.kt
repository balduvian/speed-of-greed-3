package engine

import org.joml.Matrix4f
import org.lwjgl.opengl.GL20
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException

class Shader(vertPath: String, fragPath: String, uniformNames: Array<String>) {
	private val program: Int = GL20.glCreateProgram()
	private val mvpLoc: Int
	private val uniformLocs: IntArray
	private val internalMVP = FloatArray(16)

	init {
		val vert = loadShader(vertPath, GL20.GL_VERTEX_SHADER)
		val frag = loadShader(fragPath, GL20.GL_FRAGMENT_SHADER)
		GL20.glAttachShader(program, vert)
		GL20.glAttachShader(program, frag)
		GL20.glLinkProgram(program)
		GL20.glDetachShader(program, vert)
		GL20.glDetachShader(program, frag)
		GL20.glDeleteShader(vert)
		GL20.glDeleteShader(frag)
		mvpLoc = GL20.glGetUniformLocation(program, "mvp")
		uniformLocs = IntArray(uniformNames.size)
		for (i in uniformNames.indices) {
			uniformLocs[i] = GL20.glGetUniformLocation(program, uniformNames[i])
		}
	}

	private fun loadShader(path: String, type: Int): Int {
		val file = StringBuilder()
		try {
			val reader = BufferedReader(FileReader(path))
			var line: String?
			while (reader.readLine().also { line = it } != null) {
				file.append(line).append('\n')
			}
			reader.close()
		} catch (e: IOException) {
			e.printStackTrace()
		}
		val source = file.toString()
		val shader = GL20.glCreateShader(type)
		GL20.glShaderSource(shader, source)
		GL20.glCompileShader(shader)
		if (GL20.glGetShaderi(
				shader,
				GL20.GL_COMPILE_STATUS
			) != 1
		) throw RuntimeException("Failed to compile shader: " + path + "! " + GL20.glGetShaderInfoLog(shader))
		return shader
	}

	fun setMVP(matrix: Matrix4f): Shader {
		GL20.glUniformMatrix4fv(mvpLoc, false, matrix[internalMVP])
		return this
	}

	fun uniform1f(index: Int, v: Float): Shader {
		GL20.glUniform1f(uniformLocs[index], v)
		return this
	}

	fun uniform2f(index: Int, v0: Float, v1: Float): Shader {
		GL20.glUniform2f(uniformLocs[index], v0, v1)
		return this
	}

	fun uniform3f(index: Int, v0: Float, v1: Float, v2: Float): Shader {
		GL20.glUniform3f(uniformLocs[index], v0, v1, v2)
		return this
	}

	fun uniform4f(index: Int, v0: Float, v1: Float, v2: Float, v3: Float): Shader {
		GL20.glUniform4f(uniformLocs[index], v0, v1, v2, v3)
		return this
	}

	fun enable(): Shader {
		GL20.glUseProgram(program)
		return this
	}

	fun disable() {
		GL20.glUseProgram(0)
	}

	fun destroy() {
		GL20.glDeleteProgram(program)
	}
}
