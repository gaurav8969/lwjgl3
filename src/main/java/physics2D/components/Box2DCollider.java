package physics2D.components;

import org.jbox2d.common.Vec2;
import org.joml.Vector2f;

public class Box2DCollider {
    private Vector2f halfSize = new Vector2f(1);

    public Vector2f getHalfSize() {
        return halfSize;
    }

    public void setHalfSize(Vector2f halfSize) {
        this.halfSize = halfSize;
    }
}
