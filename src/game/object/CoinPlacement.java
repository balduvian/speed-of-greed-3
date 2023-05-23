package game.object;

public class CoinPlacement {
	public int x;
	public int y;
	public CoinLevel coinLevel;

	public CoinPlacement(int x, int y, CoinLevel coinLevel) {
		this.x = x;
		this.y = y;
		this.coinLevel = coinLevel;
	}
}
