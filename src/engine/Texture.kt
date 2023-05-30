package engine

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12
import org.lwjgl.opengl.GL13
import java.io.File
import javax.imageio.ImageIO

class Texture(path: String) {
	private var id = 0
	private var width = 0
	private var height = 0

	init {
		val bi = ImageIO.read(File(path))
		width = bi.width
		height = bi.height
		val pixels = bi.getRGB(0, 0, width, height, null, 0, width)
		val buffer = BufferUtils.createByteBuffer(width * height * 4)
		for (i in 0 until height) {
			for (j in 0 until width) {
				val pixel = pixels[i * width + j]
				buffer.put((pixel shr 16 and 0xFF).toByte()) // Red
				buffer.put((pixel shr 8 and 0xFF).toByte()) // Green
				buffer.put((pixel and 0xFF).toByte()) // Blue
				buffer.put((pixel shr 24 and 0xFF).toByte()) // Alpha
			}
		}
		buffer.flip()
		id = GL11.glGenTextures()
		bind()
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST)
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST)
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE)
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE)
		GL11.glTexImage2D(
			GL11.GL_TEXTURE_2D,
			0,
			GL11.GL_RGBA,
			width,
			height,
			0,
			GL11.GL_RGBA,
			GL11.GL_UNSIGNED_BYTE,
			buffer
		)
		unbind()
	}

	fun repeat(): Texture {
		bind()
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT)
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT)
		unbind()
		return this
	}

	fun bind(index: Int = 0) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + index)
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id)
	}

	fun unbind(index: Int = 0) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + index)
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)
	}

	fun destroy() {
		GL11.glDeleteTextures(id)
	}
}
