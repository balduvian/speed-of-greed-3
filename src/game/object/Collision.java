package game.object;

public class Collision {
	public boolean hit;
	public boolean wall = false;
	public float distance = Float.MAX_VALUE;
	public float x = 0.0f;
	public float y = 0.0f;

	public Collision() {
		this.hit = false;
	}

	public Collision(boolean wall, float distance, float x, float y) {
		this.hit = true;
		this.wall = wall;
		this.distance = distance;
		this.x = x;
		this.y = y;
	}

	private static boolean insideBlockVertical(float x, float y, float worldTileX, float worldTileY) {
		return x > worldTileX && x < worldTileX + Level.TILE_SIZE &&
			y >= worldTileY && y <= worldTileY + Level.TILE_SIZE;
	}

	private static boolean insideBlockHorizontal(float x, float y, float worldTileX, float worldTileY) {
		return x >= worldTileX && x <= worldTileX + Level.TILE_SIZE &&
			y > worldTileY && y < worldTileY + Level.TILE_SIZE;
	}

	public static Collision internalCollide(Level level, float x, float y, float lineOffset, boolean vertical) {
		var groundData = new Collision();

		var baseTileX = (int)Math.floor(x / Level.TILE_SIZE);
		var baseTileY = (int)Math.floor(y / Level.TILE_SIZE);

		for (var j = -1; j <= 1; ++j) {
			for (var i = -1; i <= 1; ++i) {
				var tileX = baseTileX + i;
				var tileY = baseTileY + j;

				var worldTileX = tileX * Level.TILE_SIZE;
				var worldTileY = tileY * Level.TILE_SIZE;

				if (Level.solid(level.access(tileX, tileY))) {
					//if (!Level.solid(level.access(tileX, tileY + 1))) {
						if (vertical ? insideBlockVertical(x, y, worldTileX, worldTileY) : insideBlockHorizontal(x, y, worldTileX, worldTileY)) {
							var distance = vertical ? Math.abs((worldTileY + lineOffset) - y) : Math.abs((worldTileX + lineOffset) - x);

							if (distance < groundData.distance) {
								groundData = new Collision(false, distance, worldTileX + lineOffset, worldTileY + lineOffset);
							}
						}
					//}
				}
			}
		}

		return groundData;
	}

	public static Collision collideDown(Level level, float x, float y) {
		return internalCollide(level, x, y, Level.TILE_SIZE, true);
	}

	public static Collision collideUp(Level level, float x, float y) {
		return internalCollide(level, x, y, 0.0f, true);
	}

	public static Collision collideLeft(Level level, float x, float y) {
		return internalCollide(level, x, y, Level.TILE_SIZE, false);
	}

	public static Collision collideRight(Level level, float x, float y) {
		return internalCollide(level, x, y, 0.0f, false);
	}
}
