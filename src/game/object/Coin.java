package game.object;

import engine.Camera;
import game.Assets;

public class Coin {
	private static final float SIZE = 32.0f;

	float x;
	float y;

	public CoinLevel tier;
	public boolean collected = false;

	public Coin(float gx, float gy, CoinLevel type) {
		this.x = gx;
		this.y = gy;
		this.tier = type;
	}

	public double distTo(double px, double py) {
		return Math.sqrt(Math.pow(px - x, 2.0) + Math.pow(py - y, 2.0));
	}

	public void render(Camera camera) {
		if (collected) return;

		var texture = tier == CoinLevel.gold ? Assets.coinTexture
			: tier == CoinLevel.heavy ? Assets.blueCoinTexture
			: tier == CoinLevel.delta ? Assets.superCoinTexture
			: Assets.keyCoinTexture;

		texture.bind();
		Assets.textureShader.enable().setMVP(camera.getMVPCentered(
			x,
			y,
			SIZE,
			SIZE
		));
		Assets.rect.render();
	}
}
