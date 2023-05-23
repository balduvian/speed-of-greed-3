package engine;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
	private Matrix4f projection = new Matrix4f();
	private Matrix4f view = new Matrix4f();
	private Matrix4f viewProjection = new Matrix4f();
	private Matrix4f model = new Matrix4f();
	private Matrix4f mvp = new Matrix4f();

	private float width = 0.0f, height = 0.0f;
	private Vector3f position = new Vector3f();
	private float rotation = 0.0f;
	
	public Camera() {}
	
	public void setDims(float width, float height) {
		this.width = width;
		this.height = height;
		projection.setOrtho(0, width, 0, height, 0, 1);
	}
	
	public void update() {
		view.translation(position.negate(new Vector3f())).rotateZ(-rotation);
		projection.mul(view, viewProjection);
	}

	public boolean fledgeling() {
		return width == 0.0f;
	}

	public float getX() {
		return position.x;
	}
	
	public float getY() {
		return position.y;
	}
	
	public Vector3f getPosition() {
		return new Vector3f(position);
	}
	
	public float getWidth() {
		return width;
	}
	
	public float getHeight() {
		return height;
	}
	
	public void setX(float x) {
		position.x = x;
	}
	
	public void setY(float y) {
		position.y = y;
	}
	
	public void setPosition(float x, float y) {
		position.set(x, y, position.z);
	}
	
	public void setPosition(Vector3f position) {
		this.position.x = position.x;
		this.position.y = position.y;
	}
	
	public void setCenter(float x, float y) {
		position.set(x - width / 2, y - height / 2, position.z);
	}
	
	public void setCenter(Vector3f position) {
		this.position.x = position.x - width / 2;
		this.position.y = position.y - height / 2;
	}
	
	public void translate(float x, float y, float z) {
		position.add(x, y, z);
	}
	
	public void translate(Vector3f transform) {
		position.add(transform);
	}
	
	public void setRotation(float angle) {
		rotation = angle;
	}
	
	public void rotate(float angle) {
		rotation += angle;
	}

	/* */

	private Matrix4f getModel(float x, float y, float width, float height, Matrix4f model) {
		return model.translation(x, y, 0.0f).scale(width, height, 0);
	}

	private Matrix4f getModelCentered(float x, float y, float width, float height, Matrix4f model) {
		return model.translation(x, y, 0.0f).scale(width, height, 0).translate(-0.5f, -0.5f, 0.0f);
	}

	private Matrix4f internalGetMVP(Matrix4f model, Matrix4f viewProjection, Matrix4f mvp) {
		return viewProjection.mul(model, mvp);
	}

	public Matrix4f getMVP(float x, float y, float width, float height) {
		return internalGetMVP(getModel(x, y, width, height, model), viewProjection, mvp);
	}

	public Matrix4f getMP(float x, float y, float width, float height) {
		return internalGetMVP(getModel(x, y, width, height, model), projection, mvp);
	}

	public Matrix4f getMVPCentered(float x, float y, float width, float height) {
		return internalGetMVP(getModelCentered(x, y, width, height, model), viewProjection, mvp);
	}

	public Matrix4f getMPCentered(float x, float y, float width, float height) {
		return internalGetMVP(getModelCentered(x, y, width, height, model), projection, mvp);
	}
}
