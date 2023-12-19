package components;

import Renderer.Texture;
import contra.Component;
import org.joml.Vector2f;
import org.joml.Vector4f;

//contains sprites that are to be rendered
public class SpriteRenderer extends Component {

    public Vector4f colour;
    public Sprite sprite;
    public SpriteRenderer(Vector4f colour, Sprite sprite){
        this.sprite = sprite;
        this.colour = colour;
    }

    public SpriteRenderer(Vector4f colour){
        this.colour = colour;
        this.sprite = new Sprite(null);
    }

    public SpriteRenderer(Sprite sprite){
        this.colour = new Vector4f(1.0f,1.0f,1.0f,1.0f);
        this.sprite = sprite;
    }

    public Texture getTexture(){
        return sprite.texture;
    }

    public Vector2f[] getTextureCoords(){
        return sprite.texCoords;
    }

    @Override
    public void init() {
    }

    @Override
    public void update(float dt) {
    }

}