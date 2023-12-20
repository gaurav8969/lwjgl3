package contra;

import Renderer.Shader;
import Renderer.Texture;
import components.Sprite;
import components.SpriteRenderer;
import components.Spritesheet;
import components.Transform;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import util.AssetPool;
import util.Time;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene{
    public LevelEditorScene() {

    }

    //load big resources in the init fn, avoid lag spike mid-play
    private void loadResources(){
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.loadSpriteSheet("assets/images/animatedSprite.png",24,144,144,0);
    }
    @Override
    public void init(){
        loadResources();
        this.camera = new Camera(new Vector2f(-200, -300));
        Spritesheet spritesheet = AssetPool.getSpriteSheet("assets/images/animatedSprite.png");
        Sprite red = new Sprite(AssetPool.getTexture("assets/images/red.png"));
        Sprite green = new Sprite(AssetPool.getTexture("assets/images/green.png"));

        GameObject obj1 = new GameObject("Object 1", new Transform(new Vector2f(0,0),new
                Vector2f(256,256)));
        GameObject obj2 = new GameObject("Object 2", new Transform(new Vector2f(0,0),
                new Vector2f(256,256)));

        obj1.addComponent(new SpriteRenderer(spritesheet.getSprite(1,1)));
        obj2.addComponent(new SpriteRenderer(spritesheet.getSprite(2,4)));

        this.addGameObjectToScene(obj1);
        this.addGameObjectToScene(obj2);
    }

    @Override
    public void update(float dt){
        //System.out.println("FPS " + (1.0f/dt));

        for(GameObject go: gameObjects){
            go.update(dt);
        }

        this.renderer.render();
    }
}
