package contra;

import Renderer.Shader;
import Renderer.Texture;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.*;
import imgui.ImVec2;
import imgui.internal.ImGui;
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
    private Spritesheet sprites;
    public LevelEditorScene() {}

    //load big resources in the init fn, avoid lag spike mid-play
    @Override
    public void loadResources(){
        AssetPool.getShader("assets/shaders/default.glsl");
        sprites = AssetPool.loadSpriteSheet("assets/images/animatedSprite.png",24,144,144,0);
        AssetPool.getTexture("assets/images/red.png");
        AssetPool.getTexture("assets/images/green.png");
    }
    @Override
    public void init(){
        loadResources();
        this.camera = new Camera(new Vector2f(0, 0));
        if(levelLoaded){
            activeGameObject = gameObjects.get(0);
           return;
        }

        Sprite red = new Sprite();
        Sprite green = new Sprite().setTexture(AssetPool.getTexture("assets/images/green.png"));

        GameObject obj1 = new GameObject();
        obj1.setName("Object 1").setTransform(new Vector2f(0,100),new Vector2f(64,64));

        GameObject obj2 = new GameObject();
        obj2.setName("Green").setTransform(new Vector2f(0,200),
                new Vector2f(64,64));

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
        ImGui.begin("Level Editor");

        ImVec2 windowPos = new ImVec2();
        ImGui.getWindowPos(windowPos);
        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowSize(windowSize);
        ImVec2 itemSpacing = new ImVec2();
        //method of imgui style class for horizontal and vertical spacing between widgets/lines.
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float windowX2 = windowPos.x + windowSize.x;
        for(int i = 0; i < sprites.size(); i++){
            Sprite sprite = sprites.getSprite(i);
            float spriteWidth = sprite.getWidth();
            float spriteHeight = sprite.getHeight();
            int id = sprite.texID();
            Vector2f[] texCoords = sprite.getTexCoords();

            ImGui.pushID(i);
            if(ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y,
                    texCoords[0].x, texCoords[2].y)){
                System.out.println("Button " + i + " clicked!" );
            }
            ImGui.popID();

            ImVec2 lastButtonPos = new ImVec2();
            ImGui.getItemRectMax(lastButtonPos);
            float lastButtonX2 = lastButtonPos.x;
            float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth ;
            if(i + 1 < sprites.size() && nextButtonX2 < windowX2 ){
                ImGui.sameLine();
            }
        }
        ImGui.end();
    }

}