package game.object;

import engine.Camera;
import engine.Window;
import game.Assets;
import org.jetbrains.annotations.Nullable;

import static org.lwjgl.glfw.GLFW.*;

public class Player {
	private static final float RENDER_WIDTH = 16.0f;
	private static final float RENDER_HEIGHT = 32.0f;

	private static final float LEFT = 6.0f;
	private static final float RIGHT = 6.0f;

	private static final float DOWN = 0.0f;
	private static final float UP = 26.0f;

	private static final int SPEED_FLOOR = 1;

	public float x;
	public float y;
	public float yVel = 0;
	public float gravity = 300f;

	public int currency = 0;

	public int tilesPerSecond;
	public int jumpRange;

	public float calcJumpForce() {
		/*
		 * let h be max jump height, g be gravity, x be time
		 * equation for jumping parabola: h - gx^2
		 *
		 * solve for when height = 0, get x = -sqrt(h / g)
		 *
		 * the derivate is -2gx
		 *
		 * plug in -2g(-sqrt(h/g)) to get jump boost at t = 0 to achieve max height given gravity
		 *
		 * = 2g * sqrt(h / g)
		 */
		return 1.34f * gravity * (float)Math.sqrt(((jumpRange + 0.5f) * Level.TILE_SIZE) / gravity);
	}

	public Player(float x, float y, int tilesPerSecond, int jumpRange) {
		this.x = x;
		this.y = y;
		this.tilesPerSecond = tilesPerSecond;
		this.jumpRange = jumpRange;
	}

	public float centerX() {
		return x;
	}

	public float centerY() {
		return y + UP / 2.0f;
	}

	@Nullable
	private Collision findCollision(Collision... collisions) {
		var bestCollision = (Collision)null;

		for (var collision : collisions) {
			if (collision.hit) {
				if (bestCollision == null || collision.distance < bestCollision.distance) {
					bestCollision = collision;
				}
			}
		}

		return bestCollision;
	}

	public void update(Window window, Level level, float delta) { //20, 1620
		var left = x - LEFT;
		var right = x + RIGHT;
		var down = y + DOWN;
		var up = y + UP;

		var onGround = false;

		if (yVel <= 0) {
			var moveY = yVel * delta;

			var downCollision = findCollision(
				Collision.collideDown(level, left, down + moveY),
				Collision.collideDown(level, right, down + moveY)
			);

			if (downCollision != null) {
				onGround = true;
				y = downCollision.y + DOWN;

				if (yVel < 0) {
					yVel = 0;
				}
			}
		} else {
			var moveY = yVel * delta;

			var upCollision = findCollision(
				Collision.collideUp(level, left, up + moveY),
				Collision.collideUp(level, right, up + moveY)
			);

			if (upCollision != null) {
				y = upCollision.y - UP;

				if (yVel > 0) {
					yVel = 0;
				}
			}
		}

		if (onGround) {
			if (window.key(GLFW_KEY_SPACE) == GLFW_PRESS || window.key(GLFW_KEY_W) == GLFW_PRESS) {
				yVel = calcJumpForce();
			}
		} else {
			yVel -= gravity * delta;
		}

		if (window.key(GLFW_KEY_A) >= GLFW_PRESS) {
			var moveX = getSpeed() * delta;

			var leftCollision = findCollision(
				Collision.collideLeft(level, left - moveX, up),
				Collision.collideLeft(level, left - moveX, y),
				Collision.collideLeft(level, left - moveX, down + 1.0f)
			);

			if (leftCollision == null) {
				x -= moveX;
			} else {
				x = leftCollision.x + LEFT;
			}
		}

		if (window.key(GLFW_KEY_D) >= GLFW_PRESS) {
			var moveX = getSpeed() * delta;

			var rightCollision = findCollision(
				Collision.collideRight(level, right + moveX, up),
				Collision.collideRight(level, right + moveX, y),
				Collision.collideRight(level, right + moveX, down + 1.0f)
			);

			if (rightCollision == null) {
				x += moveX;
			} else {
				x = rightCollision.x - RIGHT;
			}
		}

		y += yVel * delta;
	}

	public boolean checkCollectCoins(Coin[] coins) {
		for (Coin coin : coins) {
			if (!coin.collected) {
				if (coin.distTo(centerX(), centerY()) <= 16.0f) {
					coin.collected = true;
					if (coin.tier == CoinLevel.gold) {
						currency += 1;

					} else if (coin.tier == CoinLevel.heavy) {
						currency += 3;

					} else if (coin.tier == CoinLevel.delta) {
						currency += 5;

					} else if (coin.tier == CoinLevel.pass) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public float getSpeed() {
		var speed = (
			((tilesPerSecond - SPEED_FLOOR) * Level.TILE_SIZE) * (float)Math.pow(5.0f / 10.0f, (float)currency / 2.0f)
		) + (SPEED_FLOOR * Level.TILE_SIZE);

		if (speed < SPEED_FLOOR * Level.TILE_SIZE) speed = SPEED_FLOOR * Level.TILE_SIZE;

		return speed;
	}

	public void changeSpeed(int amount) {
		tilesPerSecond += amount;
	}

	public void render(Camera camera) {
		//
		//Assets.colorShader.enable().setMVP(camera.getMVP(
		//	x - RENDER_WIDTH / 2.0f,
		//	y,
		//	RENDER_WIDTH,
		//	RENDER_HEIGHT
		//)).uniform4f(0, 0.0f, 1.0f, 0.0f, 1.0f);
		//Assets.rect.render();

		Assets.playerTexture.bind();
		Assets.textureShader.enable().setMVP(camera.getMVP(
			x - RENDER_WIDTH / 2.0f,
			y,
			RENDER_WIDTH,
			RENDER_HEIGHT
		));
		Assets.rect.render();
	}
}
