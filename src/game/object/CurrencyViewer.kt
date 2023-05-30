package game.`object`

import engine.Camera
import game.Assets

object CurrencyViewer {
	private const val NUMBER_SIZE = 24.0f
	fun render(camera: Camera, num: Int) {
		val str = Integer.toString(num)
		val firstDigit: Int
		val secondDigit: Int
		if (str.length != 2) {
			firstDigit = 0
			secondDigit = str[0].code - '0'.code
		} else {
			firstDigit = str[0].code - '0'.code
			secondDigit = str[1].code - '0'.code
		}
		Assets.numberTextures[firstDigit].bind()
		Assets.textureShader.enable().setMVP(
			camera.getMP(
				camera.width - NUMBER_SIZE * 2,
				camera.height - NUMBER_SIZE,
				NUMBER_SIZE,
				NUMBER_SIZE
			)
		)
		Assets.rect.render()
		Assets.numberTextures[secondDigit].bind()
		Assets.textureShader.enable().setMVP(
			camera.getMP(
				camera.width - NUMBER_SIZE,
				camera.height - NUMBER_SIZE,
				NUMBER_SIZE,
				NUMBER_SIZE
			)
		)
		Assets.rect.render()
	}
}
