package game

import engine.Shader
import engine.Texture
import engine.Vao

object Assets {
	lateinit var rect: Vao

	lateinit var textureShader: Shader
	lateinit var darkShader: Shader
	lateinit var tileShader: Shader
	lateinit var flashShader: Shader
	lateinit var colorShader: Shader
	lateinit var playerTexture: Texture

	lateinit var numberTextures: Array<Texture>

	lateinit var coinTexture: Texture

	lateinit var blueCoinTexture: Texture

	lateinit var superCoinTexture: Texture

	lateinit var keyCoinTexture: Texture

	lateinit var brickTexture: Texture
	lateinit var panelTexture: Texture

	lateinit var hellGroundTexture: Texture
	lateinit var hellBackgroundTexture: Texture

	lateinit var lavaTexture: Texture
	lateinit var drugDealerTexture: Texture
	lateinit var shopUITexture: Texture
	lateinit var gameOverTexture: Texture

	lateinit var slopeRightTexture: Texture

	fun init() {
		rect = Vao(
			floatArrayOf(
				0f, 0f, 0f,
				1f, 0f, 0f,
				1f, 1f, 0f,
				0f, 1f, 0f
			), intArrayOf(
				0, 1, 3,
				1, 2, 3
			)
		).addAttrib(
			floatArrayOf(
				0f, 1f,
				1f, 1f,
				1f, 0f,
				0f, 0f
			), 2
		)
		textureShader = Shader(
			"res/shaders/texture/vert.glsl",
			"res/shaders/texture/frag.glsl", arrayOf()
		)
		darkShader = Shader(
			"res/shaders/dark/vert.glsl",
			"res/shaders/dark/frag.glsl", arrayOf(
				"color"
			)
		)
		tileShader = Shader(
			"res/shaders/tile/vert.glsl",
			"res/shaders/tile/frag.glsl", arrayOf(
				"tile"
			)
		)
		flashShader = Shader(
			"res/shaders/flash/vert.glsl",
			"res/shaders/flash/frag.glsl", arrayOf(
				"color"
			)
		)
		colorShader = Shader(
			"res/shaders/color/vert.glsl",
			"res/shaders/color/frag.glsl", arrayOf(
				"color"
			)
		)
		numberTextures = Array(10) {
			Texture("res/$it.png")
		}
		playerTexture = Texture("res/charShi.png")
		coinTexture = Texture("res/coinTextureOne.png")
		blueCoinTexture = Texture("res/coinLvlTwo.png")
		superCoinTexture = Texture("res/coinGud.png")
		keyCoinTexture = Texture("res/keyKoin.png")
		brickTexture = Texture("res/lvlOneBrick.png")
		panelTexture = Texture("res/backgroundText.png").repeat()
		hellGroundTexture = Texture("res/rockBlock.png")
		hellBackgroundTexture = Texture("res/lavaBackground.png").repeat()
		lavaTexture = Texture("res/lavaTile.png")
		drugDealerTexture = Texture("res/shopDrugkeeperDealer.png")
		shopUITexture = Texture("res/shopUI.png")
		gameOverTexture = Texture("res/gameOverBackground.png")
		slopeRightTexture = Texture("res/slopeRight.png")
	}
}
