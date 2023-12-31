package contra;

import components.Component;
import components.ComponentID;
import components.Transform;
import org.joml.Vector2f;

public class GameObject {
    //starts at zero every game load
    public static int IDCounter = 0;
    private int uniqueID = -1;
    private String name;
    private int componentsSize = 16;
    //array instead of actual bitset since it causes serialization issues with gson, jackson works after
    // certain module imports alternatively could write custom type-adapter for bitsets with gson
    public boolean[] componentsBitset = new boolean[componentsSize];
    public Component[] components = new Component[componentsSize];
    public Transform tf = new Transform();

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

    public void imGui(){
        for(Component c: components){
            if(c != null) {
                c.imGui();
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
            if (componentClass.isAssignableFrom(c.getClass())) {
                componentsBitset[id] = false;
                components[id] = null;
            }
        }
    }

    public int generateID(){
        if(uniqueID == -1){
            uniqueID = IDCounter++;
        }
        return uniqueID;
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

    public Component[] getComponents(){
        return components;
    }

    public static void loadCounter(int maxID){
        IDCounter = maxID;
    }
}