package physics2D.components;

import components.Component;
import contra.Window;
import org.jbox2d.common.Vec2;
import org.joml.Vector2f;
import org.joml.Vector3f;
import renderer.DebugDraw;

public class Box2DCollider extends Component {
    private Vector2f halfSize = new Vector2f(1);
    private Vector2f origin = new Vector2f();
    protected Vector2f offset = new Vector2f(0,0);

    public Vector2f getHalfSize() {
        return halfSize;
    }

    public Box2DCollider setHalfSize(Vector2f halfSize) {
        this.halfSize = halfSize;
        return this;
    }

    public Vector2f getOrigin(){
        return this.origin;
    }

    @Override
    public void editorUpdate(float dt){
        Vector2f scale = new Vector2f(halfSize).mul(2);
        Vector2f centre = new Vector2f(this.gameObject.tf.position).add(offset);
        Window.getScene().debugDraw().addBox2D(centre, scale, this.gameObject.tf.rotation,
                new Vector3f(0,0,255), 1);
    }

    public Vector2f getOffset(){
        return this.offset;
    }

    public void setOffset(Vector2f offset){
        this.offset = offset;
    }
}