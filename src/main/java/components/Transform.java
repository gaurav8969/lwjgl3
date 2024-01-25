package components;

import contra.GameObject;
import imgui.ImGui;
import org.joml.Vector2f;
import editor.CImgui;

public class Transform extends Component{
    public Vector2f scale;
    public float rotation = 0.0f;
    public Vector2f position;

    public Transform(){
        this.position = new Vector2f();
        this.scale = new Vector2f(64f, 64f);
    }

    public Transform setPosition(Vector2f position){
        this.position = position;
        return this;
    }

    public Transform set(Vector2f position, Vector2f scale){
        this.position = position;
        this.scale = scale;
        return this;
    }

    public Transform set(Vector2f position, Vector2f scale, float rotation){
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
        return new Transform().set(new Vector2f(this.position),new Vector2f(this.scale),this.rotation);
    }

    public void copyTo(Transform to){
        to.position.set(this.position);
        to.scale.set(this.scale);
        to.rotation = this.rotation;
    }

    @Override
    public void imGui() {
        CImgui.drawVec2Control("Position", this.position);
        CImgui.drawVec2Control("Scale", this.scale, 64.0f);
        CImgui.dragFloat("Rotation", this.rotation);
    }
}