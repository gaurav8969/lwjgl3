package contra;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import util.Time;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private int width, height;
    private String title;
    private static Window window = null;
    private long glfwWindow;
    private float r,g,b,a;
    private static Scene currentScene;

    private Window(){
        this.width = 1280;
        this.height = 960;
        this.title = "Contra";
        r = 0;
        g = 0;
        b = 0;
        a = 1;
    }
    public static Window get(){
        if(Window.window == null){
            Window.window = new Window();
        }
        return Window.window;
    }

    public static void changeScene(int newScene){
        switch(newScene){
            case 0:
                currentScene = new LevelEditorScene();
                currentScene.init();
                currentScene.start();
                break;
            case 1:
                currentScene = new LevelScene();
            default:
                assert false: "Unknown Scene'" + newScene + "'";
                break;
        }
    }
    public void run(){
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        //free the memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        //terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_MAXIMIZED,GLFW_FALSE);

        // Create the window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title,NULL, NULL);
        if (glfwWindow == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);

        glfwSetKeyCallback(glfwWindow,KeyListener::keyCallback);
        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(glfwWindow);

        GL.createCapabilities();

        Window.changeScene(0);
    }


    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        float startTime = Time.getTime();
        float endTime;
        float dt = -1.0f;

        glClearColor(r, g, b, a);
        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(glfwWindow) ) {
            glfwPollEvents();

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            if (dt >= 0) {
                currentScene.update(dt);
            }

            glfwSwapBuffers(glfwWindow); // swap the color buffers

            endTime = Time.getTime();
            dt = endTime - startTime;
            startTime = endTime;
        }
    }

    public static Scene getScene(){
        return get().currentScene;
    }
}