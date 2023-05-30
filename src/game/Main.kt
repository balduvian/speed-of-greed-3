package game

import engine.Camera
import engine.Window
import game.`object`.CurrencyViewer
import game.`object`.Level
import game.`object`.Player
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
	var wallTime = 0.0f
	var flashTime = 0.0f
	var wallX = 0.0f
	var levelIndex = 0
	lateinit var currentLevel: Level

	fun initalPlayer(): Player {
		return Player(
			0.0f, 0.0f, 5, 3
		)
	}

	fun startLevel(completely: Boolean, levelIndex: Int) {
		if (completely) {
			player = initalPlayer()
		}
		dead = false
		currentStage = STAGE_WALL
		flashTime = 0.0f
		wallTime = 0.0f
		wallX = 0.0f
		Main.levelIndex = levelIndex
		currentLevel = levels[levelIndex % levels.size]
		currentLevel!!.reset()
		player.x = (currentLevel!!.spawnX + 0.5f) * Level.TILE_SIZE
		player.y = currentLevel!!.spawnY * Level.TILE_SIZE
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
			val delta = ((now - lastTime).toDouble() / Duration.ofSeconds(1).toNanos().toDouble()).toFloat()
			lastTime = now
			if (currentLevel!!.hasWall) {
				if (currentStage == STAGE_WALL) {
					wallTime += delta
					wallX = wallTime * Level.TILE_SIZE * 2.0f - Level.TILE_SIZE * 2
					if (player.x < wallX) {
						if (player.currency > 0) {
							if (wallTime % 30 == 0f) {
								player.currency -= 1
							}
						} else {
							dead = true
						}
					}
					if (player.x > currentLevel!!.safeX * Level.TILE_SIZE) {
						currentStage = STAGE_SHOP
					}
				} else if (currentStage == STAGE_SHOP) {
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
				if (player.checkCollectCoins(currentLevel.coins)) {
					startLevel(false, levelIndex + 1)
				}
				if (window.key(GLFW.GLFW_KEY_F5) == GLFW.GLFW_PRESS) {
					startLevel(true, 0)
				}

				//dead = player.checkDie(currentLevel.dangers);
			}
			if (window.wasResized() || camera.fledgeling()) {
				val windowRatio = window.width.toFloat() / window.height.toFloat()
				camera.setDims(windowRatio * FULL_CAMERA_HEIGHT, FULL_CAMERA_HEIGHT)
			}
			camera.setCenter(player.centerX(), player.centerY())
			camera.update()

			/* render */

			window.clear()
			currentLevel.theme.backgroundTexture().bind()
			Assets.tileShader.enable().setMVP(
				camera.getMP(
					0.0f,
					0.0f,
					camera.width,
					camera.height
				)
			).uniform4f(0, camera.x / 100.0f, camera.y / 100.0f, 5.0f, 5.0f)
			Assets.rect.render()
			currentLevel.render(camera)
			if (!dead) {
				player.render(camera)
			}
			CurrencyViewer.render(camera, player.currency)
			if (currentLevel.hasWall) {
				if (currentStage == STAGE_WALL) {
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
				} else if (currentStage == STAGE_SHOP) {
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
						Assets.shopUITexture.bind()
						Assets.textureShader.enable().setMVP(
							camera.getMPCentered(
								camera.width / 2,
								3.0f * camera.height / 4.0f,
								128.0f,
								64.0f
							)
						)
						Assets.rect.render()
					}
				}
			}
			if (dead) {
				Assets.gameOverTexture.bind()
				Assets.textureShader.enable().setMVP(camera.getMP(0f, 0f, camera.width, camera.height))
				Assets.rect.render()
			}
			window.swap()
		}
	}
}
