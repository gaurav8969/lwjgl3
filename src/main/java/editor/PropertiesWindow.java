package editor;

import Renderer.PickingTexture;
import contra.GameObject;
import contra.MouseListener;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImVec2;
import scenes.Scene;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class PropertiesWindow {
    private PickingTexture pickingTexture;
    private GameObject activeGameObject = null;
    private int entityID;
    private ImVec2 topLeft;
    private ImVec2 windowSize;
    private float scrollX, scrollY;

    public PropertiesWindow(PickingTexture pickingTexture){
        this.pickingTexture = pickingTexture;
        topLeft = new ImVec2();
        windowSize = new ImVec2();
    }

    public void update(Scene currentScene, float dt){
        ImGuiIO io = imgui.internal.ImGui.getIO();
        //we search for the objects in the current scene
        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            int x = (int)MouseListener.getScreenX();
            int y = (int)MouseListener.getScreenY();
            if(!clickIsInsidePropertiesPanel()){
                entityID = pickingTexture.readPixel(x, y);
                GameObject go = currentScene.getGameObject(entityID);
                activeGameObject = go;
            }
        }
    }

    public void imgui(){
        if(activeGameObject != null){
            ImGui.begin("Properties");

            ImGui.getWindowSize(windowSize);
            ImGui.setCursorPos(0,0);
            ImGui.getCursorScreenPos(topLeft);
            scrollX = ImGui.getScrollX();
            scrollY = ImGui.getScrollY();

            activeGameObject.imGui();
            ImGui.end();
        }
    }

    boolean clickIsInsidePropertiesPanel(){
        //no properties panel if active g.o. is null
        if(activeGameObject == null){
            return false;
        }else{
            //for clicking inside properties panel and dragging the cursor out, e.g when selecting corner colours
            if (MouseListener.isDragging()) {
                return true;
            }
        }

        float leftX = topLeft.x;
        float bottomY = topLeft.y;
        float rightX = topLeft.x + windowSize.x;
        float topY = topLeft.y + windowSize.y;

        float xClick = MouseListener.getX()-scrollX;
        float yClick = MouseListener.getY()-scrollY;
        boolean isInside =  xClick >= leftX && xClick <= rightX &&
                yClick >= bottomY && yClick <= topY;
        return isInside;
    }
}