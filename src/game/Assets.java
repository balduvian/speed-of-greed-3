package game;

import engine.Texture;
import engine.Vao;
import engine.Shader;

public class Assets {
	public static Vao rect;

	public static Shader textureShader;
	public static Shader darkShader;
	public static Shader tileShader;
	public static Shader flashShader;
	public static Shader colorShader;

	public static Texture playerTexture;
	public static Texture[] numberTextures;

	public static Texture coinTexture;
	public static Texture blueCoinTexture;
	public static Texture superCoinTexture;
	public static Texture keyCoinTexture;

	public static Texture brickTexture;
	public static Texture panelTexture;

	public static Texture hellGroundTexture;
	public static Texture hellBackgroundTexture;
	public static Texture lavaTexture;

	public static Texture drugDealerTexture;
	public static Texture shopUITexture;

	public static Texture gameOverTexture;
	public static Texture slopeRightTexture;

	public static void init () {
		rect = new Vao(new float[] {
			0, 0, 0,
			1, 0, 0,
			1, 1, 0,
			0, 1, 0
		}, new int[] {
			0, 1, 3,
			1, 2, 3
		}).addAttrib(new float[] {
			0, 1,
			1, 1,
			1, 0,
			0, 0,
		}, 2);

		textureShader = new Shader(
			"res/shaders/texture/vert.glsl",
			"res/shaders/texture/frag.glsl",
			new String[] {}
		);
		darkShader = new Shader(
			"res/shaders/dark/vert.glsl",
			"res/shaders/dark/frag.glsl",
			new String[] {
				"color"
			}
		);
		tileShader = new Shader(
			"res/shaders/tile/vert.glsl",
			"res/shaders/tile/frag.glsl",
			new String[] {
				"tile"
			}
		);
		flashShader = new Shader(
			"res/shaders/flash/vert.glsl",
			"res/shaders/flash/frag.glsl",
			new String[] {
				"color"
			}
		);
		colorShader = new Shader(
			"res/shaders/color/vert.glsl",
			"res/shaders/color/frag.glsl",
			new String[] {
				"color"
			}
		);

		playerTexture = new Texture("res/charShi.png");

		numberTextures = new Texture[10];
		for (var i = 0; i < 10; ++i) {
			numberTextures[i] = new Texture("res/" + i + ".png");
		}

		coinTexture = new Texture("res/coinTextureOne.png");
		blueCoinTexture = new Texture("res/coinLvlTwo.png");
		superCoinTexture = new Texture("res/coinGud.png");
		keyCoinTexture = new Texture("res/keyKoin.png");

		brickTexture = new Texture("res/lvlOneBrick.png");
		panelTexture = new Texture("res/backgroundText.png");
		panelTexture.repeat();

		hellGroundTexture = new Texture("res/rockBlock.png");
		hellBackgroundTexture = new Texture("res/lavaBackground.png");
		lavaTexture = new Texture("res/lavaTile.png");

		drugDealerTexture = new Texture("res/shopDrugkeeperDealer.png");
		shopUITexture = new Texture("res/shopUI.png");

		gameOverTexture = new Texture("res/gameOverBackground.png");
		slopeRightTexture = new Texture("res/slopeRight.png");
	}
}
