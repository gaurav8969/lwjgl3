package editor;

import Renderer.PickingTexture;
import contra.GameObject;
import contra.MouseListener;
import imgui.ImGui;
import scenes.Scene;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class PropertiesWindow {
    private PickingTexture pickingTexture;
    private GameObject activeGameObject = null;
    private int entityID;

    public PropertiesWindow(PickingTexture pickingTexture){
        this.pickingTexture = pickingTexture;
    }

    public void update(Scene currentScene, float dt){
        //we search for the objects in the current scene
        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            int x = (int)MouseListener.getScreenX();
            int y = (int)MouseListener.getScreenY();
            entityID = pickingTexture.readPixel(x, y);
            GameObject go = currentScene.getGameObject(entityID);
            if(go != null){activeGameObject = go;}
        }
    }

    public void imgui(){
        if(activeGameObject != null){
            ImGui.begin("Properties");
            activeGameObject.imGui();
            ImGui.end();
        }
    }
}
