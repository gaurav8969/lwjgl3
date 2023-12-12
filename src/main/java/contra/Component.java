package contra;

import java.util.HashMap;
import java.util.Map;

public abstract class Component {
    //parent entity
    public GameObject gameObject = null;
    public abstract void init();
    public abstract void update();
}

class ComponentID{
    private static Map<Class<? extends Component>, Short> ids = new HashMap<>();
    private static short lastid = 0;
    static <T extends Component> short getUniqueID(Class<T> componentClass){
        var ID = ids.get(componentClass);
        if(ID != null) {
            return ID;
        }else{
            ids.put(componentClass,lastid);
           return lastid++;
        }
    }
}