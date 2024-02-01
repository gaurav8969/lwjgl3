package contra;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import javax.management.monitor.MonitorSettingException;

import static org.lwjgl.glfw.GLFW.*;

public class MouseListener{
    private static MouseListener instance = null;
    private double scrollX,scrollY,lastY,lastX,xPos,yPos,worldX, worldY, lastWorldX, lastWorldY;
    private boolean mouseButtonPressed[] = new boolean[9];
    private boolean isDragging;
    private Vector2f gameViewportPos = new Vector2f();
    private Vector2f gameViewportSize = new Vector2f();
    private boolean wasDragging = false;

    //in world coords
    private Vector2f dragStart;
    private Vector2f dragDuration;
    private MouseListener(){
        this.scrollX = 0.0;
        this.scrollY = 0.0;
        this.xPos = 0.0;
        this.yPos = 0.0;
        this.lastX = 0.0;
        this.lastY = 0.0;
        this.dragDuration = new Vector2f(0,0);
    }

    public static MouseListener getInstance(){
        if(instance == null){
           MouseListener.instance = new MouseListener();
        }
        return MouseListener.instance;
    }

    public static void mousePosCallback(long window, double xpos, double ypos){
        getInstance().xPos = xpos;
        getInstance().yPos = ypos;

        //calc. and update latest world coords
        calcWorldX();
        calcWorldY();

        //pos callback called if mouse moves, simultaneous button clicking is dragging
        getInstance().isDragging = getInstance().mouseButtonPressed[0] || getInstance().mouseButtonPressed[1]
                || getInstance().mouseButtonPressed[2];

        //pos callback called if mouse moves, simultaneous button clicking is dragging
        getInstance().isDragging = getInstance().mouseButtonPressed[0] || getInstance().mouseButtonPressed[1]
                || getInstance().mouseButtonPressed[2];

        if(!getInstance().wasDragging && isDragging()){
            getInstance().wasDragging = true;
            getInstance().dragStart = new Vector2f(getOrthoX(), getOrthoY());
        }else if(!isDragging()){
            getInstance().wasDragging = false;
            getInstance().dragDuration = new Vector2f(0,0);
        }

        if(getInstance().wasDragging){
            Vector2f dragChange = new Vector2f(getOrthoX(), getOrthoY()).sub(getInstance().dragStart);
            getInstance().dragDuration = new Vector2f(dragChange.x, dragChange.y);
        }
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
        getInstance().lastWorldX = getInstance().worldX;
        getInstance().lastWorldY = getInstance().worldY;
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

    public static float getWorldDx(){
        return (float)(getInstance().worldX - getInstance().lastWorldX);
    }

    public static float getWorldDy(){
        return (float)(getInstance().worldY - getInstance().lastWorldY);
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

    public static float getScreenX(){
        float currentX = getX() - getInstance().gameViewportPos.x;
        currentX = (currentX / getInstance().gameViewportSize.x) * 1920.0f;
        return currentX;
    }

    public static float getScreenY(){
        float currentY = getY() - getInstance().gameViewportPos.y;
        currentY = 1080.0f - ((currentY / getInstance().gameViewportSize.y) * 1080.0f);
        return currentY;
    }

    public static float getOrthoX() {
        return (float)getInstance().worldX;
    }

    //get mouse clicks in world coords
    public static float getOrthoY() {
        return (float)getInstance().worldY;
    }

    private static void calcWorldX(){
        float currentX = getX() - getInstance().gameViewportPos.x;
        currentX = (currentX / getInstance().gameViewportSize.x) * 2.0f - 1.0f;
        Vector4f tmp = new Vector4f(currentX, 0, 0, 1);

        Camera camera = Window.getScene().camera();
        Matrix4f viewProjection = new Matrix4f();
        camera.getInverseViewMatrix().mul(camera.getInverseProjectionMatrix(), viewProjection);
        tmp.mul(viewProjection);
        currentX = tmp.x;

        getInstance().worldX = currentX;
    }

    private static void calcWorldY(){
        float currentY = getY() - getInstance().gameViewportPos.y;
        currentY = -((currentY / getInstance().gameViewportSize.y) * 2.0f - 1.0f);
        Vector4f tmp = new Vector4f(0, currentY, 0, 1);

        Camera camera = Window.getScene().camera();
        Matrix4f viewProjection = new Matrix4f();
        camera.getInverseViewMatrix().mul(camera.getInverseProjectionMatrix(), viewProjection);
        tmp.mul(viewProjection);
        currentY = tmp.y;

        getInstance().worldY = currentY;
    }

    public static void setGameViewportPos(Vector2f gameViewportPos) {
        getInstance().gameViewportPos.set(gameViewportPos);
    }

    public static void setGameViewportSize(Vector2f gameViewportSize) {
        getInstance().gameViewportSize.set(gameViewportSize);
    }


    //get drag duration
    public static Vector2f getDrag(){
        return getInstance().dragDuration;
    }
    public static boolean wasDragging(){return getInstance().wasDragging;}

    public static Vector2f dragStart(){
        if(isDragging()){
            return getInstance().dragStart;
        }
        return null;
    }

    public static Vector2f worldToScreen(Vector2f world){
        Camera camera = Window.getScene().camera();
        Vector4f ndcSpacePos = new Vector4f(world.x, world.y, 0, 1);
        Matrix4f view = new Matrix4f(camera.getViewMatrix());
        Matrix4f projection = new Matrix4f(camera.getProjectionMatrix());
        ndcSpacePos.mul(projection.mul(view));
        Vector2f windowSpace = new Vector2f(ndcSpacePos.x, ndcSpacePos.y).mul(1.0f / ndcSpacePos.w);//perspective

        //map ndc -1 to +1 coords to [0,1] range
        windowSpace.add(new Vector2f(1.0f, 1.0f)).mul(0.5f);
        windowSpace.mul(Window.getFramebuffer().getDimensions());

        return windowSpace;
    }

    public static Vector2f screenToWorld(Vector2f screenCoords){
        Vector2f normalizedScreenCords = new Vector2f(
                screenCoords.x / Window.getWidth(),
                screenCoords.y / Window.getHeight()
        );
        normalizedScreenCords.mul(2.0f).sub(new Vector2f(1.0f, 1.0f));
        Camera camera = Window.getScene().camera();
        Vector4f tmp = new Vector4f(normalizedScreenCords.x, normalizedScreenCords.y,
                0, 1);
        Matrix4f inverseView = new Matrix4f(camera.getInverseViewMatrix());
        Matrix4f inverseProjection = new Matrix4f(camera.getInverseProjectionMatrix());
        tmp.mul(inverseView.mul(inverseProjection));
        return new Vector2f(tmp.x, tmp.y);
    }
}