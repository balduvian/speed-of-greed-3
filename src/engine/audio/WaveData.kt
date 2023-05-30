package engine.audio

import org.lwjgl.openal.AL10
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.nio.ByteBuffer
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem

class WaveData private constructor(private val audioStream: AudioInputStream) {
	val format: Int
	val sampleRate: Float
	val totalBytes: Int
	val bytesPerFrame: Int
	val dataArray: ByteArray
	val data: ByteBuffer

	init {
		val audioFormat = audioStream.format
		format = getOpenAlFormat(audioFormat.channels, audioFormat.sampleSizeInBits)

		sampleRate = audioFormat.sampleRate
		bytesPerFrame = audioFormat.frameSize
		totalBytes = (audioStream.frameLength * bytesPerFrame).toInt()

		dataArray = ByteArray(totalBytes)
		audioStream.read(dataArray, 0, totalBytes)

		data = ByteBuffer.wrap(dataArray)
	}

	private fun dispose() {
		audioStream.close()
		data.clear()
	}

	companion object {
		fun create(file: File): WaveData {
			val stream: InputStream = FileInputStream(file)
			val bufferedInput: InputStream = BufferedInputStream(stream)
			val audioStream: AudioInputStream = AudioSystem.getAudioInputStream(bufferedInput)
			return WaveData(audioStream)
		}

		private fun getOpenAlFormat(channels: Int, bitsPerSample: Int): Int {
			return if (channels == 1) {
				if (bitsPerSample == 8) AL10.AL_FORMAT_MONO8 else AL10.AL_FORMAT_MONO16
			} else {
				if (bitsPerSample == 8) AL10.AL_FORMAT_STEREO8 else AL10.AL_FORMAT_STEREO16
			}
		}
	}
}
