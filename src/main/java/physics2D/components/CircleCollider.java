package physics2D.components;

import components.Component;
import contra.Window;
import org.joml.Vector2f;
import renderer.DebugDraw;

public class CircleCollider extends Component {
    protected Vector2f offset = new Vector2f(0,0);
    private float radius = 1f;

    public float getRadius() {
        return radius;
    }

    public Vector2f getOffset(){
        return this.offset;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setOffset(Vector2f offset){
        this.offset = offset;
    }

    @Override
    public void editorUpdate(float dt) {
        Vector2f center = new Vector2f(this.gameObject.tf.position).add(this.offset);
        Window.getScene().debugDraw().addCircle2D(center, this.radius);
    }
}
