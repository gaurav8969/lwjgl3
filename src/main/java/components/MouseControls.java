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
            float orthoX = MouseListener.getOrthoX() - spriteWidth;
            float orthoY = MouseListener.getOrthoY();
            holdingObject.setPosition(new Vector2f(orthoX, orthoY));

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
