package engine.audio

import org.lwjgl.openal.AL
import org.lwjgl.openal.ALC
import org.lwjgl.openal.ALC10
import org.lwjgl.openal.ALCCapabilities
import java.nio.ByteBuffer
import java.nio.IntBuffer

class ALManagement {
	private val device: Long = ALC10.alcOpenDevice(null as ByteBuffer?)
	private val context: Long
	private val deviceCaps: ALCCapabilities

	init {
		check(device != 0L) { "Failed to open the default device." }
		deviceCaps = ALC.createCapabilities(device)
		context = ALC10.alcCreateContext(device, null as IntBuffer?)
		check(context != 0L) { "Failed to create an OpenAL context." }
		ALC10.alcMakeContextCurrent(context)
		AL.createCapabilities(deviceCaps)
	}

	fun destroy() {
		ALC10.alcDestroyContext(context)
		ALC10.alcCloseDevice(device)
	}
}
