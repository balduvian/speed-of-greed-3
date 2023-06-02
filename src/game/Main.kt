package game

import engine.Camera
import engine.Texture
import engine.Window
import game.`object`.CurrencyViewer
import game.`object`.Level
import game.`object`.Player
import game.`object`.Theme
import org.lwjgl.glfw.GLFW
import java.io.File
import java.time.Duration

object Main {
	/* finals */
	const val STAGE_WALL = 0
	const val STAGE_SHOP = 1
	const val FULL_CAMERA_HEIGHT = 256.0f
	const val SHOP_ACTIVE_TIME = 1.0f
	val levels = LevelLoader.loadAll(File("res/levels"))

	/* variables */
	lateinit var player: Player
	var dead = false
	var currentStage = 0
	var flashTime = 0.0f
	var wallX = 0.0f
	var levelIndex = 0
	lateinit var currentLevel: Level

	var backgroundMover = 0.0f

	var minFollowDistance = Level.TILE_SIZE * 45.0f

	fun initalPlayer(): Player {
		return Player(
			0.0f, 0.0f, 6, 3
		)
	}

	fun startLevel(completely: Boolean, levelIndex: Int) {
		if (completely) {
			player = initalPlayer()
		}
		dead = false
		currentStage = STAGE_WALL
		flashTime = 0.0f
		wallX = -2.0f * Level.TILE_SIZE
		Main.levelIndex = levelIndex
		currentLevel = levels[levelIndex % levels.size]
		currentLevel.reset()
		player.x = (currentLevel.spawnX + 0.5f) * Level.TILE_SIZE
		player.y = currentLevel.spawnY * Level.TILE_SIZE
		backgroundMover = 0.0f
	}

	lateinit var window: Window

	@JvmStatic
	fun main(args: Array<String>) {
		window = Window(1600, 900, "Speed of Greed", true, true, true, false)
		val camera = Camera()
		Assets.init()

		startLevel(true, 0)

		var lastTime = System.nanoTime()
		while (!window.shouldClose()) {
			window.update()

			val now = System.nanoTime()
			val delta = ((now - lastTime).toFloat() / Duration.ofSeconds(1).toNanos().toFloat()).coerceAtMost(0.25f)
			lastTime = now

			if (currentLevel.hasWall) {
				wallX += delta * Level.TILE_SIZE * 3.0f

				if (wallX > currentLevel.safeX * Level.TILE_SIZE) {
					wallX = currentLevel.safeX * Level.TILE_SIZE
				} else if (player.x - wallX > minFollowDistance) {
					wallX = player.x - minFollowDistance
				}
				if (player.x < wallX - Level.TILE_SIZE * 2.0f) {
					dead = true
				}
				if (player.x > currentLevel.safeX * Level.TILE_SIZE) {
					currentStage = STAGE_SHOP
				}

				if (currentStage == STAGE_SHOP) {
					flashTime += delta
					if (flashTime >= SHOP_ACTIVE_TIME) {
						if (window.key(GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS && player.currency >= 5) {
							player.tilesPerSecond += 1
							player.currency -= 5
						}
						if (window.key(GLFW.GLFW_KEY_J) == GLFW.GLFW_PRESS && player.currency >= 5) {
							player.jumpRange += 1
							player.currency -= 5
						}
					}
				}
			}
			if (dead) {
				if (window.key(GLFW.GLFW_KEY_SPACE) == GLFW.GLFW_PRESS) {
					startLevel(true, 0)
				}
			} else {
				player.update(window, currentLevel, delta)
				if (player.checkCollectCoins(currentLevel.coins) || window.key(GLFW.GLFW_KEY_F3) == GLFW.GLFW_PRESS) {
					startLevel(false, levelIndex + 1)
				}
				if (window.key(GLFW.GLFW_KEY_F5) == GLFW.GLFW_PRESS) {
					startLevel(true, 0)
				}

				dead = player.checkDie(currentLevel)
			}
			if (window.wasResized() || camera.fledgeling()) {
				val windowRatio = window.width.toFloat() / window.height.toFloat()
				camera.setDims(windowRatio * FULL_CAMERA_HEIGHT, FULL_CAMERA_HEIGHT)
			}
			camera.setCenter(player.centerX(), player.centerY())
			camera.update()

			backgroundMover = (backgroundMover + delta * 32.0f) % (64.0f * 128.0f)

			/* render */

			window.clear()

			fun renderBackgroundLayer(texture: Texture, scale: Float, parallax: Float, offset: Float) {
				texture.bind()
				Assets.tileShader.enable().setMVP(
					camera.getMP(
						0.0f,
						0.0f,
						camera.width,
						camera.height
					)
				).uniform4f(0,
					camera.x / parallax,
					-(camera.y + offset) / parallax * (camera.height / camera.width),
					camera.width / scale,
					camera.height / scale
				)
				Assets.rect.render()
			}

			if (currentLevel.theme == Theme.PORTAL_THEME) {
				renderBackgroundLayer(Assets.panelTexture, 48.0f, 128.0f, 0.0f)
			} else {
				renderBackgroundLayer(Assets.hellBackgroundTexture1, 64.0f, 256.0f, backgroundMover)
				renderBackgroundLayer(Assets.hellBackgroundTexture0, 48.0f, 128.0f, 0.0f)
			}

			currentLevel.render(camera)

			if (currentLevel.hasWall && currentStage == STAGE_SHOP && flashTime >= SHOP_ACTIVE_TIME) {
				Assets.drugDealerTexture.bind()
				Assets.textureShader.enable().setMVP(
					camera.getMVPCentered(
						(currentLevel.drugDealerPos.x + 0.5f) * Level.TILE_SIZE,
						currentLevel.drugDealerPos.y * Level.TILE_SIZE + 32.0f,
						64.0f,
						64.0f
					)
				)
				Assets.rect.render()
			}

			if (!dead) {
				player.render(camera)
			}

			if (currentLevel.hasWall) {
				Assets.colorShader.enable().setMVP(
					camera.getMVP(
						camera.x,
						camera.y,
						wallX - camera.x,
						camera.height
					)
				).uniform4f(0, 0.0f, 0.0f, 0.0f, 1.0f)
				Assets.rect.render()
				Assets.darkShader.enable().setMVP(
					camera.getMVP(
						wallX,
						camera.y,
						Level.TILE_SIZE * 2,
						camera.height
					)
				).uniform4f(0, 0.0f, 0.0f, 0.0f, 1.0f)
				Assets.rect.render()

				if (currentStage == STAGE_SHOP) {
					if (flashTime < SHOP_ACTIVE_TIME) {
						val along = Util.invInterp(0.0f, SHOP_ACTIVE_TIME, flashTime)
						val size = Util.interp(0.0f, camera.height, along)
						Assets.flashShader.enable().setMVP(
							camera.getMPCentered(
								camera.width / 2.0f,
								camera.height / 2.0f,
								size,
								size
							)
						).uniform4f(0, 0.0f, 1.0f, 0.25f, 1.0f)
						Assets.rect.render()
					} else {
						Assets.shopUITexture.bind()
						Assets.textureShader.enable().setMVP(
							camera.getMPCentered(
								camera.width / 2,
								1.0f * camera.height / 4.0f,
								128.0f,
								64.0f
							)
						)
						Assets.rect.render()
					}
				}
			}

			/* UI render */

			CurrencyViewer.render(camera, player.currency)

			if (dead) {
				Assets.gameOverTexture.bind()
				Assets.textureShader.enable().setMVP(camera.getMP(0f, 0f, camera.width, camera.height))
				Assets.rect.render()
			}

			window.swap()
		}
	}
}
