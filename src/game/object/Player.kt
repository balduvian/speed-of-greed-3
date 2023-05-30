package game.`object`

import engine.Camera
import engine.Window
import game.Assets
import org.lwjgl.glfw.GLFW
import kotlin.math.sqrt

class Player(var x: Float, var y: Float, var tilesPerSecond: Int, var jumpRange: Int) {
	var yVel = 0f
	var gravity = 300f
	var currency = 0
	var onGround = false

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
		return 1.34f * gravity * sqrt(((jumpRange + 0.5f) * Level.TILE_SIZE / gravity).toDouble()).toFloat()
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

	fun update(window: Window, level: Level, delta: Float) { //20, 1620
		val left = x - LEFT
		val right = x + RIGHT
		val down = y + DOWN
		val up = y + UP

		var velX = 0.0f

		/* control */

		if (window.key(GLFW.GLFW_KEY_A) >= GLFW.GLFW_PRESS) {
			velX -= speed * delta
		}
		if (window.key(GLFW.GLFW_KEY_D) >= GLFW.GLFW_PRESS) {
			velX += speed * delta
		}

		if (onGround) {
			if (window.key(GLFW.GLFW_KEY_SPACE) == GLFW.GLFW_PRESS || window.key(GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS) {
				yVel = calcJumpForce()
			}
		}

		/* collision */

		if (velX < 0.0f) {
			val leftCollision = findCollision(
				Collision.collideLeft(level, left + velX, up),
				Collision.collideLeft(level, left + velX, y),
				Collision.collideLeft(level, left + velX, down + 1.0f)
			)
			if (leftCollision == null) {
				x += velX
			} else {
				x = leftCollision.value + LEFT
			}
		} else if (velX > 0.0f) {
			val rightCollision = findCollision(
				Collision.collideRight(level, right + velX, up),
				Collision.collideRight(level, right + velX, y),
				Collision.collideRight(level, right + velX, down + 1.0f)
			)
			if (rightCollision == null) {
				x += velX
			} else {
				x = rightCollision.value - RIGHT
			}
		}

		yVel -= gravity * delta

		if (yVel < 0) {
			val moveY = yVel * delta
			val downCollision = findCollision(
				Collision.collideDown(level, left, down + moveY),
				Collision.collideDown(level, right, down + moveY),
				Collision.collideDown(level, left, down + 1.0f + moveY),
				Collision.collideDown(level, right, down + 1.0f + moveY)
			)
			if (downCollision == null) {
				onGround = false
			} else {
				onGround = true
				y = downCollision.value + DOWN
				if (yVel < 0) {
					yVel = 0f
				}
			}
		} else if (yVel > 0) {
			onGround = false
			val moveY = yVel * delta
			val upCollision = findCollision(
				Collision.collideUp(level, left, up + moveY),
				Collision.collideUp(level, right, up + moveY)
			)
			if (upCollision != null) {
				y = upCollision.value - UP
				if (yVel > 0) {
					yVel = 0f
				}
			}
		}

		y += yVel * delta
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

	val speed: Float
		get() {
			var speed = (tilesPerSecond - SPEED_FLOOR) * Level.TILE_SIZE * Math.pow(
				(5.0f / 10.0f).toDouble(),
				(currency.toFloat() / 2.0f).toDouble()
			).toFloat() + SPEED_FLOOR * Level.TILE_SIZE
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
