package contra;

import Renderer.Shader;
import Renderer.Texture;
import components.SpriteRenderer;
import components.Transform;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import util.AssetPool;

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
    }
    @Override
    public void init(){
        this.camera = new Camera(new Vector2f(-200, -300));
        GameObject obj1 = new GameObject("Object 1", new Transform(new Vector2f(100,100),new
                Vector2f(256,256)));
        GameObject obj2 = new GameObject("Object 1", new Transform(new Vector2f(600,-200),
                new Vector2f(256,256)));

        obj1.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/images/ContraSheet1.png")));
        obj2.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/images/gojoRunning.png")));

        this.addGameObjectToScene(obj1);
        this.addGameObjectToScene(obj2);

        loadResources();
    }

    @Override
    public void update(float dt) {
        //System.out.println("FPS " + (1.0f/dt));

        for(GameObject go: gameObjects){
            go.update(dt);
        }

        this.renderer.render();
    }
}
