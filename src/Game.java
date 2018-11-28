import static org.lwjgl.glfw.Callbacks.errorCallbackPrint;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_3;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwGetMouseButton;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;


public class Game extends Thread implements LoopItem {
	private Map<String, World> $worlds;
	private static World $activeWorld;
	
	private static Mouse $mouse;
	
	private GLFWErrorCallback $errCallback;
	private GLFWKeyCallback $keyCallback;
	private long $window;
	
	private int $width;
	private int $height;
	private int $loops = 0;
	
	private boolean $wireframe;
	private boolean $drawColbox;
	
	public Game() {
		$mouse = new Mouse();
		$activeWorld = new World(new Vector3d($width, 1, $height));
		$worlds = new HashMap<>();
		init();
	}
	
	private void init() {
		$width = 1280;
		$height = 720;
		System.out.println("Running LWJGL " + Sys.getVersion() + ".");
		
		//Set error callback
		$errCallback = errorCallbackPrint(System.err);
		glfwSetErrorCallback($errCallback);
		
		//Init GLFW
		if(glfwInit() != GL11.GL_TRUE) {
			System.out.println("Failed to initialize GLFW");
			throw new IllegalStateException("Unable to initialize GLFW");
		}
		
		//Configure window
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable
 
        //Create Window
        $window = glfwCreateWindow($width, $height, "Project OpenGL", NULL, NULL);
        if ($window == NULL) {
        	System.out.println("Failed to create GLFW window");
            throw new RuntimeException("Failed to create the GLFW window");
        }
        
     // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback($window, $keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE ) 
                    glfwSetWindowShouldClose(window, GL_TRUE); // We will detect this in our rendering loop
                
                if (key == GLFW.GLFW_KEY_F1 && action == GLFW_RELEASE) {
                	$wireframe = !$wireframe;
                	if($wireframe) {
                		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                	}
                	else {
                		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
                	}
                }
                
                if (key == GLFW.GLFW_KEY_F2 && action == GLFW_RELEASE) {
                	GL11.glShadeModel(GL11.GL_SMOOTH);
                }
                
                if (key == GLFW.GLFW_KEY_F3 && action == GLFW_RELEASE) {
                	GL11.glShadeModel(GL11.GL_FLAT);
                }
                
                if (key == GLFW.GLFW_KEY_F4 && action == GLFW_RELEASE) {
                	$drawColbox = !$drawColbox;
                }
                
                if($activeWorld != null) {
                	$activeWorld.onKeyInput(window, key, scancode, action, mods);
                }
            }
        });
        
        // Get the resolution of the primary monitor
        ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Center our window
        glfwSetWindowPos($window, (GLFWvidmode.width(vidmode) - $width) / 2, (GLFWvidmode.height(vidmode) - $height) / 2);
 
        // Make the OpenGL context current
        glfwMakeContextCurrent($window);
        // Enable v-sync
        glfwSwapInterval(1);
 
        // Make the window visible
        glfwShowWindow($window);
        GLContext.createFromCurrent();
	}
	
	public void addWorld(String name, World world) {
		world.generateMap();
		$worlds.put(name, world);
	}
	
	public void switchWorld(String name) {
		synchronized($activeWorld) {
			if($worlds.containsKey(name))
				$activeWorld = $worlds.get(name);
		}
	}
	
	public World getActiveWorld() {
		return $activeWorld;
	}
	
	public World getWorld(String name) {
		return $worlds.get(name);
	}
	
	public int getWorldCount() {
		return $worlds.size();
	}
	
	public void run() {
		loop();
	}
	
	private void loop() {
		long lastTime = System.currentTimeMillis();
		long delta;
		
		//Critical
		//GLContext.createFromCurrent();
        // Set the clear color
        
		GL11.glEnable(GL11.GL_SMOOTH);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glEnable(GL11.GL_LIGHTING);
		
		FloatBuffer buffer = BufferUtils.createFloatBuffer(4);
		float[] arr = new float[] {0f, 0f, 0f, 0f};
		buffer.put(arr);
		buffer.flip();
		GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, buffer);
		//buffer.put(arr);
		GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_SPECULAR, buffer);
		
		GL11.glEnable(GL11.GL_LIGHT0);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		//GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        //GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);

        while (glfwWindowShouldClose($window) == GL_FALSE ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
            
            
            delta = System.currentTimeMillis() - lastTime;
            if(delta != 0)
            	System.out.println("Fps: " + (1000.0 / (double) delta));
			lastTime = System.currentTimeMillis();
            update(delta);
            physicsUpdate(delta);
            draw();
            
           
            glfwSwapBuffers($window); // swap the color buffers
        }
	}
	
	public void update(long delta) {
		DoubleBuffer x = BufferUtils.createDoubleBuffer(1);
		DoubleBuffer y = BufferUtils.createDoubleBuffer(1);
		
		boolean leftMouseDown = glfwGetMouseButton($window, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS;
		boolean rightMouseDown = glfwGetMouseButton($window, GLFW_MOUSE_BUTTON_2) == GLFW_PRESS;
		boolean middleMouseDown = glfwGetMouseButton($window, GLFW_MOUSE_BUTTON_3) == GLFW_PRESS;
		$mouse.setLeftMouseDown(leftMouseDown);
		$mouse.setRightMouseDown(rightMouseDown);
		$mouse.setMiddleMouseDown(middleMouseDown);
		
		glfwGetCursorPos($window, x, y);
		glfwSetCursorPos($window, $width / 2, $height / 2);
		
		x.rewind();
		y.rewind();
		
		int newX = (int) x.get();
		int newY = (int) y.get();
		
		int deltaX = newX - $width / 2;
		int deltaY = newY - $height / 2;
		$mouse.setXY(newX, newY);
		if($loops > 5)
			$mouse.setDeltaXY(deltaX, deltaY);
		else {
			$loops++;
		}
			
		if($activeWorld != null)
			$activeWorld.update(delta);
	}
	
	public void physicsUpdate(long delta) {
		if($activeWorld != null) {
			$activeWorld.physicsUpdate(delta);
		}
	}
	
	public void draw() {
		if($activeWorld != null) {
			$activeWorld.draw();
		}
	}
	
	public static Mouse getMouse() {
		return $mouse;
	}
	
	public int getWidth() {
		return $width;
	}
	
	public int getHeight() {
		return $height;
	}
	
	public boolean getDrawColBox() {
		return $drawColbox;
	}
}
