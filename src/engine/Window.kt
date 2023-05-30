package engine

import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import java.util.*

class Window(
	initialWidth: Int,
	initialHeight: Int,
	title: String,
	vSync: Boolean,
	resizable: Boolean,
	decorated: Boolean,
	maximized: Boolean
) {
	private var window: Long = 0
	var width = 0
		private set
	var height = 0
		private set
	private var resized = false
	private var fullScreen = false
	private val mouseButtons = IntArray(GLFW.GLFW_MOUSE_BUTTON_LAST + 1)
	private val keys = IntArray(GLFW.GLFW_KEY_LAST + 1)

	init {
		GLFW.glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err))
		Arrays.fill(mouseButtons, 0)
		if (!GLFW.glfwInit()) {
			System.err.println("GLFW failed to initialize!")
			System.exit(-1)
		}
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3)
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3)
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE)
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE)
		GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 8)
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, if (resizable) GLFW.GLFW_TRUE else GLFW.GLFW_FALSE)
		GLFW.glfwWindowHint(GLFW.GLFW_DECORATED, if (decorated) GLFW.GLFW_TRUE else GLFW.GLFW_FALSE)
		GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, if (maximized) GLFW.GLFW_TRUE else GLFW.GLFW_FALSE)
		val vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())
		SCREEN_WIDTH = vidMode!!.width()
		SCREEN_HEIGHT = vidMode.height()
		SCALE = SCREEN_HEIGHT / 1080f
		if (initialWidth == 0 || initialHeight == 0) {
			width = vidMode.width()
			height = vidMode.height()
			fullScreen = true
			window = GLFW.glfwCreateWindow(width, height, title, GLFW.glfwGetPrimaryMonitor(), 0)
		} else {
			width = initialWidth
			height = initialHeight
			fullScreen = false
			window = GLFW.glfwCreateWindow(width, height, title, 0, 0)
		}
		GLFW.glfwMakeContextCurrent(window)
		GL.createCapabilities()
		GLFW.glfwSwapInterval(if (vSync) 1 else 0)
		GLFW.glfwSetWindowSizeCallback(window) { window: Long, w: Int, h: Int ->
			width = w
			height = h
			resized = true
			GL11.glViewport(0, 0, width, height)
		}
		GLFW.glfwSetMouseButtonCallback(window) { window: Long, button: Int, action: Int, mods: Int ->
			mouseButtons[button] = action
		}
		GLFW.glfwSetKeyCallback(window) { window: Long, key: Int, scancode: Int, action: Int, mods: Int ->
			/* fix volume keys bug */if (key < 0 || key >= keys.size) {
			return@glfwSetKeyCallback
		}
			keys[key] = action
		}
		GL11.glClearColor(0f, 0f, 0f, 1f)
		GL11.glEnable(GL11.GL_TEXTURE_2D)
		//glEnable(GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND)
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

		//glEnable(GL_MULTISAMPLE);
		val awidth = IntArray(1)
		val aheight = IntArray(1)
		GLFW.glfwGetWindowSize(window, awidth, aheight)
		width = awidth[0]
		height = aheight[0]
	}

	fun setFullScreen(fullScreen: Boolean) {
		val primaryMonitor = GLFW.glfwGetPrimaryMonitor()
		val vidMode = GLFW.glfwGetVideoMode(primaryMonitor) ?: return
		this.fullScreen = fullScreen
		if (fullScreen) {
			width = vidMode.width()
			height = vidMode.height()
			GLFW.glfwSetWindowMonitor(
				window,
				primaryMonitor,
				0,
				0,
				vidMode.width(),
				vidMode.height(),
				vidMode.refreshRate()
			)
		} else {
			width = vidMode.width() / 2
			height = vidMode.height() / 2
			GLFW.glfwSetWindowMonitor(
				window,
				0,
				(vidMode.width() - width) / 2,
				(vidMode.height() - height) / 2,
				width,
				height,
				vidMode.refreshRate()
			)
		}
	}

	fun update() {
		for (i in mouseButtons.indices) if (mouseButtons[i] == 1) ++mouseButtons[i]
		for (i in keys.indices) if (keys[i] == 1) ++keys[i]
		resized = false
		GLFW.glfwPollEvents()
	}

	fun getFullScreen(): Boolean {
		return fullScreen
	}

	fun clear() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)
	}

	fun swap() {
		GLFW.glfwSwapBuffers(window)
	}

	fun close() {
		GLFW.glfwSetWindowShouldClose(window, true)
	}

	fun shouldClose(): Boolean {
		return GLFW.glfwWindowShouldClose(window)
	}

	fun key(keyCode: Int): Int {
		return keys[keyCode]
	}

	fun getMouseCoords(camera: Camera): Vector3f {
		val x = DoubleArray(1)
		val y = DoubleArray(1)
		GLFW.glfwGetCursorPos(window, x, y)
		val ret = Vector3f(x[0].toFloat(), y[0].toFloat(), 0f)
		return ret.mul(camera.width / width, camera.height / height, 1f)
	}

	fun mousePressed(button: Int): Int {
		return mouseButtons[button]
	}

	fun wasResized(): Boolean {
		return resized
	}

	companion object {
		var SCREEN_WIDTH = 0
		var SCREEN_HEIGHT = 0
		var SCALE = 0f

		fun terminate() {
			GLFW.glfwTerminate()
		}


	}
}
