package contra;

import components.Sprite;
import components.SpriteRenderer;
import components.Transform;
import org.joml.Vector2f;

public class Prefabs {
    public static GameObject generateSpriteObject(Sprite sprite, float width, float height){
        GameObject go = new GameObject();
        go.setTransform(new Vector2f(),new Vector2f(width,height)).setName("Sprite_Object_Gen_" + go.getID());
        go.addComponent(new SpriteRenderer().setSprite(sprite)); //z-index is 0
        return go;
    }
}