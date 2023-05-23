package game.object;

import engine.Camera;
import engine.Texture;
import game.Assets;
import game.Util;
import org.joml.Vector2i;

public class Level {
	public static final int TILE_EMPTY = 0;
	public static final int TILE_GROUND = 1;
	public static final int TILE_HELL_GROUND = 2;
	public static final int TILE_LAVA = 3;
	public static final int TILE_SLOPE_RIGHT = 4;
	public static final int TILE_SLOPE_LEFT = 5;

	public static final float TILE_SIZE = 32.0f;

	public boolean hasWall;
	public Theme theme;

	public int width;
	public int height;
	public int spawnX;
	public int spawnY;
	public CoinPlacement[] coinPlacements;
	public int safeX;
	public int[] level;
	public Coin[] coins;
	public Vector2i drugDealerPos;

	public Level(
		int width,
		int height,
		int spawnX,
		int spawnY,
		CoinPlacement[] coinPlacements,
		boolean hasWall,
		int safeX,
		Theme theme,
		int[] level,
		Vector2i drugDealerPos
	) {
		this.width = width;
		this.height = height;
		this.spawnX = spawnX;
		this.spawnY = spawnY;
		this.coinPlacements = coinPlacements;
		this.hasWall = hasWall;
		this.safeX = safeX;
		this.theme = theme;
		this.level = level;
		this.drugDealerPos = drugDealerPos;
	}

	public void reset() {
		coins = new Coin[coinPlacements.length];
		for (var i = 0; i < coinPlacements.length; ++i) {
			var coinPlacement = coinPlacements[i];
			coins[i] = new Coin(coinPlacement.x * TILE_SIZE + TILE_SIZE / 2.0f, coinPlacement.y * TILE_SIZE + TILE_SIZE / 2.0f, coinPlacement.coinLevel);
		}
	}

	public static boolean solid(int tile) {
		switch (tile) {
			case TILE_GROUND, TILE_HELL_GROUND, TILE_SLOPE_LEFT, TILE_SLOPE_RIGHT -> {
				return true;
			}
			default -> {
				return false;
			}
		}
	}

	public int access(int x, int y) {
		if (x < 0) {
			x = 0;
		} else if (x >= width) {
			x = width - 1;
		}

		if (y < 0) {
			y = 0;
		} else if (y >= height) {
			y = height - 1;
		}

		return level[Util.indexOf(x, y, width)];
	}

	public void render(Camera camera) {
		var left = (int)Math.floor(camera.getX() / TILE_SIZE);
		var right = (int)Math.ceil((camera.getX() + camera.getWidth()) / TILE_SIZE);

		var down = (int)Math.floor(camera.getY() / TILE_SIZE);
		var up = (int)Math.ceil((camera.getY() + camera.getHeight()) / TILE_SIZE);

		for (var x = left; x <= right; ++x) {
			for (var y = down; y <= up; ++y) {
				var tile = access(x, y);

				Texture texture = null;

				if (tile == TILE_GROUND) {
					texture = Assets.brickTexture;
				} else if (tile == TILE_HELL_GROUND) {
					texture = Assets.hellGroundTexture;
				} else if (tile == TILE_SLOPE_RIGHT) {
					texture = Assets.slopeRightTexture;
				}else if (tile == TILE_LAVA) {
					texture = Assets.lavaTexture;
				}

				if (texture != null) {
					texture.bind();
					Assets.textureShader.enable().setMVP(camera.getMVP(
						x * TILE_SIZE,
						y * TILE_SIZE,
						TILE_SIZE,
						TILE_SIZE
					));
					Assets.rect.render();
				}
			}
		}

		for (Coin coin : coins) {
			coin.render(camera);
		}
	}
}
