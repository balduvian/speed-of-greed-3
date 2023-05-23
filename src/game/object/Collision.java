package game.object;

import game.Util;
import org.joml.Vector2i;

import static game.object.Level.*;

public class Collision {
	public boolean hit;
	public float distance = Float.MIN_VALUE;
	public float value = 0.0f;

	public Collision() {
		this.hit = false;
	}

	public Collision(float distance, float value) {
		this.hit = true;
		this.distance = distance;
		this.value = value;
	}

	interface LineFunc {
		float lineFunc(float x, float y, float worldTileX, float worldTileY);
	}

	/* line funcs */

	public static float leftFlat(float x, float y, float worldTileX, float worldTileY) {
		return worldTileX;
	}

	public static float downFlat(float x, float y, float worldTileX, float worldTileY) {
		return worldTileY;
	}

	public static float rightFlat(float x, float y, float worldTileX, float worldTileY) {
		return worldTileX + TILE_SIZE;
	}

	public static float upFlat(float x, float y, float worldTileX, float worldTileY) {
		return worldTileY + TILE_SIZE;
	}

	public static float topSlopRight(float x, float y, float worldTileX, float worldTileY) {
		return Util.interp(worldTileY, worldTileY + TILE_SIZE, Util.invInterp(worldTileX, worldTileX + TILE_SIZE, x));
	}

	/* block model */

	static class BlockModel {
		LineFunc[] lineFuncs;
		boolean[] hasFace;
		public BlockModel(LineFunc[] lineFuncs, boolean[] hasFace) {
			this.lineFuncs = lineFuncs;
			this.hasFace = hasFace;
		}
	}

	private static final BlockModel fullBlockModel = new BlockModel(new LineFunc[] {
		Collision::leftFlat,
		Collision::downFlat,
		Collision::rightFlat,
		Collision::upFlat,
	}, new boolean[] {
		true,
		true,
		true,
		true,
	});

	private static final BlockModel slopeRightBlockModel = new BlockModel(new LineFunc[] {
		Collision::leftFlat,
		Collision::downFlat,
		Collision::rightFlat,
		Collision::topSlopRight,
	}, new boolean[] {
		false,
		true,
		true,
		true,
	});

	/* */

	private static boolean intnernalInsideBlock( BlockModel blockModel, float x, float y, float worldTileX, float worldTileY, boolean fullX, boolean fullY) {
		var left = blockModel.lineFuncs[DIRECTION_RIGHT].lineFunc(x, y, worldTileX, worldTileY);
		var right = blockModel.lineFuncs[DIRECTION_LEFT].lineFunc(x, y, worldTileX, worldTileY);
		var down = blockModel.lineFuncs[DIRECTION_UP].lineFunc(x, y, worldTileX, worldTileY);
		var up = blockModel.lineFuncs[DIRECTION_DOWN].lineFunc(x, y, worldTileX, worldTileY);

		var horizontalPart = fullX ? x >= left && x <= right : x > left && x < right;
		var verticalPart = fullY ? y >= down && y <= up : y > down && y < up;

		return horizontalPart && verticalPart;
	}

	public static boolean insideBlock(BlockModel blockModel, float x, float y, float worldTileX, float worldTileY, int direction) {
		if (!blockModel.hasFace[direction]) return false;

		var vertical = isVertical(direction);

		return intnernalInsideBlock(blockModel, x, y, worldTileX, worldTileY, !vertical || blockModel == slopeRightBlockModel, vertical);
	}

	public static BlockModel blockModel(int tile) {
		switch (tile) {
			case TILE_GROUND, TILE_HELL_GROUND -> {
				return fullBlockModel;
			}
			case TILE_SLOPE_RIGHT -> {
				return slopeRightBlockModel;
			}
			default -> {
				return null;
			}
		}
	}

	private static final int DIRECTION_RIGHT = 0;
	private static final int DIRECTION_UP = 1;
	private static final int DIRECTION_LEFT = 2;
	private static final int DIRECTION_DOWN = 3;

	public static Vector2i[] directionOffsets = new Vector2i[] {
		new Vector2i(1, 0),
		new Vector2i(0, 1),
		new Vector2i(-1, 0),
		new Vector2i(0, -1),
	};

	public static int[] oppositeDirection = new int[] {
		DIRECTION_LEFT,
		DIRECTION_DOWN,
		DIRECTION_RIGHT,
		DIRECTION_UP,
	};

	private static boolean isVertical(int direction) {
		return direction == DIRECTION_UP || direction == DIRECTION_DOWN;
	}

	public static Collision internalCollide(Level level, float x, float y, int direction) {
		var groundData = new Collision();

		var baseTileX = (int)Math.floor(x / Level.TILE_SIZE);
		var baseTileY = (int)Math.floor(y / Level.TILE_SIZE);

		for (var j = -1; j <= 1; ++j) {
			for (var i = -1; i <= 1; ++i) {
				var tileX = baseTileX + i;
				var tileY = baseTileY + j;

				var worldTileX = tileX * Level.TILE_SIZE;
				var worldTileY = tileY * Level.TILE_SIZE;

				var tile = level.access(tileX, tileY);
				var blockModel = blockModel(tile);

				if (blockModel != null) {
					var availableDirection = directionOffsets[oppositeDirection[direction]];
					if (blockModel(level.access(tileX + availableDirection.x, tileY + availableDirection.y)) == null) {
						if (insideBlock(blockModel, x, y, worldTileX, worldTileY, direction)) {
							var vertical = isVertical(direction);

							var distance =  Math.abs(blockModel.lineFuncs[direction].lineFunc(x, y, worldTileX, worldTileY) - (vertical ? y : x));

							if (distance > groundData.distance) {
								groundData = new Collision(distance, blockModel.lineFuncs[direction].lineFunc(x, y, worldTileX, worldTileY));
							}
						}
					}
				}
			}
		}

		return groundData;
	}

	public static Collision collideDown(Level level, float x, float y) {
		return internalCollide(level, x, y, DIRECTION_DOWN);
	}

	public static Collision collideUp(Level level, float x, float y) {
		return internalCollide(level, x, y, DIRECTION_UP);
	}

	public static Collision collideLeft(Level level, float x, float y) {
		return internalCollide(level, x, y, DIRECTION_LEFT);
	}

	public static Collision collideRight(Level level, float x, float y) {
		return internalCollide(level, x, y, DIRECTION_RIGHT);
	}
}
