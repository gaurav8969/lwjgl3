package contra;

import components.Transform;

import java.util.BitSet;

public class GameObject {
    private String name;
    private int componentsSize;
    public BitSet componentsBitset;
    public Component[] components;

    public GameObject(String Name){
        name = Name;
        componentsSize = 32;
        componentsBitset = new BitSet(32);
        components = new Component[componentsSize];
        Transform tf = new Transform();
        this.addComponent(tf);
    }

    public GameObject(String Name, Transform transform){
        name = Name;
        componentsSize = 32;
        componentsBitset = new BitSet(32);
        components = new Component[componentsSize];
        this.addComponent(transform);
    }

    void init(){
        for(Component c : components){
            if(c != null){
            c.init();
            }
        }
    }

    void update(float dt){
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

    public void addComponent(Component c){
        short id = ComponentID.getUniqueID(c.getClass());
        componentsBitset.set(id);
        components[id] = c;
        c.gameObject = this;
    }

    public <T extends Component> boolean hasComponent(Class<T> componentClass){
        short id = ComponentID.getUniqueID(componentClass);
        return componentsBitset.get(id);
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
                componentsBitset.clear(id);
                components[id] = null;
            }
        }
    }
}