package game;

public class Util {
	public static int mod(int a, int b) {
		return ((a % b) + b) % b;
	}

	public static float interp(float low, float high, float along) {
		return (high - low) * along + low;
	}

	public static float invInterp(float low, float high, float value) {
		return (value - low) / (high - low);
	}

	public static int indexOf(int x, int y, int width) {
		return y * width + x;
	}
}
