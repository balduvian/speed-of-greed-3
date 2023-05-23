package game;

import game.object.CoinLevel;
import game.object.CoinPlacement;
import game.object.Level;
import game.object.Theme;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class LevelLoader {
	public static Level[] loadAll(File folder) {
		var files = folder.listFiles();
		assert files != null;

		return Arrays.stream(files).sorted(Comparator.comparing(file0 -> file0.toPath().getFileName().toString())).map(file -> {
			try {
				return load(loadFile(file));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}).toArray(Level[]::new);
	}

	public static String[] loadFile(File file) throws FileNotFoundException {
		var reader = new BufferedReader(new FileReader(file));
		return reader.lines().toArray(String[]::new);
	}

	public  static Level load(String[] lines) throws Exception {
		if (lines.length < 2) throw new Exception("map file too short");

		var flags = Arrays.stream(lines[0].split(";")).map(String::toLowerCase).toList();

		var width = lines[1].length();
		var height = lines.length - 1;
		var safeX = 0;
		var level = new int[width * height];
		var coins = new ArrayList<CoinPlacement>();
		var spawnX = -1;
		var spawnY = -1;

		for (var j = 0; j < height; ++j) {
			var line = lines[height - j];
			if (line.length() != width) throw new Exception("inconsitent map width on line " + (j + 2));

			for (var i = 0; i < width; ++i) {
				var current = line.charAt(i);

				switch (current) {
					case '#' -> level[Util.indexOf(i, j, width)] = Level.TILE_GROUND;
					case '@' -> level[Util.indexOf(i, j, width)] = Level.TILE_HELL_GROUND;
					case '*' -> level[Util.indexOf(i, j, width)] = Level.TILE_LAVA;
					case '|' -> safeX = i;
					case 'p' -> {
						spawnX = i;
						spawnY = j;
					}
					case 'o' -> coins.add(new CoinPlacement(i, j, CoinLevel.gold));
					case 'b' -> coins.add(new CoinPlacement(i, j, CoinLevel.heavy));
					case 'd' -> coins.add(new CoinPlacement(i, j, CoinLevel.delta));
					case 'k' -> coins.add(new CoinPlacement(i, j, CoinLevel.pass));
				}
			}
		}

		if (spawnX == -1) {
			throw new Exception("no player spawn found (p)");
		}
		if (coins.stream().noneMatch(coin -> coin.coinLevel == CoinLevel.pass)) {
			throw new Exception("no exit coin (k)");
		}

		return new Level(
			width,
			height,
			spawnX,
			spawnY,
			coins.toArray(new CoinPlacement[] {}),
			flags.contains("wall"),
			safeX,
			flags.contains("portal") ? Theme.PORTAL_THEME : Theme.HELL_THEME,
			level
		);
	}
}
