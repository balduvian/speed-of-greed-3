package game

object Util {
	fun mod(a: Int, b: Int): Int {
		return (a % b + b) % b
	}

	fun interp(low: Float, high: Float, along: Float): Float {
		return (high - low) * along + low
	}

	fun invInterp(low: Float, high: Float, value: Float): Float {
		return (value - low) / (high - low)
	}

	fun indexOf(x: Int, y: Int, width: Int): Int {
		return y * width + x
	}
}
