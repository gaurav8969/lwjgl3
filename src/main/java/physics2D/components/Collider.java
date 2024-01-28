package physics2D.components;

import components.Component;
import org.joml.Vector2f;

public class Collider extends Component {
    protected Vector2f offset = new Vector2f(3,3);
    protected float scalingFactor = 1.08f;

    public Vector2f getOffset(){
        return this.offset;
    }
    public float getScalingFactor(){return this.scalingFactor;}
}
