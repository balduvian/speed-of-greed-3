package game.`object`

import engine.Camera
import engine.Texture
import game.Assets
import game.Util
import org.joml.Vector2i
import kotlin.math.ceil
import kotlin.math.floor

class Level(
	var width: Int,
	var height: Int,
	var spawnX: Int,
	var spawnY: Int,
	var coinPlacements: Array<CoinPlacement>,
	var hasWall: Boolean,
	var safeX: Int,
	var theme: Theme,
	var level: IntArray,
	val drugDealerPos: Vector2i
) {
	var coins = ArrayList<Coin>()

	fun reset() {
		coins = coinPlacements.map {
			Coin(
				it.x * TILE_SIZE + TILE_SIZE / 2.0f,
				it.y * TILE_SIZE + TILE_SIZE / 2.0f,
				it.coinLevel
			)
		} as ArrayList<Coin>
	}

	fun access(x: Int, y: Int) = level[Util.indexOf(
		x.coerceAtLeast(0).coerceAtMost(width - 1),
		y.coerceAtLeast(0).coerceAtMost(height - 1),
		width
	)]

	fun render(camera: Camera) {
		val left = floor((camera.x / TILE_SIZE)).toInt()
		val right = ceil(((camera.x + camera.width) / TILE_SIZE)).toInt()
		val down = floor((camera.y / TILE_SIZE)).toInt()
		val up = ceil(((camera.y + camera.height) / TILE_SIZE)).toInt()

		for (x in left..right) {
			for (y in down..up) {
				val tile = access(x, y)
				var texture: Texture? = null
				when (tile) {
					TILE_GROUND -> {
						texture = Assets.brickTexture
					}
					TILE_HELL_GROUND -> {
						texture = Assets.hellGroundTexture
					}
					TILE_SLOPE_RIGHT -> {
						texture = Assets.slopeRightTexture
					}
					TILE_LAVA -> {
						texture = Assets.lavaTexture
					}
				}
				if (texture != null) {
					texture.bind()
					Assets.textureShader.enable().setMVP(
						camera.getMVP(
							x * TILE_SIZE,
							y * TILE_SIZE,
							TILE_SIZE,
							TILE_SIZE
						)
					)
					Assets.rect.render()
				}
			}
		}
		for (coin in coins) {
			coin.render(camera)
		}
	}

	companion object {
		const val TILE_EMPTY = 0
		const val TILE_GROUND = 1
		const val TILE_HELL_GROUND = 2
		const val TILE_LAVA = 3
		const val TILE_SLOPE_RIGHT = 4
		const val TILE_SLOPE_LEFT = 5
		const val TILE_SIZE = 32.0f
	}
}
