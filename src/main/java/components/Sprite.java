package components;

import Renderer.Texture;
import contra.Component;
import org.joml.Vector2f;

public class Sprite{

    private Texture texture;
    private Vector2f[] texCoords = loadTexCoords();;

    private Vector2f[] loadTexCoords() {
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

    public Texture getTexture(){
        return this.texture;
    }

    public Vector2f[] getTexCoords(){
        return this.texCoords;
    }

    public Sprite setTexture(Texture texture){
        this.texture =  texture;
        return this;
    }

    public Sprite setTexCoords(Vector2f[] texCoords){
        this.texCoords = texCoords;
        return this;
    }
}