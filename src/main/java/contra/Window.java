package contra;

import components.Sprite;
import components.SpriteRenderer;
import editor.KeyControls;
import observers.EventSystem;
import observers.Observer;
import observers.events.Event;
import observers.events.EventType;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.opengl.ARBCopyBuffer;
import physics2D.Physics2D;
import renderer.*;
import scenes.SceneInitializer;
import util.AssetPool;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import scenes.LevelEditorScene;
import scenes.Scene;
import util.Time;
import editor.ImGuiLayer;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.openal.ALC10.*;
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
    private Framebuffer effectsFramebuffer;
    private PickingTexture pickingTexture;
    private boolean runTimePlaying = false;
    private long audioContext;
    private long audioDevice;

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

        // Initialize the audio device
        String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        audioDevice = alcOpenDevice(defaultDeviceName);

        int[] attributes = {0};
        audioContext = alcCreateContext(audioDevice, attributes);
        alcMakeContextCurrent(audioContext);

        ALCCapabilities alcCapabilities = ALC.createCapabilities(audioDevice);
        ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);

        if (!alCapabilities.OpenAL10) {
            assert false : "Audio library not supported.";
        }

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        this.framebuffer = new Framebuffer(1920,1080);
        this.effectsFramebuffer = new Framebuffer(1920,1080);
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

    public static long getID(){
        return get().glfwWindow;
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
        //Destroy the audio context
        alcDestroyContext(audioContext);
        alcCloseDevice(audioDevice);

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

        //glClearColor(1f, 1f, 1f, 1.0f);

        Shader defaultShader = AssetPool.getShader("assets/shaders/default.glsl");
        Shader pickingShader = AssetPool.getShader("assets/shaders/pickingShader.glsl");
        Shader effectsShader = AssetPool.getShader("assets/shaders/effectsShader.glsl");

        Sprite effectSprite = new Sprite();
        effectSprite.setTexture(framebuffer.texture);

        GameObject effectsObject = Prefabs.generateSpriteObject(effectSprite, 6, 3);
        effectsObject.tf.position.x = 3;
        effectsObject.tf.position.y = 1.5f;
        SpriteRenderer spr = effectsObject.getComponent(SpriteRenderer.class);

        EffectsBatch effectsBatch = new EffectsBatch(1, 1,0,getScene().renderer());
        effectsBatch.init();
        effectsBatch.addSprite(spr);

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
            if(runTimePlaying){
                Vector4f clearColour = getScene().camera().clearColour;
                glClearColor(clearColour.x, clearColour.y, clearColour.z, clearColour.w);
            }else{
                glClearColor(1f,1f,1f,1f);
            }

            glClear(GL_COLOR_BUFFER_BIT); // clear the framebuffer

            if (dt >= 0) {
                Renderer.bindShader(defaultShader);
                if(runTimePlaying){
                    currentScene.update(dt);
                }else{
                    currentScene.editorUpdate(dt);
                }
                currentScene.render();
                currentScene.debugDraw().draw();
            }
            framebuffer.unbind();

            effectsFramebuffer.bind();
            glClear(GL_COLOR_BUFFER_BIT); // clear the framebuffer
            effectsBatch.render();
            effectsFramebuffer.unbind();

            imguiLayer.update(currentScene,dt);

            glfwSwapBuffers(glfwWindow); // swap the color buffers
            MouseListener.endFrame(); //so camera controls and picking are not messed up
            KeyListener.endFrame();

            endTime = Time.getTime();
            dt = endTime - startTime;
            startTime = endTime;
        }
    }

    public static Physics2D getPhysics(){
        return currentScene.physics2D;
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

    public static Framebuffer getEffectsFramebuffer(){
        return get().effectsFramebuffer;
    }

    public static float getTargetAspectRatio(){
        return 1278.0f/931.0f;
    }//copied from the viewport ss dimensions

    @Override
    public void onNotify(GameObject go, Event event){
        switch(event.type){
            case GameEngineStartPlay:
                this.runTimePlaying = true;
                AssetPool.getSound("assets/sounds/main-theme-overworld.ogg").play();
                currentScene.save();
                Window.changeScene(new LevelEditorScene());
                break;
            case GameEngineStopPlay:
                AssetPool.getSound("assets/sounds/main-theme-overworld.ogg").stop();
                this.runTimePlaying = false;
                Window.changeScene(new LevelEditorScene());
                break;
            case LoadLevel:
                AssetPool.getSound("assets/sounds/main-theme-overworld.ogg").play();
                Window.changeScene(new LevelEditorScene());
                break;
            case SaveLevel:

                currentScene.save();
                break;
        }
    }
}