package editor;

import components.*;
import contra.GameObject;
import contra.MouseListener;
import contra.Window;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.AssetPool;

public class Gizmo extends Component {

    public GameObject yaxis;
    public GameObject xaxis;
    protected GizmoSystem gizmoSystem;

    public Gizmo(Sprite gizmoSprite, GizmoSystem gizmoSystem){
        this.gizmoSystem = gizmoSystem;
        xaxis = Window.getScene().createGameObject("xaxis").setSize(gizmoSystem.gizmoSize);
        yaxis = Window.getScene().createGameObject("yaxis").setSize(gizmoSystem.gizmoSize);
        xaxis.addComponent(new SpriteRenderer().setSprite(gizmoSprite).setZIndex(100)).setRotation(90).addComponent(new Unpickable());
        yaxis.addComponent(new SpriteRenderer().setSprite(gizmoSprite).setZIndex(100)).setRotation(180).addComponent(new Unpickable());
        xaxis.makeUnserializable();
        yaxis.makeUnserializable();
        Window.getScene().addGameObjectToScene(xaxis);
        Window.getScene().addGameObjectToScene(yaxis);
    }

    public void makeVisible(){
        xaxis.getComponent(SpriteRenderer.class).setColour(gizmoSystem.xaxisColour);
        yaxis.getComponent(SpriteRenderer.class).setColour(gizmoSystem.yaxisColour);
    }

    public void makeTransparent(){
        xaxis.getComponent(SpriteRenderer.class).setColour(new Vector4f(0,0,0,0));
        yaxis.getComponent(SpriteRenderer.class).setColour(new Vector4f(0,0,0,0));
    }

    public void setPosition(Vector2f position){
        xaxis.tf.position.set(position.add(gizmoSystem.xAxisOffset, new Vector2f()));
        yaxis.tf.position.set(position.add(gizmoSystem.yAxisOffset, new Vector2f()));
    }
}