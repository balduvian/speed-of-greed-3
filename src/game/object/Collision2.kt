package game.`object`

import game.CCD
import game.Vector
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

object Collision2 {
	enum class Direction(val offsetX: Int, val offsetY: Int) {
		RIGHT(1, 0),
		UP(0, 1),
		LEFT(-1, 0),
		DOWN(0, -1);

		fun opposite() = values()[(ordinal + 2) % 4]
		fun rotateLeft() = values()[(ordinal + 1) % 4]
		fun rotateRight() = values()[(ordinal - 1) % 4]
	}

	data class BlockModel(val lines: Array<CCD.Line?>, val fullFace: BooleanArray) {
		fun getLine(direction: Direction) = lines[direction.ordinal]
		fun isFullFace(direction: Direction) = fullFace[direction.ordinal]
	}

	val fullBlockModel = BlockModel(arrayOf(
		CCD.Line.create(Level.TILE_SIZE, Level.TILE_SIZE, Level.TILE_SIZE, 0.0f),
		CCD.Line.create(0.0f, Level.TILE_SIZE, Level.TILE_SIZE, Level.TILE_SIZE),
		CCD.Line.create(0.0f, 0.0f, 0.0f, Level.TILE_SIZE),
		CCD.Line.create(Level.TILE_SIZE, 0.0f, 0.0f, 0.0f),
	), booleanArrayOf(true, true, true, true))

	val slopeRightBlockModel = BlockModel(arrayOf(
		CCD.Line.create(Level.TILE_SIZE, Level.TILE_SIZE, Level.TILE_SIZE, 0.0f),
		CCD.Line.create(0.0f, 0.0f, Level.TILE_SIZE, Level.TILE_SIZE),
		null,
		CCD.Line.create(Level.TILE_SIZE, 0.0f, 0.0f, Level.TILE_SIZE),
	), booleanArrayOf(true, false, false, true))

	fun getBlockModel(tile: Int) = when (tile) {
		Level.TILE_GROUND, Level.TILE_HELL_GROUND -> fullBlockModel
		Level.TILE_SLOPE_RIGHT -> slopeRightBlockModel
		else -> null
	}

	fun collectLines(level: Level, xRange: IntRange, yRange: IntRange, overshoot: Float): List<CCD.Line> {
		val lines = ArrayList<CCD.Line>()

		for (y in yRange) {
			for (x in xRange) {
				val blockModel = getBlockModel(level.access(x, y)) ?: continue
				val base = Vector(x * Level.TILE_SIZE, y * Level.TILE_SIZE)

				fun addLine(direction: Direction) {
					blockModel.getLine(direction)?.let { line ->
						if (getBlockModel(level.access(x + direction.offsetX, y + direction.offsetY))?.let { block ->
								!block.isFullFace(direction.opposite())
							} != false
						) {
							//val leftDirection = direction.rotateLeft()

							//if (getBlockModel(level.access(x + leftDirection.offsetX, y + leftDirection.offsetY))?.let { block -> block. })
							val lineVector = line.vector()
							val overshootLine = CCD.Line(line.start - lineVector.asLength(overshoot), line.end + lineVector.asLength(overshoot))
							lines.add(overshootLine + base)
						}
					}
				}

				addLine(Direction.RIGHT)
				addLine(Direction.UP)
				addLine(Direction.LEFT)
				addLine(Direction.DOWN)
			}
		}

		return lines
	}

	fun getRange(start: Float, end: Float, overshoot: Float): IntRange {
		val low = min(start, end)
		val high = max(start, end)
		return floor((low - overshoot) / Level.TILE_SIZE).toInt()..floor((high + overshoot) / Level.TILE_SIZE).toInt()
	}
}
