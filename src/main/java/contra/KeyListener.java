package contra;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class KeyListener {
    private static KeyListener instance = null;
    private boolean keyPressed[] = new boolean[350];

    private KeyListener() {

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
        } else if (action == GLFW_RELEASE) {
            getInstance().keyPressed[key] = false;
        }
    }

    public static boolean isKeyPressed(int KeyCode) {
        if (KeyCode < getInstance().keyPressed.length) {
            return getInstance().keyPressed[KeyCode];
        }else{
            return false;
        }
    }
}
