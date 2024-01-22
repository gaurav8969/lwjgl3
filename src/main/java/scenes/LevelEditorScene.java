package scenes;

import Renderer.DebugDraw;
import components.Sprite;
import components.SpriteRenderer;
import components.Spritesheet;
import editor.Gridlines;
import editor.MouseControls;
import contra.*;
import editor.EditorCamera;
import imgui.ImVec2;
import imgui.internal.ImGui;
import org.joml.Vector2f;
import org.joml.Vector3f;
import scenes.Scene;
import util.AssetPool;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene {
    private GameObject editorContext;
    private Spritesheet sprites;
    public LevelEditorScene() {}

    //load big resources in the init fn, avoid lag spike mid-play
    @Override
    public void loadResources(){
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.loadSpriteSheet("assets/images/blocks.png",84,16,16,0);
        AssetPool.loadSpriteSheet("assets/images/gizmos.png", 2, 24, 48,0);

        //scrape the deserialized texture duplicates with old ids and replace with latest ones
        for(GameObject go: gameObjects){
            if(go.getComponent(SpriteRenderer.class).getTexture() != null){
                SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
                //texID is transient so textures won't work without this step
                spr.setTexture(AssetPool.getTexture(spr.getTexture().filepath));
            }
        }
    }

    @Override
    public void init(){
        loadResources();
        sprites = AssetPool.loadSpriteSheet("assets/images/blocks.png",84,16,16,0);
        this.camera = new Camera(new Vector2f(0, 0));

        editorContext = new GameObject();
        Gridlines gridInstance = new Gridlines();
        MouseControls mouseControls = new MouseControls(gridInstance);
        EditorCamera editorCamera = new EditorCamera(camera);

        editorContext.addComponent(gridInstance).addComponent(mouseControls).addComponent(editorCamera);

        if(levelLoaded){
            return;
        }
    }

    @Override
    public void update(float dt){
       //System.out.println("FPS " + (1.0/dt));
        editorContext.update(dt);
        for(GameObject go: gameObjects){
            go.update(dt); //update all objects in scene but don't call imgui for all of them
                          //It is only called for the active object
        }
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
            float spriteWidth = 4*sprite.getWidth();
            float spriteHeight = 4*sprite.getHeight();
            int id = sprite.texID();
            Vector2f[] texCoords = sprite.getTexCoords();

            ImGui.pushID(i);
            //imgui needs tex coords top left to bottom right
            if(ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y,
                    texCoords[0].x, texCoords[2].y)){
                GameObject go = Prefabs.generateSpriteObject(sprite,64, 64);
                editorContext.getComponent(MouseControls.class).pickUp(go);
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

        if (ImGui.checkbox("Grid", true)) {editorContext.getComponent(Gridlines.class).toggleGrid();}

        ImGui.end();
    }
}