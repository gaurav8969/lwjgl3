package scenes;

import Renderer.DebugDraw;
import components.*;
import contra.*;
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
    private Spritesheet sprites;
    public LevelEditorScene() {}

    //load big resources in the init fn, avoid lag spike mid-play
    @Override
    public void loadResources(){
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.loadSpriteSheet("assets/images/blocks.png",84,16,16,0);
        AssetPool.getTexture("assets/images/red.png");
        AssetPool.getTexture("assets/images/green.png");

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
        this.mouseControls = new MouseControls();
        this.camera = new Camera(new Vector2f(0, 0));
        sprites = AssetPool.loadSpriteSheet("assets/images/blocks.png",84,16,16,0);
        if(levelLoaded){
            if(!gameObjects.isEmpty()) {
                activeGameObject = gameObjects.get(0);
            }
            return;
        }
    }

    @Override
    public void update(float dt){
       //System.out.println("FPS " + (1.0/dt));
        this.mouseControls.update();
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
                this.mouseControls.pickUp(go);
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

        Vector2f cameraPos = camera.position();

        float[] imFloat2f = {cameraPos.x, cameraPos.y};
        if(imgui.ImGui.dragFloat2("Camera Pos" + ": ", imFloat2f)){
            cameraPos.set(imFloat2f[0],imFloat2f[1]);
        }

        if (ImGui.checkbox("Grid", true))
        {
            gridlines.toggleGrid();
        }

        ImGui.end();
    }
}