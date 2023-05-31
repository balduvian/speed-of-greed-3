package game

object CCD {
	data class Line(val start: Vector, val end: Vector) {
		fun vector() = end - start

		fun interp(along: Float) = Vector.interp(start, end, along)

		operator fun plus(vector: Vector): Line {
			return Line(start + vector, end + vector)
		}

		fun onSide(point: Vector): Boolean {
			return vector().perpendicularLeft().dot(point - start) >= 0.0f
		}

		companion object {
			fun create(x0: Float, y0: Float, x1: Float, y1: Float): Line {
				return Line(Vector(x0, y0), Vector(x1, y1))
			}
		}
	}

	fun intersectionAlong(x0: Float, y0: Float, x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float): Float {
		return ((y0 - y2) * (x3 - x2) - (x0 - x2) * (y3 - y2)) /
			((x1 - x0) * (y3 - y2) - (y1 - y0) * (x3 - x2))
	}

	data class Collision(val along: Float, val wallVectors: ArrayList<Vector>, val movePoint: Vector)

	fun collideWall(move: Line, wall: Line, skekValue: Float): Collision? {
		/* moving in wall's collision direction */
		if (wall.vector().perpendicularRight().dot(move.vector()) <= 0.0f) return null

		val intersectionI = intersectionAlong(move.start.x, move.start.y, move.end.x, move.end.y, wall.start.x, wall.start.y, wall.end.x, wall.end.y)
		val intersectionJ = intersectionAlong(wall.start.x, wall.start.y, wall.end.x, wall.end.y, move.start.x, move.start.y, move.end.x, move.end.y)

		/* moving exactly parallel within the line */
		if (intersectionI.isNaN() || intersectionJ.isNaN()) return null

		if (intersectionI < 0.0f || intersectionI > 1.0f || intersectionJ < 0.0f || intersectionJ > 1.0f) return null

		val collisionPoint = move.interp(intersectionI)

		val wallVector = wall.vector()

		val moveSkek = Vector(0.0f, 0.0f)// wallVector.perpendicularLeft().setAsLength(skekValue)

		val movePoint = (move.end - collisionPoint).project(wallVector) + collisionPoint + moveSkek

		return Collision(intersectionI, arrayListOf(wallVector), movePoint)
	}

	fun findCollision(move: Line, walls: List<Line>, skekValue: Float): Collision? {
		var thisMove = move
		var thisCollision: Collision? = null

		val alreadyCollided = BooleanArray(walls.size) { false }

		while (true) {
			val newCollision = doCollisionRound(thisMove, walls, alreadyCollided, skekValue) ?: break
			if (thisCollision != null) newCollision.wallVectors.addAll(thisCollision.wallVectors)
			thisCollision = newCollision
			thisMove = Line(move.start, thisCollision.movePoint)
		}

		return thisCollision
	}

	private fun doCollisionRound(
		move: Line,
		walls: List<Line>,
		aleradyCollided: BooleanArray,
		skekValue: Float
	): Collision? {
		var bestIndex = -1
		var bestCollision: Collision? = null

		if (move.vector().length() == 0.0f) return null

		val newMoveStart = move.start - move.vector().setAsLength(skekValue)
		val newMove = Line(newMoveStart, move.end)

		for (i in walls.indices) {
			if (aleradyCollided[i]) continue

			val thisCollision = collideWall(newMove, walls[i], skekValue)
			if ((thisCollision != null) && ((bestCollision == null) || (thisCollision.along < bestCollision.along))) {
				bestCollision = thisCollision
				bestIndex = i
			}
		}

		if (bestCollision != null) aleradyCollided[bestIndex] = true
		return bestCollision
	}
}
