package contra;

import java.util.HashMap;
import java.util.Map;

public abstract class Component {
    //parent entity
    public GameObject gameObject = null;
    public abstract void init();
    public abstract void update(float dt);
}