package components;

import Renderer.Texture;
import contra.Component;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class SpriteRenderer extends Component {

    public Vector4f colour;
    public Texture texture;
    public Vector2f[] texCoords;

    public SpriteRenderer(Vector4f color){
        colour = color;
        this.texture = null;
        this.texCoords = getTexCoords();
    }
    public SpriteRenderer(Texture texture){
        this.texture = texture;
        this.texCoords = getTexCoords();
        this.colour = new Vector4f(1.0f,1.0f,1.0f,1.0f);
    }

    @Override
    public void init() {
    }

    @Override
    public void update(float dt) {
    }

    private Vector2f[] getTexCoords() {
        class localTextureCoords{
            static Vector2f[] texCoords = {
                    new Vector2f(1, 1),
                    new Vector2f(1, 0),
                    new Vector2f(0, 0),
                    new Vector2f(0, 1)
            };
        }
        return localTextureCoords.texCoords;
    }
}