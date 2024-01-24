package editor;

import components.*;
import contra.GameObject;
import contra.MouseListener;
import contra.Window;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.AssetPool;

public class Gizmo extends Component {
    protected Vector4f xaxisColour = new Vector4f(1f,0f,0f,1f);
    protected Vector4f yaxisColour = new Vector4f(0,1f,0f,1f);

    protected Vector4f xaxisHoverColour = new Vector4f(0.5f, 0f,0f,1f);
    protected Vector4f yaxisHoverColour = new Vector4f(0f, 0.7f,0f,1f);

    protected Vector2f gizmoSize = new Vector2f(50,100);
    public GameObject yaxis;
    public GameObject xaxis;
    protected Vector2f xAxisOffset = new Vector2f(60,0);
    protected Vector2f yAxisOffset = new Vector2f(20,65);
    protected GizmoSystem gizmoSystem;

    public Gizmo(Sprite gizmoSprite, GizmoSystem gizmoSystem){
        this.gizmoSystem = gizmoSystem;
        xaxis = new GameObject().setName("xaxis").setSize(new Vector2f(24,48));
        yaxis = new GameObject().setName("yaxis").setSize(new Vector2f(24,48));
        xaxis.addComponent(new SpriteRenderer().setSprite(gizmoSprite).setZIndex(2)).setRotation(90);
        yaxis.addComponent(new SpriteRenderer().setSprite(gizmoSprite).setZIndex(2)).setRotation(180);
        xaxis.makeUnserializable();
        yaxis.makeUnserializable();
        Window.getScene().addGameObjectToScene(xaxis);
        Window.getScene().addGameObjectToScene(yaxis);
    }

    public void makeVisible(){
        xaxis.getComponent(SpriteRenderer.class).setColour(xaxisColour);
        yaxis.getComponent(SpriteRenderer.class).setColour(yaxisColour);
    }

    public void makeTransparent(){
        xaxis.getComponent(SpriteRenderer.class).setColour(new Vector4f(0,0,0,0));
        yaxis.getComponent(SpriteRenderer.class).setColour(new Vector4f(0,0,0,0));
    }

    public void setPosition(Vector2f position){
        xaxis.tf.position.set(position.add(xAxisOffset, new Vector2f()));
        yaxis.tf.position.set(position.add(yAxisOffset, new Vector2f()));
    }
}