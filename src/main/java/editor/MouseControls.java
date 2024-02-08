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
    protected GameObject holdingObject = null;
    private float spriteWidth, spriteHeight;
    private Gridlines gridInstance;
    private PickingTexture pickingTexture;
    private PropertiesWindow propertiesWindow;

    public MouseControls(Gridlines gridInstance){
        this.gridInstance = gridInstance;
        this.pickingTexture = Window.getImGuilayer().getPropertiesWindow().getPickingTexture();
        this.propertiesWindow = Window.getImGuilayer().getPropertiesWindow();
    }

    @Override
    public void editorUpdate(float dt){
        if(holdingObject != null) {
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
                drop();
                propertiesWindow.setActiveGameObject(null);
            }

            if(KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT)
                && !placeOccupied()){
                GameObject duplicate = holdingObject.copy();
                if(!propertiesWindow.getActiveObjects().isEmpty()){
                    duplicate.getComponent(SpriteRenderer.class).setColour(propertiesWindow.getActiveColours().get(0));
                }
                propertiesWindow.setActiveGameObject(duplicate);
                drop();
                pickUp(duplicate);
                return;
            }

            if(MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT) && !placeOccupied()){
                drop();
            }
        }else{
            if(MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT)){
                Vector2f dragStart = MouseListener.dragStart();
                Vector2f drag = MouseListener.getDrag();

                Vector2f centre = new Vector2f(dragStart).add(drag.mul(0.5f, new Vector2f()));

                Window.getScene().debugDraw().addBox2D(centre,new Vector2f(Math.abs(drag.x), Math.abs(drag.y)),0,1);
                List<GameObject> gameObjects = Window.getScene().getGameObjects();
                for(GameObject go: gameObjects){
                    if(objectInRect(centre,new Vector2f(Math.abs(drag.x), Math.abs(drag.y)),go)
                            && go.getComponent(Unpickable.class) == null){
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
        //line below switches focus to properties tab, a bit annoying but expected behaviour
        //propertiesWindow.setActiveGameObject(go);
        Window.getScene().addGameObjectToScene(go);
    }

    public void scoop(GameObject go){
        holdingObject = go;
        spriteWidth = holdingObject.tf.scale.x;
        spriteHeight = holdingObject.tf.scale.y;
    }

    public void drop(){
        this.holdingObject = null;
    }

    @Override
    public void imGui(){}

    //check if there is another object present where holding object is hovering
    private boolean placeOccupied(){
        List<GameObject> gameObjects = Window.getScene().getGameObjects();
        for(GameObject go:gameObjects) {
            if(go == holdingObject || go.getComponent(Unpickable.class) != null) {
                continue;
            }

            Vector2f objCentre = go.tf.position;
            Vector2f min = new Vector2f(objCentre).sub(new Vector2f(go.tf.scale).mul(0.5f));
            Vector2f max = new Vector2f(objCentre).add(new Vector2f(go.tf.scale).mul(0.5f));

            Vector2f[] vertices = {
                    new Vector2f(min.x, min.y), new Vector2f(min.x, max.y),
                    new Vector2f(max.x, max.y), new Vector2f(max.x, min.y)
            };

            if(objCentre.equals(holdingObject.tf.position))return true;

            for(Vector2f vertex:vertices){
                if(JMath.pointInRect(holdingObject.tf.position, holdingObject.tf.scale,vertex)){
                    return true;
                }
            }
        }
        return false;
    }

    //used for selecting multiple objects
    private boolean objectInRect(Vector2f centre, Vector2f dimensions, GameObject go){
        //readPixel is in screen coords, the rest in world coords
        int id = go.getID();
        Vector2f objCentre = go.tf.position;
        Vector2f min = new Vector2f(objCentre).sub(new Vector2f(go.tf.scale).mul(0.5f));
        Vector2f max = new Vector2f(objCentre).add(new Vector2f(go.tf.scale).mul(0.5f));

        Vector2f[] vertices = {
                new Vector2f(min.x, min.y), new Vector2f(min.x, max.y),
                new Vector2f(max.x, max.y), new Vector2f(max.x, min.y)
        };

        //if any of the vertices or the centre are in the rect
        boolean hovering = false;
        for(int i = 0; i < 4; i++){
            hovering = JMath.pointInRect(centre,dimensions, vertices[i]);
            if(hovering)break;
        }
        hovering = hovering || JMath.pointInRect(centre,dimensions,objCentre);

        //if any of the vertices or the centre are not covered by another obj
        boolean onTop = false;
        for(int i = 0; i < 4; i++){
            Vector2f screenCoords = MouseListener.worldToScreen(vertices[i]);
            onTop = (id == pickingTexture.readPixel((int)screenCoords.x, (int)screenCoords.y));
            if(onTop)break;
        }
        Vector2f centerScreenCoords = MouseListener.worldToScreen(objCentre);
        onTop = onTop || (id == pickingTexture.readPixel((int)centerScreenCoords.x, (int)centerScreenCoords.y));

        return onTop && hovering;
    }
}