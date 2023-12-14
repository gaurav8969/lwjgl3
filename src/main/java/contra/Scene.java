package contra;

import Renderer.Renderer;

import java.util.ArrayList;
import java.util.List;

public  abstract class Scene {
    protected Renderer renderer = new Renderer();
    protected Camera camera;
    boolean isRunning = false;
    protected List<GameObject> gameObjects = new ArrayList<>();
    public Scene(){

    }

    public abstract void init();

    public void start(){
        for(GameObject go: gameObjects){
            go.init();
            this.renderer.add(go);
        }
        isRunning = true;
    }
    public abstract void update(float dt);

    public void addGameObjectToScene(GameObject go){
        if(isRunning){
            gameObjects.add(go);
            /*natural to init and render an obj when and only when its added to a running scene, also the procedure
            is same for all scenes so method is implemented in the interface class to be present in every scene*/
            go.init();
            this.renderer.add(go);
        }else{
            gameObjects.add(go);
        }
    }
    public Camera camera() {
        return this.camera;
    }
}