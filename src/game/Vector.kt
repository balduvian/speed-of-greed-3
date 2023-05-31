package game

import org.joml.Math
import kotlin.math.acos
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class Vector(var x: Float, var y: Float) {
	operator fun plus(other: Vector): Vector {
		return Vector(this.x + other.x, this.y + other.y)
	}

	operator fun minus(other: Vector): Vector {
		return Vector(this.x - other.x, this.y - other.y)
	}

	operator fun times(scale: Float): Vector {
		return Vector(this.x * scale, this.y * scale)
	}

	operator fun div(scale: Float): Vector {
		return Vector(this.x / scale, this.y / scale)
	}

	operator fun component1(): Float {
		return x
	}

	operator fun component2(): Float {
		return y
	}

	/* ------------------------------------------- */

	fun add(x: Float, y: Float): Vector {
		return Vector(this.x + x, this.y + y)
	}

	fun setAdd(x: Float, y: Float): Vector {
		this.x += x
		this.y += y
		return this
	}

	fun add(other: Vector): Vector {
		return Vector(this.x + other.x, this.y + other.y)
	}

	fun setAdd(other: Vector): Vector {
		this.x += other.x
		this.y += other.y
		return this
	}

	fun sub(x: Float, y: Float): Vector {
		return Vector(this.x - x, this.y - y)
	}

	fun setSub(x: Float, y: Float): Vector {
		this.x -= x
		this.y -= y
		return this
	}

	fun sub(other: Vector): Vector {
		return Vector(this.x - other.x, this.y - other.y)
	}

	fun setSub(other: Vector): Vector {
		this.x -= other.x
		this.y -= other.y
		return this
	}

	fun rotate(angle: Float): Vector {
		val cos = cos(angle)
		val sin = sin(angle)
		return Vector(this.x * cos - this.y * sin, this.x * sin + this.y * cos)
	}

	fun setRotate(angle: Float): Vector {
		val cos = cos(angle)
		val sin = sin(angle)
		val oldX = this.x

		this.x = oldX * cos - this.y * sin
		this.y = oldX * sin + this.y * cos

		return this
	}

	fun project(onto: Vector): Vector {
		return onto * (this.dot(onto) / onto.lengthSquared())
	}

	fun setProject(onto: Vector): Vector {
		val (x, y) = onto * (this.dot(onto) / onto.lengthSquared())

		this.x = x
		this.y = y

		return this
	}

	fun normalize(): Vector {
		val length = this.length()
		return Vector(x / length, y / length)
	}

	fun setNormalize(): Vector {
		val length = this.length()

		this.x /= length
		this.y /= length

		return this
	}

	fun asLength(length: Float): Vector {
		val oldLength = this.length()
		return Vector(this.x * length / oldLength, this.y * length / oldLength)
	}

	fun setAsLength(length: Float): Vector {
		val oldLength = this.length()

		this.x *= length / oldLength
		this.y *= length / oldLength

		return this
	}

	fun asAngle(angle: Float): Vector {
		val length = this.length()
		return Vector(cos(angle) * length, sin(angle) * length)
	}

	fun setAsAngle(angle: Float): Vector {
		val length = this.length()

		this.x = cos(angle) * length
		this.y = sin(angle) * length

		return this
	}

	fun perpendicularRight(): Vector {
		return Vector(y, -x)
	}

	fun perpendicularLeft(): Vector {
		return Vector(-y, x)
	}

	fun setPerpendicularRight(): Vector {
		val oldX = this.x

		this.x = this.y
		this.y = -oldX

		return this
	}

	fun setPerpendicularLeft(): Vector {
		val oldX = this.x

		this.x = -this.y
		this.y = oldX

		return this
	}

	fun invert(): Vector {
		return Vector(-x, -y)
	}

	fun setInvert(): Vector {
		this.x *= -1
		this.y *= -1

		return this
	}

	/* ------------------------------------------- */

	fun length(): Float {
		return Math.sqrt(x * x + y * y)
	}

	fun lengthSquared(): Float {
		return x * x + y * y
	}

	fun dot(other: Vector): Float {
		return x * other.x + y * other.y
	}

	fun angle(): Float {
		return atan2(y, x)
	}

	fun angleBetween(other: Vector): Float {
		return acos(dot(other) / (length() * other.length()))
	}

	companion object {
		fun atAngle(angle: Float): Vector {
			return Vector(cos(angle), sin(angle))
		}

		fun interp(start: Vector, end: Vector, along: Float): Vector {
			return Vector((end.x - start.x) * along + start.x, (end.y - start.y) * along + start.y)
		}
	}
}