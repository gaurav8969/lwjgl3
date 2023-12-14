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

        int xOffset = 10;
        int yOffset = 10;

        float totalWidth = (float)(600 - xOffset * 2);
        float totalHeight = (float)(300 - yOffset * 2);
        float sizeX = totalWidth / 100.0f;
        float sizeY = totalHeight / 100.0f;
        float padding = 3;

        for (int x=0; x < 100; x++) {
            for (int y=0; y < 100; y++) {
                float xPos = xOffset + (x * sizeX) + (padding * x);
                float yPos = yOffset + (y * sizeY) + (padding * y);

                GameObject go = new GameObject("Obj" + x + "" + y, new Transform(new Vector2f(xPos, yPos), new Vector2f(sizeX, sizeY)));
                go.addComponent(new SpriteRenderer(new Vector4f(xPos / totalWidth, yPos / totalHeight, 1, 1)));
                this.addGameObjectToScene(go);
            }
        }
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
