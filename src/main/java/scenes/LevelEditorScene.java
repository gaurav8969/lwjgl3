package scenes;

import components.Sprite;
import components.SpriteRenderer;
import components.Spritesheet;
import editor.*;
import contra.*;
import imgui.ImVec2;
import imgui.internal.ImGui;
import org.joml.Vector2f;
import util.AssetPool;

import static org.lwjgl.opengl.GL13.glCompressedTexImage1D;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

//also initializes the scene, including the level editor resources needed
public class LevelEditorScene extends SceneInitializer {
    GameObject editorContext;
    private Spritesheet sprites;
    public LevelEditorScene() {}

    //load big resources in the init fn, avoid lag spike mid-play
    @Override
    public void loadResources(Scene scene){
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.loadSpriteSheet("assets/images/blocks.png",84,16,16,0);
        AssetPool.loadSpriteSheet("assets/images/gizmos.png", 3, 24, 48,0);

        //scrape the deserialized texture duplicates with old ids and replace with latest ones
        for(GameObject go: scene.getGameObjects()){
            if(go.getComponent(SpriteRenderer.class).getTexture() != null){
                SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
                //texID is transient so textures won't work without this step
                spr.setTexture(AssetPool.getTexture(spr.getTexture().filepath));
            }
        }
    }

    @Override
    public void init(Scene scene){
        sprites = AssetPool.loadSpriteSheet("assets/images/blocks.png",84,16,16,0);

        editorContext = new GameObject();
        editorContext.setName("Editor Context");
        editorContext.makeUnserializable();
        Gridlines gridInstance = new Gridlines();
        MouseControls mouseControls = new MouseControls(gridInstance);
        EditorCamera editorCamera = new EditorCamera(scene.camera());
        GizmoSystem gizmo = new GizmoSystem();
        editorContext.addComponent(gridInstance).addComponent(mouseControls).addComponent(editorCamera).addComponent(gizmo);
        scene.addGameObjectToScene(editorContext);//the scene updates it
    }

    @Override
    public void imGui(){
        ImGui.begin("Level Editor");

        editorContext.imGui();
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

        ImGui.end();
    }
}