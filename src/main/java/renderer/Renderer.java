package renderer;

//renders and manages groups of batches, batches are an implementation detail, it is the renderer that
//acts as a simpleton manager and cranks out the renders in the end

import components.SpriteRenderer;
import contra.GameObject;
import util.AssetPool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Renderer {
    private final int MAX_BATCH_SIZE = 1000;
    private final int MAX_TEXTURES_SIZE = 7;//8 slots, 7 textures, the first default-activated is for no textures
    private List<RenderBatch> batches;
    private static Shader currentShader = AssetPool.getShader("assets/shaders/pickingShader.glsl");

    public Renderer(){
        this.batches = new ArrayList<>();
    }
    public  void add(GameObject go){
        SpriteRenderer sprite = go.getComponent(SpriteRenderer.class);
        if(sprite != null) {
            addSprite(sprite);
        }
    }

    public void addSprite(SpriteRenderer sprite){
        boolean added = false;
        for(RenderBatch batch: batches){
            if(batch.hasRoom && batch.zIndex() == sprite.zIndex()){
                Texture tex = sprite.getTexture();
                if(tex == null || batch.hasTexture(tex)|| batch.hasTextureRoom){
                    batch.addSprite(sprite);
                    added = true;
                    break;
                }
            }
        }

        if(!added){
            RenderBatch newRenderBatch = new RenderBatch(MAX_BATCH_SIZE,MAX_TEXTURES_SIZE, sprite.zIndex());
            newRenderBatch.init();
            batches.add(newRenderBatch);
            newRenderBatch.addSprite(sprite);
            Collections.sort(batches);
        }
    }

    public void render(){
        currentShader.use();
        for(RenderBatch batch: batches){
            batch.render();
        }
    }

    public void destroyGameObject(GameObject go){
        if(go.getComponent(SpriteRenderer.class) == null) return;
        for(RenderBatch batch: batches){
            if(batch.destroyIfExists(go)){
                return;
            }
        }
    }

    public static void bindShader(Shader shader){
        currentShader = shader;
    }

    public static Shader getBoundShader(){
        return currentShader;
    }
}