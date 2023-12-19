package components;

import Renderer.Texture;
import contra.Component;
import org.joml.Vector2f;

public class Sprite extends Component {

    public Texture texture;
    public Vector2f[] texCoords;
    public Sprite(Texture texture){
        this.texture = texture;
        this.texCoords = getTexCoords();
    }

    public Sprite(Texture texture, Vector2f[] texCoords){
        this.texture = texture;
        this.texCoords = texCoords;
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