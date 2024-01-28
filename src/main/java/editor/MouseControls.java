package editor;

import components.Component;
import components.SpriteRenderer;
import contra.GameObject;
import contra.MouseListener;
import contra.Window;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

//class for managing level editor mouse magic
//Not part of any specific game objects
public class MouseControls extends Component {
    private GameObject holdingObject = null;
    private float spriteWidth, spriteHeight;
    private Gridlines gridInstance;

    public MouseControls(Gridlines gridInstance){
        this.gridInstance = gridInstance;
    }

    @Override
    public void editorUpdate(float dt){
        if(holdingObject != null) {
            Vector2f orthoPos = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
            Vector2f gridPos = gridInstance.getGridPos(orthoPos);
            if(gridInstance.shouldDraw()){
                holdingObject.setPosition(gridPos);
            }else{
                holdingObject.setPosition(orthoPos);
            }

            if(MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)){
                drop();
            };
        }
    }

    public void pickUp(GameObject go){
        holdingObject = go;
        spriteWidth = holdingObject.getComponent(SpriteRenderer.class).getSprite().getWidth();
        spriteHeight = holdingObject.getComponent(SpriteRenderer.class).getSprite().getHeight();
        Window.getScene().addGameObjectToScene(go);
    }

    public void drop(){
        this.holdingObject = null;
    }

    @Override
    public void imGui(){}

}

