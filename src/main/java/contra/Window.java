package contra;

import observers.EventSystem;
import observers.Observer;
import observers.events.Event;
import observers.events.EventType;
import renderer.Framebuffer;
import renderer.PickingTexture;
import renderer.Shader;
import scenes.SceneInitializer;
import util.AssetPool;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import scenes.LevelEditorScene;
import scenes.Scene;
import util.Time;
import renderer.Renderer;
import editor.ImGuiLayer;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window implements Observer {
    private int width, height;
    private String title;
    private static Window window = null;
    private String glslVersion = null;
    private long glfwWindow;
    private static Scene currentScene;
    private ImGuiLayer imguiLayer;
    private Framebuffer framebuffer;
    private PickingTexture pickingTexture;
    private boolean runTimePlaying = false;


    private Window(){
        this.width = 960;
        this.height = 960;
        this.title = "Contra";
        EventSystem.addObserver(this);
    }
    private void init(){
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        glslVersion = "#version 330";

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
        glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) -> {
            Window.setWidth(newWidth);
            Window.setHeight(newHeight);
        });

        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        // Enable v-sync
        glfwSwapInterval(1);

        GL.createCapabilities();
        // Make the window visible
        glfwShowWindow(glfwWindow);

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        this.framebuffer = new Framebuffer(1920,1080);
        this.pickingTexture = new PickingTexture(1920,1080);
        glViewport(0,0,1920,1080);

        imguiLayer = new ImGuiLayer(pickingTexture,glfwWindow, glslVersion);
        imguiLayer.initImGui();

        Window.changeScene(new LevelEditorScene());
    }

    public static Window get(){
        if(Window.window == null){
            Window.window = new Window();
        }
        return Window.window;
    }

    public static void changeScene(SceneInitializer sceneInitializer) {
        if(currentScene != null){
            currentScene.destroy();
        }

        getImGuilayer().getPropertiesWindow().setActiveGameObject(null);
        currentScene = new Scene(sceneInitializer);
        currentScene.load();
        currentScene.init();
        currentScene.start();

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

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        float startTime = Time.getTime();
        float endTime;
        float dt = -1.0f;

        glClearColor(1f, 1f, 1f, 1.0f);

        Shader defaultShader = AssetPool.getShader("assets/shaders/default.glsl");
        Shader pickingShader = AssetPool.getShader("assets/shaders/pickingShader.glsl");

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(glfwWindow) ) {
            glfwPollEvents();

            glDisable(GL_BLEND);
            pickingTexture.enableWriting();

            glViewport(0,0,1920,1080);
            glClearColor(0f, 0f, 0f, 0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            Renderer.bindShader(pickingShader);
            currentScene.render();

            pickingTexture.disableWriting();
            glEnable(GL_BLEND);

            framebuffer.bind();

            currentScene.debugDraw().beginFrame();
            glClearColor(1f,1f,1f,1f);
            glClear(GL_COLOR_BUFFER_BIT); // clear the framebuffer

            if (dt >= 0) {
                currentScene.debugDraw().draw();
                Renderer.bindShader(defaultShader);
                if(runTimePlaying){
                    currentScene.update(dt);
                }else{
                    currentScene.editorUpdate(dt);
                }

                currentScene.render();
            }
            framebuffer.unbind();

            imguiLayer.update(currentScene,dt);

            glfwSwapBuffers(glfwWindow); // swap the color buffers
            MouseListener.endFrame(); //so camera controls and picking are not messed up

            endTime = Time.getTime();
            dt = endTime - startTime;
            startTime = endTime;
        }
    }

    public static Scene getScene(){
        return get().currentScene;
    }

    public static ImGuiLayer getImGuilayer(){return get().imguiLayer;}

    public static int getWidth() {
        return get().width;
    }

    public static int getHeight() {
        return get().height;
    }

    public static void setWidth(int newWidth) {
        get().width = newWidth;
    }

    public static void setHeight(int newHeight) {
        get().height = newHeight;
    }

    public static Framebuffer getFramebuffer(){
        return get().framebuffer;
    }

    public static float getTargetAspectRatio(){
        return 1f;
    }

    @Override
    public void onNotify(GameObject go, Event event){
        switch(event.type){
            case GameEngineStartPlay:
                this.runTimePlaying = true;
                currentScene.save();
                Window.changeScene(new LevelEditorScene());
                break;
            case GameEngineStopPlay:
                this.runTimePlaying = false;
                Window.changeScene(new LevelEditorScene());
                break;
            case LoadLevel:
                Window.changeScene(new LevelEditorScene());
                break;
            case SaveLevel:
                currentScene.save();
                break;
        }
    }
}