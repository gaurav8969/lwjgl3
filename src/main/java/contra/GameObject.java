package contra;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.*;
import imgui.ImGui;
import imgui.type.ImBoolean;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.AssetPool;
import util.JMath;

import java.util.stream.IntStream;

public class GameObject {
    private boolean serializable = true;

    private boolean isDead = false;

    //starts at zero every game load
    public static int IDCounter = 0;
    private int uniqueID = -1;
    public String name;
    private final int componentsSize = 32;
    //array instead of actual bitset since it causes serialization issues with gson
    public boolean[] componentsBitset = new boolean[componentsSize];
    public Component[] components = new Component[componentsSize];
    public transient Transform tf;
    public GameObject(){
        //counter starts at zero and gets incremented everytime constructor is called
        uniqueID = IDCounter++;
    }

    public void init(){
        for (Component c : components) {
            if (c != null) {
                c.init();
            }
        }
    }

    public void update(float dt){
        for(Component c: components){
            if(c != null) {
                c.update(dt);
            }
        }
    }

    public void editorUpdate(float dt){
        for(Component c: components){
            if(c != null) {
                c.editorUpdate(dt);
            }
        }
    }

    public GameObject copy(){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .enableComplexMapKeySerialization()
                .create();
        String objAsJson = gson.toJson(this);
        GameObject obj = gson.fromJson(objAsJson, GameObject.class);

        obj.setName("Sprite_Object_Gen_" + obj.getID());
        for(Component c: obj.components){
            if(c != null){
                c.generateID();
            }
        }

        StateMachine stateMachine = obj.getComponent(StateMachine.class);
        if(stateMachine != null){
            stateMachine.refreshTextures();
        }else {
            //texture id is transient, the texture is copied without id, so we update it manually
            SpriteRenderer sprite = obj.getComponent(SpriteRenderer.class);
            if (sprite != null && sprite.getTexture() != null) {
                sprite.setTexture(AssetPool.getTexture(sprite.getTexture().filepath));
            }
        }

        return obj;
    }

    public void destroy(){
        this.isDead = true;
        for(Component c: components){
            if(c != null){
                c.destroy();
            }
        }
    }

    public void imGui(){
        for(Component c: components){
            if(c != null) {
                String name = c.getClass().getSimpleName();
                //hacky way to get around a pesky bug
                if(name.equals("Transform") && !this.name.equals("Editor Context")){
                    ImGui.collapsingHeader("");
                }

                if (ImGui.collapsingHeader(c.getClass().getSimpleName())){
                    c.imGui();
                }
            }
        }
    }

    public GameObject addComponent(Component c){
        if(c != null) {
            c.generateID();
            short id = ComponentID.getUniqueID(c.getClass());
            componentsBitset[id] = true;
            components[id] = c;
            c.gameObject = this;
        }
        return this;
    }

    public <T extends Component> boolean hasComponent(Class<T> componentClass){
        short id = ComponentID.getUniqueID(componentClass);
        return componentsBitset[id];
    };

    public <T extends Component> T getComponent(Class<T> componentClass){
        short id = ComponentID.getUniqueID(componentClass);
        //noinspection unchecked
        return (T)components[id];
    }

    public <T extends Component> void removeComponent(Class<T> componentClass){
        for (int id=0; id < componentsSize; id++) {
            Component c = components[id];
            if (c != null && componentClass.isAssignableFrom(c.getClass())) {
                componentsBitset[id] = false;
                components[id] = null;
            }
        }
    }


    public int getID(){
        return uniqueID;
    }

    public void setMaxID(int maxID){
        IDCounter = maxID;
    }

    public GameObject setName(String string){
        this.name = string;
        return this;
    }

    public GameObject setTransform(Vector2f position, Vector2f scale){
        this.tf.position = position;
        this.tf.scale = scale;
        return this;
    }

    public GameObject setTransform(Transform tf){
        this.tf.position = tf.position;
        this.tf.scale = tf.scale;
        this.tf.rotation = tf.rotation;
        return this;
    }

    public GameObject setPosition(Vector2f position){
        this.tf.position = position;
        return this;
    }

    public GameObject setSize(Vector2f size){
        this.tf.scale = size;
        return this;
    }

    public GameObject setRotation(float rotation){
        this.tf.rotation = rotation;
        return this;
    }

    public Component[] getComponents(){
        return components;
    }

    public static void loadCounter(int maxID){
        IDCounter = maxID;
    }

    public boolean isSerializable(){
        return this.serializable;
    }

    public void makeSerializable(){
        this.serializable = true;
    }

    public void makeUnserializable(){
        this.serializable = false;
    }

    public boolean isDead() {
        return isDead;
    }

    //also works for rotated objects
    public boolean insideObject(float x, float y){
        Vector2f min = new Vector2f(tf.position).sub(new Vector2f(tf.scale).mul(0.5f));
        Vector2f max = new Vector2f(tf.position).add(new Vector2f(tf.scale).mul(0.5f));

        Vector2f[] vertices = {
                new Vector2f(min.x, min.y), new Vector2f(min.x, max.y),
                new Vector2f(max.x, max.y), new Vector2f(max.x, min.y)
        };

        Vector2f pointCoords = new Vector2f(x,y);
        if(tf.rotation != 0.0f){
            //inversely rotate point to check enclosure in original rect
            JMath.rotate(pointCoords,-tf.rotation,tf.position);
        }
        float xPoint = pointCoords.x;
        float yPoint = pointCoords.y;

        float xMin = vertices[0].x;
        float xMax = vertices[2].x;
        float yMin = vertices[0].y;
        float yMax = vertices[2].y;

        return xPoint > xMin && xPoint < xMax && yPoint > yMin && yPoint < yMax;
    }
}