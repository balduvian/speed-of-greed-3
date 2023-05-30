package game.`object`

import engine.Texture
import game.Assets

abstract class Theme {
	abstract fun tileTexture(): Texture
	abstract fun backgroundTexture(): Texture
	abstract fun dangerTexture(): Texture

	companion object {
		@JvmField
		var PORTAL_THEME: Theme = object : Theme() {
			override fun tileTexture(): Texture {
				return Assets.brickTexture
			}

			override fun backgroundTexture(): Texture {
				return Assets.panelTexture
			}

			override fun dangerTexture(): Texture {
				return Assets.lavaTexture
			}
		}
		@JvmField
		var HELL_THEME: Theme = object : Theme() {
			override fun tileTexture(): Texture {
				return Assets.hellGroundTexture
			}

			override fun backgroundTexture(): Texture {
				return Assets.hellBackgroundTexture
			}

			override fun dangerTexture(): Texture {
				return Assets.lavaTexture
			}
		}
	}
}
