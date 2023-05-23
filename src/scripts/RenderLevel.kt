package scripts

import java.awt.Point
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun main() {
	val shapes = arrayOf(
		arrayOf(
			Point(-500, 2500),
			Point(-500, -250),
			Point(750, -250),
			Point(750, 0),
			Point(1000, 0),
			Point(1000, -1000),
			Point(2250, -1000),
			Point(2250, -250),
			Point(2750, -250),//startHold
			//startHold
			Point(2750, 0),
			Point(3000, 0),
			Point(3000, -250),
			Point(3750, -250),
			Point(3750, 0),
			Point(4000, 0),
			Point(4000, -250),//end
			//end
			Point(4750, -250),
			Point(4750, -500),
			Point(5250, -500),
			Point(5250, 0),
			Point(5500, 0),
			Point(5500, -250),

			Point(6250, -250),
			Point(6250, -500),
			Point(6750, -500),
			Point(6750, -250),

			Point(10000, -250),
			Point(10000, 0),
			Point(12000, 0),
			Point(12000, 2500),
		),
		arrayOf(
			Point(1500, -500),
			Point(1750, -500),
			Point(1750, -750),
			Point(1500, -750)
		),
		arrayOf(
			Point(1500, 500),
			Point(1750, 500),
			Point(1750, 250),
			Point(1500, 250),
		),
		arrayOf(
			Point(3250, 1000),
			Point(5250, 1000),
			Point(5250, 750),
			Point(3250, 750),
		),
		arrayOf(
			Point(2500, 750),
			Point(2750, 750),
			Point(2750, 500),
			Point(2500, 500),
		),
		arrayOf(
			Point(6250, 1000),
			Point(6500, 1000),
			Point(6500, 1250),
			Point(6250, 1250),
		),
		arrayOf(
			Point(7000, 1000),
			Point(7250, 1000),
			Point(7250, 1250),
			Point(7000, 1250)
		)
	)

	var left = Int.MAX_VALUE
	var right = Int.MIN_VALUE
	var up = Int.MAX_VALUE
	var down = Int.MIN_VALUE

	for (shape in shapes) {
		for (point in shape) {
			if (point.x < left) left = point.x
			if (point.x > right) right = point.x
			if (point.y < up) up = point.y
			if (point.y > down) down = point.y
		}
	}

	val shapesWidth = right - left
	val shapesHeight = down - up

	val buffer = 10
	val pictureWidth = 1000
	val pictureHeight = ((shapesHeight.toFloat() / shapesWidth.toFloat()) * pictureWidth.toFloat()).toInt()

	val image = BufferedImage(buffer * 2 + pictureWidth, buffer * 2 + pictureHeight, BufferedImage.TYPE_INT_ARGB)
	val graphics = image.createGraphics()

	fun mod(a: Int, b: Int) = ((a % b) + b) % b
	fun interp(left: Float, right: Float, along: Float) = (right - left) * along + left
	fun invInterp(left: Float, right: Float, value: Float) = (value - left) / (right - left)

	for (j in shapes.indices) {
		val shape = shapes[j]
		//graphics.color = Color.getHSBColor(j.toFloat() / shapes.size, 1.0f, 1.0f)

		for (i in shape.indices) {
			val back = shape[mod(i - 1, shape.size)]
			val front = shape[mod(i, shape.size)]

			graphics.drawLine(
				interp(buffer.toFloat(), buffer.toFloat() + pictureWidth, invInterp(left.toFloat(), right.toFloat(), back.x.toFloat())).toInt(),
				interp(buffer.toFloat() + pictureHeight, buffer.toFloat(), invInterp(up.toFloat(), down.toFloat(), back.y.toFloat())).toInt(),

				interp(buffer.toFloat(), buffer.toFloat() + pictureWidth, invInterp(left.toFloat(), right.toFloat(), front.x.toFloat())).toInt(),
				interp(buffer.toFloat() + pictureHeight, buffer.toFloat(), invInterp(up.toFloat(), down.toFloat(), front.y.toFloat())).toInt(),
			)
		}
	}

	graphics.dispose()
	ImageIO.write(image, "PNG", File("level-render.png"))
}