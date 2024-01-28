package physics2D.components;

import components.Component;
import org.jbox2d.common.Vec2;
import org.joml.Vector2f;
import org.joml.Vector3f;
import renderer.DebugDraw;

public class Box2DCollider extends Collider {
    private Vector2f halfSize = new Vector2f(1);
    private Vector2f origin = new Vector2f();

    public Vector2f getHalfSize() {
        return halfSize;
    }

    public void setHalfSize(Vector2f halfSize) {
        this.halfSize = halfSize;
    }

    public Vector2f getOrigin(){
        return this.origin;
    }

    @Override
    public void editorUpdate(float dt){
        Vector2f scale = new Vector2f(this.gameObject.tf.scale);
        Vector2f centre = new Vector2f(this.gameObject.tf.position).add(new Vector2f(scale).mul(0.5f));
        DebugDraw.addBox2D(centre, scale.mul(scalingFactor), this.gameObject.tf.rotation,
                new Vector3f(0,0,1), 1);
    }
}
