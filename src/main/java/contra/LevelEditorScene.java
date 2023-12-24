package contra;

import Renderer.Shader;
import Renderer.Texture;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.*;
import imgui.internal.ImGui;
import org.joml.Vector2f;
import org.joml.Vector3f;
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
    public LevelEditorScene() {}

    //load big resources in the init fn, avoid lag spike mid-play
    @Override
    public void loadResources(){
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.loadSpriteSheet("assets/images/animatedSprite.png",24,144,144,0);
        AssetPool.getTexture("assets/images/red.png");
        AssetPool.getTexture("assets/images/green.png");
    }
    @Override
    public void init(){
        loadResources();
        this.camera = new Camera(new Vector3f(-200, -300,0));
        if(levelLoaded){
            activeGameObject = gameObjects.get(0);
            return;
        }

        Sprite red = new Sprite();
        Sprite green = new Sprite().setTexture(AssetPool.getTexture("assets/images/green.png"));

        GameObject obj1 = new GameObject();
        obj1.setName("Object 1").setTransform(new Vector2f(0,0),new Vector2f(256,256));

        GameObject obj2 = new GameObject();
        obj2.setName("Green").setTransform(new Vector2f(300,0),
                new Vector2f(256,256));

        obj1.addComponent(new SpriteRenderer().setSprite(red));
        obj2.addComponent(new SpriteRenderer().setSprite(green));

        this.activeGameObject = obj1;

        this.addGameObjectToScene(obj1);
        this.addGameObjectToScene(obj2);
    }

    @Override
    public void update(float dt){
        //System.out.println("FPS " + (1.0f/dt));

        for(GameObject go: gameObjects){
            go.update(dt); //update all objects in scene but don't call imgui for all of them
                          //It is only called for the active object
        }

        this.renderer.render();
    }

    @Override
    public void imGui(){
        ImGui.begin("Level Editor(Camera)");
        Vector3f pos = camera.position;
        float[] posFloat3f = {pos.x, pos.y, pos.z};
        if(imgui.ImGui.dragFloat3("Position" + ": ", posFloat3f)){
            pos.set(posFloat3f[0],posFloat3f[1],posFloat3f[2]);
        }

        Vector3f cameraFront = camera.cameraFront;
        float[] frontFloat3f = {cameraFront.x, cameraFront.y, cameraFront.z};
        if(imgui.ImGui.dragFloat3("Front" + ": ", frontFloat3f)){
            cameraFront.set(frontFloat3f[0],frontFloat3f[1],frontFloat3f[2]);
        }

        Vector3f cameraUp = camera.cameraUp;
        float[] upFloat3f = {cameraUp.x, cameraUp.y, cameraUp.z};
        if(imgui.ImGui.dragFloat3("Up" + ": ", upFloat3f)){
            cameraUp.set(upFloat3f[0],upFloat3f[1],upFloat3f[2]);
        }

        ImGui.end();
    }
}