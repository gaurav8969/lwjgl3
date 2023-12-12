package contra;

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
    }
    void init(){};
    public void addComponent(Component c){
        short id = ComponentID.getUniqueID(c.getClass());
        componentsBitset.set(id);
        components[id] = c;
    }
    public <T extends Component> boolean hasComponent(Class<T> componentClass){
        short id = ComponentID.getUniqueID(componentClass);
        return componentsBitset.get(id);
    };

    public <T extends Component> Component getComponent(Class<T> componentClass){
        short id = ComponentID.getUniqueID(componentClass);
        return components[id];
    }
    public <T extends Component> void removeComponent(Class<T> componentClass){
        short id = ComponentID.getUniqueID(componentClass);
        componentsBitset.clear(id);
        components[id] = null;
    }
}
