package components;

import org.joml.Vector2f;

public class Transform {
    public Vector2f position;
    public Vector2f scale;
    public float rotation = 0.0f;

    public Transform(){
        this.position = new Vector2f();
        this.scale = new Vector2f(64f,64f);
    }
    public Transform setTransform(Vector2f position, Vector2f scale, float rotation){
        this.position = position;
        this.scale = scale;
        this.rotation = rotation;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Transform)) {
            return false;
        }

        Transform tf = (Transform) obj;
        return tf.scale.equals(this.scale) && tf.position.equals(this.position) && tf.rotation == this.rotation;
    }

    public Transform copy(){
        return new Transform().setTransform(new Vector2f(this.position),new Vector2f(this.scale),this.rotation);
    }

    public void copyTo(Transform to){
        to.position.set(this.position);
        to.scale.set(this.scale);
        to.rotation = this.rotation;
    }
}