package game.`object`

import engine.Camera
import engine.Window
import game.Assets
import game.CCD
import game.Util
import game.Vector
import org.lwjgl.glfw.GLFW
import kotlin.math.PI
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sqrt

class Player(var x: Float, var y: Float, var tilesPerSecond: Int, var jumpRange: Int) {
	var yVel = 0f
	var xVel = 0f
	var gravity = 300f
	var acceleration = 300f
	var currency = 0
	var onGround = false

	val floorAngle = 2.0f * PI.toFloat() / 7.0f

	fun calcJumpForce(): Float {
		/*
		 * let h be max jump height, g be gravity, x be time
		 * equation for jumping parabola: h - gx^2
		 *
		 * solve for when height = 0, get x = -sqrt(h / g)
		 *
		 * the derivative is -2gx
		 *
		 * plug in -2g(-sqrt(h/g)) to get jump boost at t = 0 to achieve max height given gravity
		 *
		 * = 2g * sqrt(h / g)
		 */
		return 2.0f * gravity * sqrt((jumpRange + 0.5f) * Level.TILE_SIZE / gravity)
	}

	fun centerX(): Float {
		return x
	}

	fun centerY(): Float {
		return y + UP / 2.0f
	}

	private fun findCollision(vararg collisions: Collision): Collision? {
		var bestCollision = null as Collision?
		for (collision in collisions) {
			if (collision.hit) {
				if (bestCollision == null || collision.distance < bestCollision.distance) {
					bestCollision = collision
				}
			}
		}
		return bestCollision
	}

	private fun angleIsFloor(angle: Float): Boolean {
		return angle in 0.0f..floorAngle || angle in PI - floorAngle..PI
			//angle in 0.0f - floorAngle..0.0f || angle in -PI..-PI + floorAngle
	}

	data class Hitbox(val left: Float, val right: Float, val down: Float, val up: Float)

	fun getHitbox(): Hitbox {
		return Hitbox(x - LEFT, x + RIGHT, y + DOWN, y + UP)
	}

	fun update(window: Window, level: Level, delta: Float) {
		/* control */

		var walking = false

		if (window.key(GLFW.GLFW_KEY_A) >= GLFW.GLFW_PRESS) {
			xVel -= acceleration * delta
			walking = true
		}
		if (window.key(GLFW.GLFW_KEY_D) >= GLFW.GLFW_PRESS) {
			xVel += acceleration * delta
			walking = true
		}

		val speed = speed
		if (xVel > speed) xVel = speed
		else if (xVel < -speed) xVel = -speed

		if (!walking) {
			if (xVel > 0) {
				xVel -= acceleration * delta
				if (xVel < 0) xVel = 0.0f
			} else if (xVel < 0) {
				xVel += acceleration * delta
				if (xVel > 0) xVel = 0.0f
			}
		}

		if (onGround && (
			window.key(GLFW.GLFW_KEY_SPACE) == GLFW.GLFW_PRESS || window.key(GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS
		)) {
			yVel = 160.0f//calcJumpForce()
		} else {
			yVel -= gravity * delta
		}

		/* collision */

		val move = CCD.Line(Vector(x, y), Vector(x + xVel * delta, y + yVel * delta))
		val lines = Collision2.collectLines(
			level,
			Collision2.getRange(move.start.x, move.end.x, 0.1f),
			Collision2.getRange(move.start.y, move.end.y, 0.1f),
			0.1f
		)
		val collision = CCD.findCollision(move, lines, 0.1f)

		if (collision == null) {
			x = move.end.x
			y = move.end.y
			onGround = false

		} else {
			x = collision.movePoint.x
			y = collision.movePoint.y

			collision.wallVectors.forEach { wallVector ->
				if (angleIsFloor(wallVector.angle())) {
					val velocityVector = Vector(xVel, 0.0f)
					velocityVector.setProject(wallVector)

					xVel = velocityVector.x
					yVel = velocityVector.y
					onGround = true

				} else {
					val velocityVector = Vector(xVel, yVel)
					velocityVector.setProject(wallVector)

					xVel = velocityVector.x
					yVel = velocityVector.y
				}
			}
		}

		if (y > 96.0f) print("$y,")
	}

	fun checkCollectCoins(coins: List<Coin>): Boolean {
		for (coin in coins) {
			if (!coin.collected) {
				if (coin.distTo(centerX(), centerY()) <= 16.0f) {
					coin.collected = true
					currency += when (coin.tier) {
						CoinLevel.GOLD -> 1
						CoinLevel.HEAVY -> 3
						CoinLevel.DELTA -> 5
						CoinLevel.PASS -> {
							return true
						}
					}
				}
			}
		}
		return false
	}

	fun checkDie(level: Level): Boolean {
		val (left, right, down, up) = getHitbox()

		return arrayOf(
			left to down,
			left to up,
			right to down,
			right to up,
		).any { (x, y) ->
			val subY = Util.mod(y, Level.TILE_SIZE)

			subY < 12.0f && level.access(
				floor(x / Level.TILE_SIZE).toInt(),
				floor(y / Level.TILE_SIZE).toInt()
			) == Level.TILE_LAVA
		}
	}

	val speed: Float
		get() {
			var speed = (tilesPerSecond - SPEED_FLOOR) * Level.TILE_SIZE * (5.0f / 10.0f)
				.pow((currency / 2.0f)) + SPEED_FLOOR * Level.TILE_SIZE
			if (speed < SPEED_FLOOR * Level.TILE_SIZE) speed = SPEED_FLOOR * Level.TILE_SIZE
			return speed
		}

	fun changeSpeed(amount: Int) {
		tilesPerSecond += amount
	}

	fun render(camera: Camera) {
		//
		//Assets.colorShader.enable().setMVP(camera.getMVP(
		//	x - RENDER_WIDTH / 2.0f,
		//	y,
		//	RENDER_WIDTH,
		//	RENDER_HEIGHT
		//)).uniform4f(0, 0.0f, 1.0f, 0.0f, 1.0f);
		//Assets.rect.render();
		Assets.playerTexture.bind()
		Assets.textureShader.enable().setMVP(
			camera.getMVP(
				x - RENDER_WIDTH / 2.0f,
				y,
				RENDER_WIDTH,
				RENDER_HEIGHT
			)
		)
		Assets.rect.render()
	}

	companion object {
		private const val RENDER_WIDTH = 16.0f
		private const val RENDER_HEIGHT = 32.0f
		private const val LEFT = 6.0f
		private const val RIGHT = 6.0f
		private const val DOWN = 0.0f
		private const val UP = 26.0f
		private const val SPEED_FLOOR = 1
	}
}
