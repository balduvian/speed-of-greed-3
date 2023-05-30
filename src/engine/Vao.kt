package engine

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30

class Vao(vertices: FloatArray, indices: IntArray) {
	private var numAttribs = 0
	private val vao: Int = GL30.glGenVertexArrays()
	private val ibo: Int
	private val count: Int
	private val vbos = IntArray(15)

	init {
		GL30.glBindVertexArray(vao)
		addAttrib(vertices, 3)
		ibo = GL15.glGenBuffers()
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ibo)
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW)
		count = indices.size
	}

	fun addAttrib(data: FloatArray, size: Int): Vao {
		val vbo = GL15.glGenBuffers()
		vbos[numAttribs] = vbo
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo)
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW)
		GL20.glVertexAttribPointer(numAttribs, size, GL11.GL_FLOAT, false, 0, 0)
		++numAttribs
		return this
	}

	fun render() {
		GL30.glBindVertexArray(vao)
		for (i in 0 until numAttribs) GL20.glEnableVertexAttribArray(i)
		GL11.glDrawElements(GL11.GL_TRIANGLES, count, GL11.GL_UNSIGNED_INT, 0)
		for (i in 0 until numAttribs) GL20.glDisableVertexAttribArray(i)
	}

	fun destroy() {
		for (vbo in vbos) GL15.glDeleteBuffers(vbo)
		GL15.glDeleteBuffers(ibo)
		GL30.glDeleteVertexArrays(vao)
	}
}
