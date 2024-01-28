package editor;

import physics2D.components.Box2DCollider;
import physics2D.components.CircleCollider;
import physics2D.components.RigidBody2D;
import renderer.PickingTexture;
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

    public PropertiesWindow(PickingTexture pickingTexture){
        this.pickingTexture = pickingTexture;
        topLeft = new ImVec2();
        windowSize = new ImVec2();
    }

    public void update(Scene currentScene, float dt){
        ImGuiIO io = imgui.internal.ImGui.getIO();
        //we search for the objects in the current scene

        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && GameViewWindow.isFocused() && !MouseListener.isDragging()) {
            int x = (int)MouseListener.getScreenX();
            int y = (int)MouseListener.getScreenY();
            entityID = pickingTexture.readPixel(x, y);
            GameObject go = currentScene.getGameObject(entityID);
            activeGameObject = go;
        }
    }

    public void imgui(){
        if(activeGameObject != null){
            ImGui.begin("Properties");

            if(ImGui.beginPopupContextWindow("Component Adder")){
                if(ImGui.menuItem("Add Rigidbody")){
                    if(activeGameObject.getComponent(RigidBody2D.class) == null){
                        activeGameObject.addComponent(new RigidBody2D());
                    }
                }

                if(ImGui.menuItem("Add Box Collider")){
                    if(activeGameObject.getComponent(Box2DCollider.class) == null &&
                    activeGameObject.getComponent(CircleCollider.class) == null){
                        activeGameObject.addComponent(new Box2DCollider());
                    }
                }

                if(ImGui.menuItem("Add Circle Collider")){
                    if(activeGameObject.getComponent(Box2DCollider.class) == null &&
                            activeGameObject.getComponent(CircleCollider.class) == null){
                        activeGameObject.addComponent(new CircleCollider());
                    }
                }

                ImGui.endPopup();
            }

            ImGui.setCursorPos(0,0);

            ImGui.getWindowSize(windowSize);

            ImGui.getCursorScreenPos(topLeft);
            topLeft.x += ImGui.getScrollX();
            topLeft.y += ImGui.getScrollY();

            activeGameObject.imGui();
            ImGui.end();
        }
    }

    public GameObject getActiveGameObject(){
        return activeGameObject;
    }

    public void setActiveGameObject(GameObject go){
        this.activeGameObject = go;
    }
}