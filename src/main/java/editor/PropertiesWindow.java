package editor;

import components.BreakableBrick;
import components.Ground;
import components.SpriteRenderer;
import contra.KeyListener;
import contra.Window;
import org.joml.Vector2f;
import org.joml.Vector4f;
import physics2D.components.Box2DCollider;
import physics2D.components.CircleCollider;
import physics2D.components.RigidBody2D;
import physics2D.enums.BodyType;
import renderer.PickingTexture;
import contra.GameObject;
import contra.MouseListener;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImVec2;
import scenes.Scene;
import util.Settings;

import java.io.LineNumberReader;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DELETE;

public class PropertiesWindow {
    private PickingTexture pickingTexture;
    private int entityID;
    private ImVec2 topLeft;
    private ImVec2 windowSize;
    private List<GameObject> activeGameObjects;
    private List<Vector4f> activeColours; //original colours of the selected objects

    public PropertiesWindow(PickingTexture pickingTexture){
        this.pickingTexture = pickingTexture;
        topLeft = new ImVec2();
        windowSize = new ImVec2();
        this.activeGameObjects = new ArrayList<>();
        this.activeColours = new ArrayList<>();
    }

    public void editorUpdate(Scene currentScene, float dt){
            //separation of multi select and single object select features
            if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && GameViewWindow.isFocused()
                    && !MouseListener.isDragging()){
                int x = (int)MouseListener.getScreenX();
                int y = (int)MouseListener.getScreenY();
                entityID = pickingTexture.readPixel(x, y);
                GameObject go = currentScene.getGameObject(entityID);

                if(go == null){
                    clearActiveObjects();
                    return;
                }

                if(!activeGameObjects.contains(go) && go.getComponent(Unpickable.class) == null){
                    clearActiveObjects();
                    addActiveGameObject(go);
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

                if(ImGui.menuItem("Add Ground Component")){
                    if(activeGameObject.getComponent(Ground.class) == null){
                        activeGameObject.addComponent(new Ground());
                    }

                    if(activeGameObject.getComponent(Box2DCollider.class) == null &&
                            activeGameObject.getComponent(CircleCollider.class) == null){
                        activeGameObject.addComponent(new Box2DCollider()
                                .setHalfSize(new Vector2f(activeGameObject.tf.scale).mul(0.5f)));
                    }

                        if(activeGameObject.getComponent(RigidBody2D.class) == null){
                            activeGameObject.addComponent(new RigidBody2D());
                            activeGameObject.getComponent(RigidBody2D.class).setBodyType(BodyType.Static);
                        }

                }

                if(ImGui.menuItem("Add Breakable component")){
                    if(activeGameObject.getComponent(BreakableBrick.class) == null){
                        activeGameObject.addComponent(new BreakableBrick());
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
            clearActiveObjects();
            if(go != null) {
                addActiveGameObject(go);
            }
    }

    public PickingTexture getPickingTexture(){return pickingTexture;}

    public void addActiveGameObject(GameObject go){
        if(activeGameObjects.contains(go) || go == null){return;}
        SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
        if(spr != null){
            Vector4f sprCol = spr.getColour();
            activeColours.add(new Vector4f(sprCol));
            sprCol.w = 0.5f;
            spr.setDirty();
        }
        activeGameObjects.add(go);
    }

    public void clearActiveObjects(){
        int size = activeGameObjects.size();
        for(int i = 0; i < size; i++){
            GameObject obj = activeGameObjects.get(i);
            SpriteRenderer spr = obj.getComponent(SpriteRenderer.class);
            if(spr != null){
                spr.setColour(activeColours.get(i));
            }
        }
        activeGameObjects.clear();
        activeColours.clear();
    }

    public void clearObject(GameObject go){
        int index = activeGameObjects.indexOf(go);
        go.getComponent(SpriteRenderer.class).setColour(activeColours.get(index));
        activeGameObjects.remove(go);
        activeColours.remove(index);
    }

    public boolean isActive(GameObject go){
        return activeGameObjects.contains(go);
    }

    public List<GameObject> getActiveObjects(){
        return activeGameObjects;
    }

    public List<Vector4f> getActiveColours(){return activeColours;}
}