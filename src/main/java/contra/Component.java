package contra;

import java.util.HashMap;
import java.util.Map;

public abstract class Component {
    //parent entity, transient to avoid infinitely recursive serialization of game objects
    public transient GameObject gameObject = null;

    public void init(){}

    public void update(float dt){}

    public void imGui(){}

}