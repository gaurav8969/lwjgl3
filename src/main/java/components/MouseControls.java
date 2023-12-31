package components;

import contra.GameObject;
import contra.MouseListener;
import contra.Window;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

//class for managing level editor mouse magic
//Not part of any specific game objects
public class MouseControls{
    private GameObject holdingObject = null;
    private float spriteWidth, spriteHeight;

    public void update(){
        if(holdingObject != null) {
            Vector2f orthoPos = new Vector2f(MouseListener.getOrthoX() - spriteWidth, MouseListener.getOrthoY());
            Vector2f gridPos = Window.getScene().gridInstance().getGridPos(orthoPos);
            if(Window.getScene().gridInstance().shouldDraw()){
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

}
