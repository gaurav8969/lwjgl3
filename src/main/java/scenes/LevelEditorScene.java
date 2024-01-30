package scenes;

import components.Sprite;
import components.SpriteRenderer;
import components.Spritesheet;
import components.StateMachine;
import editor.*;
import contra.*;
import imgui.ImVec2;
import imgui.internal.ImGui;
import org.joml.Vector2f;
import util.AssetPool;

import java.io.File;
import java.util.Collection;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_P;
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
        AssetPool.loadSpriteSheet("assets/images/characterSprites.png",
                26, 16, 16, 0);
        AssetPool.loadSpriteSheet("assets/images/items.png", 43,
                16, 16, 0);

        //scrape the deserialized texture duplicates with old ids and replace with latest ones
        for(GameObject go: scene.getGameObjects()){
            if(go.getComponent(SpriteRenderer.class).getTexture() != null){
                SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
                //texID is transient so textures won't work without this step
                spr.setTexture(AssetPool.getTexture(spr.getTexture().filepath));
            }

            if (go.getComponent(StateMachine.class) != null) {
                StateMachine stateMachine = go.getComponent(StateMachine.class);
                stateMachine.refreshTextures();
            }
        }

        //sounds
        AssetPool.addSound("assets/sounds/main-theme-overworld.ogg", true);
        AssetPool.addSound("assets/sounds/flagpole.ogg", false);
        AssetPool.addSound("assets/sounds/break_block.ogg", false);
        AssetPool.addSound("assets/sounds/bump.ogg", false);
        AssetPool.addSound("assets/sounds/coin.ogg", false);
        AssetPool.addSound("assets/sounds/gameover.ogg", false);
        AssetPool.addSound("assets/sounds/jump-small.ogg", false);
        AssetPool.addSound("assets/sounds/mario_die.ogg", false);
        AssetPool.addSound("assets/sounds/pipe.ogg", false);
        AssetPool.addSound("assets/sounds/powerup.ogg", false);
        AssetPool.addSound("assets/sounds/powerup_appears.ogg", false);
        AssetPool.addSound("assets/sounds/stage_clear.ogg", false);
        AssetPool.addSound("assets/sounds/stomp.ogg", false);
        AssetPool.addSound("assets/sounds/kick.ogg", false);
        AssetPool.addSound("assets/sounds/invincible.ogg", false);
    }

    @Override
    public void init(Scene scene){
        sprites = AssetPool.loadSpriteSheet("assets/images/blocks.png",84,16,16,0);

        editorContext = scene.createGameObject("Editor Context");
        editorContext.makeUnserializable();
        Gridlines gridInstance = new Gridlines();
        MouseControls mouseControls = new MouseControls(gridInstance);
        EditorCamera editorCamera = new EditorCamera(scene.camera());
        GizmoSystem gizmo = new GizmoSystem();
        editorContext.addComponent(gridInstance).addComponent(mouseControls).addComponent(editorCamera).addComponent(gizmo);
        scene.addGameObjectToScene(editorContext);//the scene updates it
    }

    @Override
    public void update(float dt){
        GameObject go = Window.getImGuilayer().getPropertiesWindow().getActiveGameObject();
        GizmoSystem gizmoSystem = editorContext.getComponent(GizmoSystem.class);

        if(go != null && !gizmoSystem.isGizmo(go)){
            if(KeyListener.isKeyPressed(GLFW_KEY_P)){
                MouseControls mouseControls = editorContext.getComponent(MouseControls.class);
                mouseControls.scoop(go);
            }
        }
    }
    @Override
    public void imGui(){
        ImGui.begin("Level Editor");
        editorContext.imGui();
        if(ImGui.beginTabBar("WindowTabBar")) {
            ImVec2 windowPos = new ImVec2();
            ImGui.getWindowPos(windowPos);
            ImVec2 windowSize = new ImVec2();
            ImGui.getWindowSize(windowSize);
            float windowX2 = windowPos.x + windowSize.x;
            ImVec2 itemSpacing = new ImVec2();

            if(ImGui.beginTabItem("Block")) {
                //method of imgui style class for horizontal and vertical spacing between widgets/lines.
                ImGui.getStyle().getItemSpacing(itemSpacing);

                for (int i = 0; i < sprites.size(); i++) {
                    Sprite sprite = sprites.getSprite(i);
                    float spriteWidth = 4 * sprite.getWidth();
                    float spriteHeight = 4 * sprite.getHeight();
                    int id = sprite.texID();
                    Vector2f[] texCoords = sprite.getTexCoords();

                    ImGui.pushID(i);
                    //imgui needs tex coords top left to bottom right
                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y,
                            texCoords[0].x, texCoords[2].y)) {
                        GameObject go = Prefabs.generateSpriteObject(sprite, 0.25f, 0.25f);
                        editorContext.getComponent(MouseControls.class).pickUp(go);
                    }
                    ImGui.popID();

                    ImVec2 lastButtonPos = new ImVec2();
                    ImGui.getItemRectMax(lastButtonPos);
                    float lastButtonX2 = lastButtonPos.x;
                    float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
                    if (i + 1 < sprites.size() && nextButtonX2 < windowX2) {
                        ImGui.sameLine();
                    }
                }
                ImGui.endTabItem(); //keep it inside if clause so it doesn't run when tab isn't open
            }

            if(ImGui.beginTabItem("Prefabs")){
                Spritesheet playerSprites = AssetPool.getSpriteSheet("assets/images/characterSprites.png");
                Sprite sprite = playerSprites.getSprite(0);
                float spriteWidth = sprite.getWidth() * 4;
                float spriteHeight = sprite.getHeight() * 4;
                int id = sprite.texID();
                Vector2f[] texCoords = sprite.getTexCoords();

                if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                    GameObject object = Prefabs.generateMario();
                    editorContext.getComponent(MouseControls.class).pickUp(object);
                }
                ImGui.sameLine();

                Spritesheet items = AssetPool.getSpriteSheet("assets/images/items.png");
                sprite = items.getSprite(0);
                id = sprite.texID();
                texCoords = sprite.getTexCoords();
                if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                    GameObject object = Prefabs.generateQuestionBlock();
                    editorContext.getComponent(MouseControls.class).pickUp(object);
                }

                ImGui.endTabItem();
            }

            if (ImGui.beginTabItem("Sounds")) {
                Collection<Sound> sounds = AssetPool.getAllSounds();
                for (Sound sound : sounds) {
                    File tmp = new File(sound.getFilepath());
                    if (ImGui.button(tmp.getName())) {
                        if (!sound.isPlaying()) {
                            sound.play();
                        } else {
                            sound.stop();
                        }
                    }

                    ImVec2 lastButtonPos = new ImVec2();
                    ImGui.getItemRectMax(lastButtonPos);
                    float lastButtonX2 = lastButtonPos.x;
                    float nextButtonX2 = lastButtonX2 + itemSpacing.x;
                    if (nextButtonX2 < windowX2 - 100) {
                        ImGui.sameLine();
                    }
                }
                ImGui.endTabItem();
            }
            ImGui.endTabBar();
        }
        ImGui.end();
    }
}