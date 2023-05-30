package engine.audio

import org.lwjgl.openal.AL10
import java.io.File

class Sound(file: File) {
	private val buffer: Int = AL10.alGenBuffers()
	private val sourceId: Int = AL10.alGenSources()

	init {
		val waveData = WaveData.create(file)
		AL10.alBufferData(buffer, waveData.format, waveData.data, waveData.sampleRate.toInt())
		AL10.alSourcei(sourceId, AL10.AL_BUFFER, buffer)
		AL10.alSourcef(sourceId, AL10.AL_GAIN, 1f)
		AL10.alSourcef(sourceId, AL10.AL_PITCH, 1f)
	}

	fun play(loop: Boolean) {
		AL10.alSourcei(sourceId, AL10.AL_LOOPING, if (loop) 1 else 0)
		AL10.alSource3f(sourceId, AL10.AL_POSITION, 0f, 0f, 0f)
		AL10.alSourcePlay(sourceId)
	}

	fun stop() {
		AL10.alSourceStop(sourceId)
	}

	fun setVolume(volume: Float) {
		AL10.alSourcef(sourceId, AL10.AL_GAIN, volume)
	}

	fun destroy() {
		AL10.alDeleteBuffers(buffer)
		AL10.alDeleteSources(sourceId)
	}
}
