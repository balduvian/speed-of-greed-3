package game.`object`

import engine.Camera
import engine.Texture
import game.Assets
import kotlin.math.sqrt

class Coin(var x: Float, var y: Float, var tier: CoinLevel) {
	var collected = false

	fun distTo(px: Float, py: Float) = sqrt((px - x) * (px - x) + (py - y) * (py - y))

	fun render(camera: Camera) {
		if (collected) return
		val texture: Texture = when (tier) {
			CoinLevel.GOLD -> Assets.coinTexture
			CoinLevel.HEAVY -> Assets.blueCoinTexture
			CoinLevel.DELTA -> Assets.superCoinTexture
			else -> Assets.keyCoinTexture
		}
		texture.bind()
		Assets.textureShader.enable().setMVP(
			camera.getMVPCentered(
				x,
				y,
				SIZE,
				SIZE
			)
		)
		Assets.rect.render()
	}

	companion object {
		private const val SIZE = 32.0f
	}
}
