package contra;

import components.Sprite;
import components.SpriteRenderer;
import components.Transform;
import org.joml.Vector2f;

public class Prefabs {
    public static GameObject generateSpriteObject(Sprite sprite, float width, float height){
        GameObject go = Window.getScene().createGameObject("Sprite_Object_Gen_" + GameObject.IDCounter);
        go.tf.scale.x = width;
        go.tf.scale.y = height;
        go.addComponent(new SpriteRenderer().setSprite(sprite)); //z-index is 0
        return go;
    }
}