package Renderer;

//renders and manages groups of batches, batches are an implementation detail, it is the renderer that
//acts as a simpleton manager and cranks out the renders in the end

import components.SpriteRenderer;
import contra.GameObject;

import java.util.ArrayList;
import java.util.List;

public class Renderer {
    private final int MAX_BATCH_SIZE = 1000;
    private List<RenderBatch> batches;

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
            if(batch.hasRoom){
                batch.addSprite(sprite);
                added = true;
                break;
            }
        }
        if(!added){
            RenderBatch newRenderBatch = new RenderBatch(MAX_BATCH_SIZE);
            newRenderBatch.init();
            batches.add(newRenderBatch);
            newRenderBatch.addSprite(sprite);
        }
    }

    public void render(){
        for(RenderBatch batch: batches){
            batch.render();
        }
    }
}