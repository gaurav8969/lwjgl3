package contra;

import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener{
    private static MouseListener instance = null;
    private double scrollX,scrollY,lastY,lastX,xPos,yPos;
    private boolean mouseButtonPressed[] = new boolean[9];
    private boolean isDragging;

    private MouseListener(){
        this.scrollX = 0.0;
        this.scrollY = 0.0;
        this.xPos = 0.0;
        this.yPos = 0.0;
        this.lastX = 0.0;
        this.lastY = 0.0;

    }

    public static MouseListener getInstance(){
        if(instance == null){
           MouseListener.instance = new MouseListener();
        }
        return MouseListener.instance;
    }

    public static void mousePosCallback(long window, double xpos, double ypos){
        getInstance().lastX = getInstance().xPos;
        getInstance().lastY = getInstance().yPos;

        getInstance().xPos = xpos;
        getInstance().yPos = ypos;

    //pos callback is called if the mouse is moved, and so if any of the buttons is clicked as that happens, we've dragging
        getInstance().isDragging = getInstance().mouseButtonPressed[0] || getInstance().mouseButtonPressed[1]
                || getInstance().mouseButtonPressed[2];
    }

    public static void mouseButtonCallback(long window, int button,int action,int mods){
        if (action == GLFW_PRESS){
            if(button < getInstance().mouseButtonPressed.length) {
                //stays true till next release action
                getInstance().mouseButtonPressed[button] = true;
            }
        }else if(action == GLFW_RELEASE){
            if(button < getInstance().mouseButtonPressed.length) {
                getInstance().mouseButtonPressed[button] = false;
                getInstance().isDragging = false;
            }
        }
    }

    public static void mouseScrollCallback(long Window, double xOffset, double yOffset){
        getInstance().scrollX = xOffset;
        getInstance().scrollY = yOffset;
    }

    public static void endFrame(){
        getInstance().scrollX = 0;
        getInstance().scrollY = 0;
        getInstance().lastX = getInstance().xPos;
        getInstance().lastY = getInstance().yPos;
    }

    //get mouse click in screen coords
    public static float getX(){
        return (float)getInstance().xPos;
    }

    public static float getY(){
        return (float)getInstance().yPos;
    }

    //get mouse in click world coords
    public static float getOrthoX(){
        float normalizedX = (getX()/Window.getWidth()) * 2.0f - 1.0f;
        Vector4f tmp = new Vector4f(normalizedX,0,0,1);
        tmp.mul(Window.getScene().camera().getInverseProjectionMatrix()).
                mul(Window.getScene().camera().getInverseViewMatrix());
        return tmp.x; //x in world coords
    }

    public static float getOrthoY(){
        //account for that OpenGL sees bottom-left as 0,0 & GLFW cursor callback sees top-left as 0,0
        float normalizedY = (1- getY()/Window.getHeight()) * 2 - 1;
        Vector4f tmp = new Vector4f(0,normalizedY,0,1);
        tmp.mul(Window.getScene().camera().getInverseProjectionMatrix()).
                mul(Window.getScene().camera().getInverseViewMatrix());
        return tmp.y; //y in world coords
    }

    public static float getDx(){
        return (float)(getInstance().lastX-getInstance().xPos);
    }

    public static float getDy(){
        return (float)(getInstance().lastY-getInstance().yPos);
    }

    public static float getScrollX(){
        return (float)getInstance().scrollX;
    }

    public static float getScrollY(){
        return (float)getInstance().scrollY;
    }

    public static boolean isDragging(){
        return getInstance().isDragging;
    }

    public static boolean mouseButtonDown(int button){
        if(button < getInstance().mouseButtonPressed.length) {
            return getInstance().mouseButtonPressed[button];
        }else{
            return false;
        }
    }
}
