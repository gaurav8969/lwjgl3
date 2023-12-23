package contra;

import Renderer.Renderer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imgui.internal.ImGui;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public  abstract class Scene {
    protected Renderer renderer = new Renderer();
    protected Camera camera;
    boolean isRunning = false;
    protected List<GameObject> gameObjects = new ArrayList<>();
    protected GameObject activeGameObject = null;
    protected boolean levelLoaded = false; //if the level has been loaded from a json
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

    public void saveExit(){
        Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                .registerTypeAdapter(Component.class,new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class,new GameObjectDeserializer())
                .create();

        try{
            FileWriter writer = new FileWriter("level.txt");
            writer.write(gson.toJson(this.gameObjects));
            writer.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void load(){
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .create();

        String inputFile = "";
        try{
            inputFile = new String(Files.readAllBytes(Paths.get("level.txt")));
        }catch(IOException e){
            e.printStackTrace();
        }

        if(!inputFile.equals("")){
            GameObject[] objs = gson.fromJson(inputFile, GameObject[].class);
            for(GameObject go: objs){
                this.gameObjects.add(go);
            }
            this.levelLoaded = true;
        }
    }
    public void imGui(){}
    public void loadResources(){}//override this and always load resources in it before you start using them in a scene

    public void sceneImgui(){
        if (activeGameObject != null){
            ImGui.begin("Inspector");
            activeGameObject.imGui(); //coupling introduced, caller of go.imGui() must sandwich it btw imgui window calls
            ImGui.end();
        }

        imGui();
    }
    public void addGameObjectToScene(GameObject go){
        if(isRunning){
            gameObjects.add(go);
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