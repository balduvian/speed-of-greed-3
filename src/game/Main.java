package game;

import engine.Camera;
import engine.Window;
import game.object.CurrencyViewer;
import game.object.Level;
import game.object.Player;

import java.io.File;
import java.time.Duration;

import static org.lwjgl.glfw.GLFW.*;

public class Main {
	/* finals */

	static final int STAGE_WALL = 0;
	static final int STAGE_SHOP = 1;
	static final float FULL_CAMERA_HEIGHT = 256.0f;

	static final float SHOP_ACTIVE_TIME = 5.0f;

	static final Level[] levels = LevelLoader.loadAll(new File("res/levels"));

	/* variables */

	static Player player;
	static boolean dead = false;

	static int currentStage = 0;
	static float wallTime = 0.0f;
	static float flashTime = 0.0f;
	static float wallX = 0.0f;

	static int levelIndex = 0;
	static Level currentLevel;

	static Player initalPlayer() {
		return new Player(
			0.0f, 0.0f, 5, 3
		);
	}

	static void startLevel(boolean completely, int levelIndex) {
		if (completely) {
			player = initalPlayer();
		}

		dead = false;

		currentStage = STAGE_WALL;
		flashTime = 0.0f;
		wallTime = 0.0f;
		wallX = 0.0f;

		Main.levelIndex = levelIndex;
		currentLevel = levels[levelIndex % levels.length];
		currentLevel.reset();

		player.x = (currentLevel.spawnX + 0.5f) * Level.TILE_SIZE;
		player.y = currentLevel.spawnY * Level.TILE_SIZE;
	}

	public static Window window;

	public static void main(String[] args) {
		window = new Window(1600, 900, "Speed of Greed", true, true, true);

		var camera = new Camera();

		Assets.init();

		var lastTime = System.nanoTime();

		startLevel(true, 0);

		while(!window.shouldClose()) {
			window.update();

			var now = System.nanoTime();
			var delta = (float)((double)(now - lastTime) / (double)Duration.ofSeconds(1).toNanos());
			lastTime = now;

			if (currentLevel.hasWall) {
				if (currentStage == STAGE_WALL) {
					wallTime += delta;
					wallX = wallTime * Level.TILE_SIZE * 0.5f - Level.TILE_SIZE * 2;

					if (player.x < wallX) {
						if (player.currency > 0) {
							if (wallTime % 30 == 0) {
								player.currency -= 1;
							}
						} else {
							dead = true;
						}
					}

					if (player.x > currentLevel.safeX * Level.TILE_SIZE) {
						currentStage = STAGE_SHOP;
					}

				} else if (currentStage == STAGE_SHOP) {
					flashTime += delta;

					if (flashTime >= SHOP_ACTIVE_TIME) {
						if (window.key(GLFW_KEY_S) == GLFW_PRESS && player.currency >= 5) {
							player.tilesPerSecond += 1;
							player.currency -= 5;
						}

						if (window.key(GLFW_KEY_J) == GLFW_PRESS && player.currency >= 5) {
							player.jumpRange += 1;
							player.currency -= 5;
						}
					}
				}
			}

			if (dead) {
				if (window.key(GLFW_KEY_SPACE) == GLFW_PRESS) {
					startLevel(true, 0);
				}
			} else {
				player.update(window, currentLevel, delta);

				if (player.checkCollectCoins(currentLevel.coins)) {
					startLevel(false, levelIndex + 1);
				}

				if (window.key(GLFW_KEY_F5) == GLFW_PRESS) {
					startLevel(true, 0);
				}

				//dead = player.checkDie(currentLevel.dangers);
			}

			if (window.wasResized() || camera.fledgeling()) {
				float windowRatio = (float)window.getWidth() / (float)window.getHeight();
				camera.setDims(windowRatio * FULL_CAMERA_HEIGHT, FULL_CAMERA_HEIGHT);
			}

			camera.setCenter(player.centerX(), player.centerY());
			camera.update();

			/* render */

			window.clear();

			currentLevel.theme.backgroundTexture().bind();
			Assets.tileShader.enable().setMVP(camera.getMP(
				0.0f,
				0.0f,
				camera.getWidth(),
				camera.getHeight()
			)).uniform4f(0, camera.getX() / 100.0f, camera.getY() / 100.0f, 5.0f, 5.0f);
			Assets.rect.render();

			currentLevel.render(camera);

			if (!dead) {
				player.render(camera);
			}

			CurrencyViewer.render(camera, player.currency);

			if (currentLevel.hasWall) {
				if (currentStage == STAGE_WALL) {
					Assets.colorShader.enable().setMVP(camera.getMVP(
						camera.getX(),
						camera.getY(),
						wallX - camera.getX(),
						camera.getHeight()
					)).uniform4f(0, 0.0f, 0.0f, 0.0f, 1.0f);
					Assets.rect.render();

					Assets.darkShader.enable().setMVP(camera.getMVP(
						wallX,
						camera.getY(),
						Level.TILE_SIZE * 2,
						camera.getHeight()
					)).uniform4f(0, 0.0f, 0.0f, 0.0f, 1.0f);
					Assets.rect.render();

				} else if (currentStage == STAGE_SHOP) {
					if (flashTime < SHOP_ACTIVE_TIME) {
						var along = Util.invInterp(0.0f, SHOP_ACTIVE_TIME, flashTime);
						var size = Util.interp(0.0f, camera.getHeight(), along);

						Assets.flashShader.enable().setMVP(camera.getMPCentered(
							camera.getWidth() / 2,
							camera.getHeight() / 2,
							size,
							size
						)).uniform4f(0, 0.0f, 1.0f, 0.25f, 1.0f);
						Assets.rect.render();

					} else {
						//Assets.drugDealerTexture.bind();
						//Assets.textureShader.enable().setMVP(camera.getMVPCentered(
						//	11000.0f,
						//	500.0f,
						//	1000.0f,
						//	1000.0f
						//));
						//Assets.rect.render();

						Assets.shopUITexture.bind();
						Assets.textureShader.enable().setMVP(camera.getMPCentered(
							camera.getWidth() / 2,
							3.0f * camera.getHeight() / 4.0f,
							128.0f,
							64.0f
						));
						Assets.rect.render();
					}
				}
			}

			if (dead) {
				Assets.gameOverTexture.bind();
				Assets.textureShader.enable().setMVP(camera.getMP(0, 0, camera.getWidth(), camera.getHeight()));
				Assets.rect.render();
			}

			window.swap();
		}
	}
}
