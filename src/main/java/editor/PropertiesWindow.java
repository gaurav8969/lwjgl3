package editor;

import components.SpriteRenderer;
import contra.KeyListener;
import contra.Window;
import org.joml.Vector2f;
import org.joml.Vector4f;
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

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DELETE;

public class PropertiesWindow {
    private PickingTexture pickingTexture;
    //private GameObject activeGameObject = null;
    private int entityID;
    private ImVec2 topLeft;
    private ImVec2 windowSize;
    private List<GameObject> activeGameObjects;
    private final float debounceTime = 0.1f;
    private float debounce = 0.0f;

    public PropertiesWindow(PickingTexture pickingTexture){
        this.pickingTexture = pickingTexture;
        topLeft = new ImVec2();
        windowSize = new ImVec2();
        this.activeGameObjects = new ArrayList<>();
    }

    public void update(Scene currentScene, float dt){
        debounce -= dt;

        //we search for the objects in the current scene
        if(!activeGameObjects.isEmpty()){
            if(activeGameObjects.size() == 1){
                setActiveGameObject(activeGameObjects.get(0));
            }else{
                if(KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) &&
                        KeyListener.keyBeginPress(GLFW_KEY_D)) {
                    for(GameObject go: activeGameObjects){
                        GameObject newObj = go.copy();
                        Window.getScene().addGameObjectToScene(newObj);
                        newObj.tf.position.add(0.1f, 0.1f);
                    }
                }else if(KeyListener.keyBeginPress(GLFW_KEY_DELETE)){
                    for(GameObject go: activeGameObjects){
                        go.destroy();
                    }
                }
            }
        }

        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && GameViewWindow.isFocused() && !MouseListener.isDragging()
            && debounce < 0){
            debounce = debounceTime;
            int x = (int)MouseListener.getScreenX();
            int y = (int)MouseListener.getScreenY();
            entityID = pickingTexture.readPixel(x, y);
            GameObject go = currentScene.getGameObject(entityID);

            if(go == null){
                clearActiveObjects();
                return;
            }

            if(!activeGameObjects.contains(go) && go.getComponent(Unpickable.class) == null){
                for(GameObject obj: activeGameObjects){
                    obj.getComponent(SpriteRenderer.class).setColour(new Vector4f(1,1,1,1));
                }
                activeGameObjects.clear();
                go.getComponent(SpriteRenderer.class).setColour(new Vector4f(0.8f,0.8f,0.8f,0.5f));
                activeGameObjects.add(go);
            }
        }
    }

    public void imgui(){
        if(activeGameObjects.size() == 1 && activeGameObjects.get(0) != null){
            GameObject activeGameObject = activeGameObjects.get(0);
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
                        activeGameObject.addComponent(new Box2DCollider()
                                .setHalfSize(new Vector2f(activeGameObject.tf.scale).mul(0.5f)));
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
        if(activeGameObjects.size() == 1){
            return activeGameObjects.get(0);
        }else{
            return null;
        }
    }

    public void setActiveGameObject(GameObject go){
        if(go != null) {
            clearActiveObjects();
            addActiveGameObject(go);
        }
    }

    public PickingTexture getPickingTexture(){return pickingTexture;}

    public void addActiveGameObject(GameObject go){
        if(activeGameObjects.contains(go) || go == null){return;}

        activeGameObjects.add(go);
        go.getComponent(SpriteRenderer.class).setColour(new Vector4f(0.8f,0.8f,0.8f,0.5f));
    }

    public void clearActiveObjects(){
        for(GameObject obj: activeGameObjects){
            obj.getComponent(SpriteRenderer.class).setColour(new Vector4f(1,1,1,1));
        }
        activeGameObjects.clear();
    }

    public void clearObject(GameObject go){
        go.getComponent(SpriteRenderer.class).setColour(new Vector4f(1,1,1,1));
        activeGameObjects.remove(go);
    }

    public boolean isActive(GameObject go){
        return activeGameObjects.contains(go);
    }
}