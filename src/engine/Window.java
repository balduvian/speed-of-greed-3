package engine;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;

public class Window {

	public static int
		SCREEN_WIDTH,
		SCREEN_HEIGHT;
	
	public static float SCALE;
	
	private long window;
	private int width, height;
	private boolean resized;
	private boolean fullScreen;

	private final int[] mouseButtons = new int[GLFW_MOUSE_BUTTON_LAST + 1];
	private final int[] keys = new int[GLFW_KEY_LAST + 1];
	
	public Window(String title, boolean vSync) {
		init(0, 0, title, vSync, false, false, false);
	}
	
	public Window(String title, boolean vSync, boolean resizable, boolean decorated) {
		init(800, 500, title, vSync, resizable, decorated, true);
	}
	
	public Window(int width, int height, String title, boolean vSync, boolean resizable, boolean decorated) {
		init(width, height, title, vSync, resizable, decorated, false);
	}
	
	public void init(int lwidth, int lheight, String title, boolean vSync, boolean resizable, boolean decorated, boolean maximized) {
		glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));

		Arrays.fill(mouseButtons, 0);
		
		if(!glfwInit()) {
			System.err.println("GLFW failed to initialize!");
			System.exit(-1);
		}
		
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
		
		glfwWindowHint(GLFW_SAMPLES, 8);
		glfwWindowHint(GLFW_RESIZABLE, resizable ? GLFW_TRUE : GLFW_FALSE);
		glfwWindowHint(GLFW_DECORATED, decorated ? GLFW_TRUE : GLFW_FALSE);
		glfwWindowHint(GLFW_MAXIMIZED, maximized ? GLFW_TRUE : GLFW_FALSE);
		
		GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		SCREEN_WIDTH = vidMode.width();
		SCREEN_HEIGHT = vidMode.height();
		SCALE = SCREEN_HEIGHT / 1080f;
		if(lwidth == 0 || lheight == 0) {
			width = vidMode.width();
			height = vidMode.height();
			fullScreen = true;
			window = glfwCreateWindow(width, height, title, glfwGetPrimaryMonitor(), 0);
		}
		else {
			this.width = lwidth;
			this.height = lheight;
			fullScreen = false;
			window = glfwCreateWindow(width, height, title, 0, 0);
		}
		
		glfwMakeContextCurrent(window);
		createCapabilities();
		 
		glfwSwapInterval(vSync ? 1 : 0);
		
		glfwSetWindowSizeCallback(window, (long window, int w, int h) -> {
			width = w;
			height = h;
			resized = true;
			glViewport(0, 0, width, height);
		});
		
		glfwSetMouseButtonCallback(window, (long window, int button, int action, int mods) -> {
			mouseButtons[button] = action;
		});
		
		glfwSetKeyCallback(window, (long window, int key, int scancode, int action, int mods) -> {
			/* fix volume keys bug */
			if (key < 0 || key >= keys.length) {
				return;
			}
			keys[key] = action;
		});
		
		glClearColor(0, 0, 0, 1);
		
		glEnable(GL_TEXTURE_2D);
		//glEnable(GL_DEPTH_TEST);
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		//glEnable(GL_MULTISAMPLE);
		
		int[] awidth = new int[1], aheight = new int[1];
		glfwGetWindowSize(window, awidth, aheight);
		width = awidth[0];
		height = aheight[0];
	}

	public void setFullScreen(boolean fullScreen) {
		var primaryMonitor = glfwGetPrimaryMonitor();
		var vidMode = glfwGetVideoMode(primaryMonitor);
		if (vidMode == null) return;

		this.fullScreen = fullScreen;

		if (fullScreen) {
			this.width = vidMode.width();
			this.height = vidMode.height();
			glfwSetWindowMonitor(window, primaryMonitor , 0, 0, vidMode.width(), vidMode.height(), vidMode.refreshRate());
		} else {
			this.width = vidMode.width() / 2;
			this.height = vidMode.height() / 2;
			glfwSetWindowMonitor(window, 0, (vidMode.width() - this.width) / 2, (vidMode.height() - this.height) / 2, this.width, this.height, vidMode.refreshRate());
		}
	}
	
	public void update() {
		for (int i = 0; i < mouseButtons.length; i++)
			if (mouseButtons[i] == 1)
				++mouseButtons[i];
		for (int i = 0; i < keys.length; i++)
			if (keys[i] == 1)
				++keys[i];
		resized = false;
		glfwPollEvents();
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}

	public boolean getFullScreen() {
		return fullScreen;
	}
	
	public void clear() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	
	public void swap() {
		glfwSwapBuffers(window);
	}
	
	public void close() {
		glfwSetWindowShouldClose(window, true);
	}
	
	public static void terminate() {
		glfwTerminate();
	}
	
	public boolean shouldClose() {
		return glfwWindowShouldClose(window);
	}
	
	public int key(int keyCode) {
		return keys[keyCode];
	}
	
	public Vector3f getMouseCoords(Camera camera) {
		double[] x = new double[1], y = new double[1];
		glfwGetCursorPos(window, x, y);
		Vector3f ret = new Vector3f((float) x[0], (float) y[0], 0);
		return ret.mul(camera.getWidth() / this.width, camera.getHeight() / this.height, 1);
	}
	
	public int mousePressed(int button) {
		return mouseButtons[button];
	}
	
	public boolean wasResized() {
		return resized;
	}
}
