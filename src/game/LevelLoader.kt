package game

import game.`object`.CoinLevel
import game.`object`.CoinPlacement
import game.`object`.Level
import game.`object`.Theme
import org.joml.Vector2i
import java.io.File

object LevelLoader {
	fun loadAll(folder: File): Array<Level> {
		val files = folder.listFiles() as Array<File>
		files.sortWith { file0: File, file1: File -> file0.toPath().fileName.toString().compareTo(file1.toPath().fileName.toString()) }

		return files.map { load(it.readLines()) }.toTypedArray()
	}

	private fun load(lines: List<String>): Level {
		if (lines.size < 2) throw Exception("map file too short")
		val flags = lines[0].trim().split(";").map { it.lowercase() }
		val width = lines[1].length
		val height = lines.size - 1
		var safeX = 0
		val level = IntArray(width * height)
		val coins: ArrayList<CoinPlacement> = ArrayList<CoinPlacement>()
		var spawnX = -1
		var spawnY = -1
		var drugDealerPos: Vector2i = Vector2i(0, 0)

		for (j in 0 until height) {
			val line = lines[height - j]
			if (line.length != width) throw Exception("inconsistent map width on line " + (j + 2))
			for (i in 0 until width) {
				when (line[i]) {
					'#' -> level[Util.indexOf(i, j, width)] = Level.TILE_GROUND
					'@' -> level[Util.indexOf(i, j, width)] = Level.TILE_HELL_GROUND
					'*' -> level[Util.indexOf(i, j, width)] = Level.TILE_LAVA
					'/' -> level[Util.indexOf(i, j, width)] = Level.TILE_SLOPE_RIGHT
					'\\' -> level[Util.indexOf(i, j, width)] = Level.TILE_SLOPE_LEFT
					'|' -> safeX = i
					'p' -> {
						spawnX = i
						spawnY = j
					}
					's' -> drugDealerPos = Vector2i(i, j)
					'o' -> coins.add(CoinPlacement(i, j, CoinLevel.GOLD))
					'b' -> coins.add(CoinPlacement(i, j, CoinLevel.HEAVY))
					'd' -> coins.add(CoinPlacement(i, j, CoinLevel.DELTA))
					'k' -> coins.add(CoinPlacement(i, j, CoinLevel.PASS))
				}
			}
		}

		if (spawnX == -1) {
			throw Exception("no player spawn found (p)")
		}

		if (coins.none { it.coinLevel == CoinLevel.PASS }) {
			throw Exception("no exit coin (k)")
		}

		return Level(
			width,
			height,
			spawnX,
			spawnY,
			coins.toArray<CoinPlacement>(arrayOf<CoinPlacement>()),
			flags.contains("wall"),
			safeX,
			if (flags.contains("portal")) Theme.PORTAL_THEME else Theme.HELL_THEME,
			level,
			drugDealerPos
		)
	}
}
