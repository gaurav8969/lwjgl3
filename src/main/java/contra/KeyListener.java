package contra;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class KeyListener {
    private static KeyListener instance = null;
    private boolean keyPressed[] = new boolean[350];
    private boolean keyBeginPress[] = new boolean[350];

    private KeyListener() {

    }

    public static void endFrame() {
        Arrays.fill(getInstance().keyBeginPress, false);
    }

    public static KeyListener getInstance() {
        if (KeyListener.instance == null) {
            KeyListener.instance = new KeyListener();
        }
        return KeyListener.instance;
    }


    public static void keyCallback(long window, int key, int scancode, int action, int mods) {
        if (action == GLFW_PRESS) {
            getInstance().keyPressed[key] = true;
            getInstance().keyBeginPress[key] = true;
        } else if (action == GLFW_RELEASE) {
            getInstance().keyPressed[key] = false;
            getInstance().keyBeginPress[key] = false;
        }
    }

    public static boolean isKeyPressed(int keyCode) {
        return getInstance().keyPressed[keyCode];
    }

    public static boolean keyBeginPress(int keyCode){
        return getInstance().keyBeginPress[keyCode];
    }

    public static void clear(){
        Arrays.fill(getInstance().keyBeginPress, false);
        Arrays.fill(getInstance().keyPressed, false);
    }

}