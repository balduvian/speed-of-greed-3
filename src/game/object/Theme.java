package game.object;

import engine.Texture;
import game.Assets;

abstract public class Theme {
	abstract public Texture tileTexture();
	abstract public Texture backgroundTexture();
	abstract public Texture dangerTexture();

	public Theme() {}

	public static Theme PORTAL_THEME = new Theme() {
		@Override
		public Texture tileTexture() {
			return Assets.brickTexture;
		}

		@Override
		public Texture backgroundTexture() {
			return Assets.panelTexture;
		}

		@Override
		public Texture dangerTexture() {
			return Assets.lavaTexture;
		}
	};

	public static Theme HELL_THEME = new Theme() {
		@Override
		public Texture tileTexture() {
			return Assets.hellGroundTexture;
		}

		@Override
		public Texture backgroundTexture() {
			return Assets.hellBackgroundTexture;
		}

		@Override
		public Texture dangerTexture() {
			return Assets.lavaTexture;
		}
	};
}
