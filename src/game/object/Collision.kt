package game.`object`

import game.CCD
import game.Util
import org.joml.Vector2i

typealias LineFunc = (x: Float, y: Float, worldTileX: Float, worldTileY: Float) -> Float

class Collision {
	var hit: Boolean
	var distance = Float.MIN_VALUE
	var value = 0.0f

	constructor() {
		hit = false
	}

	constructor(distance: Float, value: Float) {
		hit = true
		this.distance = distance
		this.value = value
	}

	/* block model */
	class BlockModel(var lineFuncs: Array<LineFunc>, var hasFace: BooleanArray)
	companion object {
		/* line funcs */
		fun leftFlat(x: Float, y: Float, worldTileX: Float, worldTileY: Float): Float {
			return worldTileX
		}

		fun downFlat(x: Float, y: Float, worldTileX: Float, worldTileY: Float): Float {
			return worldTileY
		}

		fun rightFlat(x: Float, y: Float, worldTileX: Float, worldTileY: Float): Float {
			return worldTileX + Level.TILE_SIZE
		}

		fun upFlat(x: Float, y: Float, worldTileX: Float, worldTileY: Float): Float {
			return worldTileY + Level.TILE_SIZE
		}

		fun topSlopRight(x: Float, y: Float, worldTileX: Float, worldTileY: Float): Float {
			return Util.interp(
				worldTileY,
				worldTileY + Level.TILE_SIZE,
				Util.invInterp(worldTileX, worldTileX + Level.TILE_SIZE, x)
			)
		}

		private val fullBlockModel = BlockModel(
			arrayOf(
				{ x: Float, y: Float, worldTileX: Float, worldTileY: Float ->
					leftFlat(x, y, worldTileX, worldTileY)
				},
				{ x: Float, y: Float, worldTileX: Float, worldTileY: Float ->
					downFlat(x, y, worldTileX, worldTileY)
				},
				{ x: Float, y: Float, worldTileX: Float, worldTileY: Float ->
					rightFlat(x, y, worldTileX, worldTileY)
				},
				{ x: Float, y: Float, worldTileX: Float, worldTileY: Float ->
					upFlat(x, y, worldTileX, worldTileY)
				}
			), booleanArrayOf(true, true, true, true)
		)
		private val slopeRightBlockModel = BlockModel(
			arrayOf(
				{ x: Float, y: Float, worldTileX: Float, worldTileY: Float ->
					leftFlat(x, y, worldTileX, worldTileY)
				},
				{ x: Float, y: Float, worldTileX: Float, worldTileY: Float ->
					downFlat(x, y, worldTileX, worldTileY)
				},
				{ x: Float, y: Float, worldTileX: Float, worldTileY: Float ->
					rightFlat(x, y, worldTileX, worldTileY)
				},
				{ x: Float, y: Float, worldTileX: Float, worldTileY: Float ->
					topSlopRight(x, y, worldTileX, worldTileY)
				}
			), booleanArrayOf(false, true, true, true)
		)

		/* */
		private fun intnernalInsideBlock(
			blockModel: BlockModel,
			x: Float,
			y: Float,
			worldTileX: Float,
			worldTileY: Float,
			fullX: Boolean,
			fullY: Boolean
		): Boolean {
			val left = blockModel.lineFuncs[DIRECTION_RIGHT](x, y, worldTileX, worldTileY)
			val right = blockModel.lineFuncs[DIRECTION_LEFT](x, y, worldTileX, worldTileY)
			val down = blockModel.lineFuncs[DIRECTION_UP](x, y, worldTileX, worldTileY)
			val up = blockModel.lineFuncs[DIRECTION_DOWN](x, y, worldTileX, worldTileY)
			val horizontalPart = if (fullX) x in left..right else x > left && x < right
			val verticalPart = if (fullY) y in down..up else y > down && y < up
			return horizontalPart && verticalPart
		}

		fun insideBlock(
			blockModel: BlockModel,
			x: Float,
			y: Float,
			worldTileX: Float,
			worldTileY: Float,
			direction: Int
		): Boolean {
			if (!blockModel.hasFace[direction]) return false
			val vertical = isVertical(direction)
			return intnernalInsideBlock(
				blockModel,
				x,
				y,
				worldTileX,
				worldTileY,
				!vertical || blockModel === slopeRightBlockModel,
				vertical
			)
		}

		fun blockModel(tile: Int): BlockModel? {
			return when (tile) {
				Level.TILE_GROUND, Level.TILE_HELL_GROUND -> {
					fullBlockModel
				}

				Level.TILE_SLOPE_RIGHT -> {
					slopeRightBlockModel
				}

				else -> {
					null
				}
			}
		}

		private const val DIRECTION_RIGHT = 0
		private const val DIRECTION_UP = 1
		private const val DIRECTION_LEFT = 2
		private const val DIRECTION_DOWN = 3
		var directionOffsets: Array<Vector2i> = arrayOf<Vector2i>(
			Vector2i(1, 0),
			Vector2i(0, 1),
			Vector2i(-1, 0),
			Vector2i(0, -1)
		)
		var oppositeDirection = intArrayOf(
			DIRECTION_LEFT,
			DIRECTION_DOWN,
			DIRECTION_RIGHT,
			DIRECTION_UP
		)

		private fun isVertical(direction: Int): Boolean {
			return direction == DIRECTION_UP || direction == DIRECTION_DOWN
		}

		fun internalCollide(level: Level, x: Float, y: Float, direction: Int): Collision {
			var groundData = Collision()
			val baseTileX = Math.floor((x / Level.TILE_SIZE).toDouble()).toInt()
			val baseTileY = Math.floor((y / Level.TILE_SIZE).toDouble()).toInt()
			for (j in -1..1) {
				for (i in -1..1) {
					val tileX = baseTileX + i
					val tileY = baseTileY + j
					val worldTileX = tileX * Level.TILE_SIZE
					val worldTileY = tileY * Level.TILE_SIZE
					val tile = level.access(tileX, tileY)
					val blockModel = blockModel(tile)
					if (blockModel != null) {
						val availableDirection: Vector2i = directionOffsets[oppositeDirection[direction]]
						if (blockModel(
								level.access(
									tileX + availableDirection.x,
									tileY + availableDirection.y
								)
							) == null
						) {
							if (insideBlock(blockModel, x, y, worldTileX, worldTileY, direction)) {
								val vertical = isVertical(direction)
								val distance = Math.abs(
									blockModel.lineFuncs[direction](
										x,
										y,
										worldTileX,
										worldTileY
									) - if (vertical) y else x
								)
								if (distance > groundData.distance) {
									groundData = Collision(
										distance,
										blockModel.lineFuncs[direction](x, y, worldTileX, worldTileY)
									)
								}
							}
						}
					}
				}
			}
			return groundData
		}

		fun collideDown(level: Level, x: Float, y: Float): Collision {
			return internalCollide(level, x, y, DIRECTION_DOWN)
		}

		fun collideUp(level: Level, x: Float, y: Float): Collision {
			return internalCollide(level, x, y, DIRECTION_UP)
		}

		fun collideLeft(level: Level, x: Float, y: Float): Collision {
			return internalCollide(level, x, y, DIRECTION_LEFT)
		}

		fun collideRight(level: Level, x: Float, y: Float): Collision {
			return internalCollide(level, x, y, DIRECTION_RIGHT)
		}
	}
}
