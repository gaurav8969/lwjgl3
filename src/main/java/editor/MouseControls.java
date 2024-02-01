package editor;

import components.Component;
import components.SpriteRenderer;
import components.Transform;
import contra.GameObject;
import contra.KeyListener;
import contra.MouseListener;
import contra.Window;
import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector4f;
import renderer.DebugDraw;
import renderer.PickingTexture;
import util.JMath;

import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

//class for managing level editor mouse magic
//Not part of any specific game objects
public class MouseControls extends Component {
    private GameObject holdingObject = null;
    private float spriteWidth, spriteHeight;
    private Gridlines gridInstance;
    private PickingTexture pickingTexture;
    private PropertiesWindow propertiesWindow;

    //so we don't update too often

    public MouseControls(Gridlines gridInstance){
        this.gridInstance = gridInstance;
        this.pickingTexture = Window.getImGuilayer().getPropertiesWindow().getPickingTexture();
        this.propertiesWindow = Window.getImGuilayer().getPropertiesWindow();
    }

    @Override
    public void editorUpdate(float dt){
        if(holdingObject != null) {
            Window.getImGuilayer().getPropertiesWindow().setActiveGameObject(null);
            Vector2f orthoPos = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
            Vector2f gridPos = gridInstance.getGridPos(orthoPos);
            gridPos.x += spriteWidth/2f;
            gridPos.y += spriteHeight/2f;
            if(gridInstance.shouldDraw()){
                holdingObject.setPosition(gridPos);
            }else{
                holdingObject.setPosition(orthoPos);
            }

            if(KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)){
                holdingObject.destroy();
                holdingObject = null;
            }

            if(KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT)){
                GameObject duplicate = holdingObject.copy();
                drop();
                pickUp(duplicate);
                removeDuplicates();
                return;
            }

            if(MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT)){
                drop();
            }
        }else{
            if(MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT)){
                Vector2f dragStart = MouseListener.dragStart();
                Vector2f drag = MouseListener.getDrag();

                Vector2f centre = new Vector2f(dragStart).add(drag.mul(0.5f, new Vector2f()));

                DebugDraw.addBox2D(centre,new Vector2f(Math.abs(drag.x), Math.abs(drag.y)),0,1);
                List<GameObject> gameObjects = Window.getScene().getGameObjects();
                for(GameObject go: gameObjects){
                    if(objectInRect(centre,new Vector2f(Math.abs(drag.x), Math.abs(drag.y)),go)){
                        propertiesWindow.addActiveGameObject(go);
                    }else if(propertiesWindow.isActive(go)){
                        propertiesWindow.clearObject(go);
                    }
                }
            }
        }
    }

    public void pickUp(GameObject go){
        scoop(go);
        Window.getScene().addGameObjectToScene(go);
    }

    public void scoop(GameObject go){
        holdingObject = go;
        spriteWidth = holdingObject.tf.scale.x;
        spriteHeight = holdingObject.tf.scale.y;
        SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
        spr.setColour(new Vector4f(.8f,.8f,.8f,0.5f));
    }

    public void drop(){
        SpriteRenderer spr = holdingObject.getComponent(SpriteRenderer.class);
        spr.setColour(new Vector4f(1f,1f,1f,1f));
        this.holdingObject = null;
    }

    @Override
    public void imGui(){}
    //purge duplicates created during dragging
    private void removeDuplicates(){
        List<GameObject> gameObjects = Window.getScene().getGameObjects();
        int last = gameObjects.size() - 1;

        //only check last three elements because only one copy can be created per frame
        GameObject endObj = gameObjects.get(last);
        GameObject oneBefore = gameObjects.get(last - 1);
        GameObject twoBefore = gameObjects.get(last - 2);


        if(endObj.tf.equals(oneBefore.tf) && oneBefore.tf.equals(twoBefore.tf)){
            oneBefore.destroy();
        }
    }

    //used for selecting multiple objects
    private boolean objectInRect(Vector2f centre, Vector2f dimensions, GameObject go){
        //readPixel is in screen coords, the rest in world coords
        int id = go.getID();
        Vector2f pos = go.tf.position;
        Vector2f screenCoords = MouseListener.worldToScreen(pos);
        boolean hovering = JMath.pointInRect(centre,dimensions, pos);
        boolean onTop = (id == pickingTexture.readPixel((int)screenCoords.x, (int)screenCoords.y));
        return hovering && onTop;
    }
}