package engine

import org.joml.Matrix4f
import org.joml.Vector3f

class Camera {
	private val projection = Matrix4f()
	private val view = Matrix4f()
	private val viewProjection = Matrix4f()
	private val model = Matrix4f()
	private val mvp = Matrix4f()
	var width = 0.0f
		private set
	var height = 0.0f
		private set
	private val position = Vector3f()
	private var rotation = 0.0f
	fun setDims(width: Float, height: Float) {
		this.width = width
		this.height = height
		projection.setOrtho(0f, width, 0f, height, 0f, 1f)
	}

	fun update() {
		view.translation(position.negate(Vector3f())).rotateZ(-rotation)
		projection.mul(view, viewProjection)
	}

	fun fledgeling(): Boolean {
		return width == 0.0f
	}

	var x: Float
		get() = position.x
		set(x) {
			position.x = x
		}
	var y: Float
		get() = position.y
		set(y) {
			position.y = y
		}

	fun getPosition(): Vector3f {
		return Vector3f(position)
	}

	fun setPosition(x: Float, y: Float) {
		position[x, y] = position.z
	}

	fun setPosition(position: Vector3f) {
		this.position.x = position.x
		this.position.y = position.y
	}

	fun setCenter(x: Float, y: Float) {
		position[x - width / 2, y - height / 2] = position.z
	}

	fun setCenter(position: Vector3f) {
		this.position.x = position.x - width / 2
		this.position.y = position.y - height / 2
	}

	fun translate(x: Float, y: Float, z: Float) {
		position.add(x, y, z)
	}

	fun translate(transform: Vector3f?) {
		position.add(transform)
	}

	fun setRotation(angle: Float) {
		rotation = angle
	}

	fun rotate(angle: Float) {
		rotation += angle
	}

	/* */
	private fun getModel(x: Float, y: Float, width: Float, height: Float, model: Matrix4f): Matrix4f {
		return model.translation(x, y, 0.0f).scale(width, height, 0f)
	}

	private fun getModelCentered(x: Float, y: Float, width: Float, height: Float, model: Matrix4f): Matrix4f {
		return model.translation(x, y, 0.0f).scale(width, height, 0f).translate(-0.5f, -0.5f, 0.0f)
	}

	private fun internalGetMVP(model: Matrix4f, viewProjection: Matrix4f, mvp: Matrix4f): Matrix4f {
		return viewProjection.mul(model, mvp)
	}

	fun getMVP(x: Float, y: Float, width: Float, height: Float): Matrix4f {
		return internalGetMVP(getModel(x, y, width, height, model), viewProjection, mvp)
	}

	fun getMP(x: Float, y: Float, width: Float, height: Float): Matrix4f {
		return internalGetMVP(getModel(x, y, width, height, model), projection, mvp)
	}

	fun getMVPCentered(x: Float, y: Float, width: Float, height: Float): Matrix4f {
		return internalGetMVP(getModelCentered(x, y, width, height, model), viewProjection, mvp)
	}

	fun getMPCentered(x: Float, y: Float, width: Float, height: Float): Matrix4f {
		return internalGetMVP(getModelCentered(x, y, width, height, model), projection, mvp)
	}
}
