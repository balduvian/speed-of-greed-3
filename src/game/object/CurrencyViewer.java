package game.object;

import engine.Camera;
import game.Assets;

public class CurrencyViewer {
	private static final float NUMBER_SIZE = 24.0f;

	public static void render(Camera camera, int num) {
		String str = Integer.toString(num);
		int firstDigit;
		int secondDigit;

		if (str.length() != 2) {
			firstDigit = 0;
			secondDigit = str.charAt(0) - '0';
		} else {
			firstDigit = str.charAt(0) - '0';
			secondDigit = str.charAt(1) - '0';
		}

		Assets.numberTextures[firstDigit].bind();
		Assets.textureShader.enable().setMVP(camera.getMP(
			camera.getWidth() - NUMBER_SIZE * 2,
			camera.getHeight() - NUMBER_SIZE,
			NUMBER_SIZE,
			NUMBER_SIZE
		));
		Assets.rect.render();

		Assets.numberTextures[secondDigit].bind();
		Assets.textureShader.enable().setMVP(camera.getMP(
			camera.getWidth() - NUMBER_SIZE,
			camera.getHeight() - NUMBER_SIZE,
			NUMBER_SIZE,
			NUMBER_SIZE
		));
		Assets.rect.render();
	}
}
