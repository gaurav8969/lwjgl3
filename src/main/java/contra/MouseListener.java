package contra;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener{
    private static MouseListener instance = null;
    private double scrollX,scrollY,lastY,lastX,xPos,yPos;
    private boolean mouseButtonPressed[] = new boolean[9];
    private boolean isDragging;
    private Vector2f gameViewportPos = new Vector2f();
    private Vector2f gameViewportSize = new Vector2f();

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

    public static float getOrthoX() {
        float currentX = getX() - getInstance().gameViewportPos.x;
        currentX = (currentX / getInstance().gameViewportSize.x) * 2.0f - 1.0f;
        Vector4f tmp = new Vector4f(currentX, 0, 0, 1);

        Camera camera = Window.getScene().camera();
        Matrix4f viewProjection = new Matrix4f();
        camera.getInverseViewMatrix().mul(camera.getInverseProjectionMatrix(), viewProjection);
        tmp.mul(viewProjection);
        currentX = tmp.x;

        return currentX;
    }

    //get mouse clicks in world coords
    public static float getOrthoY() {
        float currentY = getY() - getInstance().gameViewportPos.y;
        currentY = -((currentY / getInstance().gameViewportSize.y) * 2.0f - 1.0f);
        Vector4f tmp = new Vector4f(0, currentY, 0, 1);

        Camera camera = Window.getScene().camera();
        Matrix4f viewProjection = new Matrix4f();
        camera.getInverseViewMatrix().mul(camera.getInverseProjectionMatrix(), viewProjection);
        tmp.mul(viewProjection);
        currentY = tmp.y;

        return currentY;
    }

    public static void setGameViewportPos(Vector2f gameViewportPos) {
        getInstance().gameViewportPos.set(gameViewportPos);
    }

    public static void setGameViewportSize(Vector2f gameViewportSize) {
        getInstance().gameViewportSize.set(gameViewportSize);
    }
}
