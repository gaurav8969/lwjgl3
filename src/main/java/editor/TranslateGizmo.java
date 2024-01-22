package editor;

import Renderer.Texture;
import components.*;
import contra.GameObject;
import contra.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import util.AssetPool;

public class TranslateGizmo extends Component {
    private Vector4f xaxisColour = new Vector4f(1f,0f,0f,1f);
    private Vector4f yaxisColour = new Vector4f(0,1f,0f,1f);

    private PropertiesWindow propertiesWindow;
    private GameObject up;
    private GameObject right;
    private Vector2f xAxisOffset = new Vector2f(48,-23);
    private Vector2f yAxisOffset = new Vector2f(1,37);

    public TranslateGizmo(PropertiesWindow propertiesWindow){
        this.propertiesWindow  = propertiesWindow;
        Spritesheet gizmos = AssetPool.getSpriteSheet("assets/images/gizmos.png");
        Sprite arrow = gizmos.getSprite(1);
        up = new GameObject().setName("up").setSize(new Vector2f(24,48));
        right = new GameObject().setName("right").setSize(new Vector2f(24,48));
        up.addComponent(new SpriteRenderer().setSprite(arrow).setZIndex(2)).setRotation(180);
        right.addComponent(new SpriteRenderer().setSprite(arrow).setZIndex(2)).setRotation(90);
        up.makeUnserializable();
        right.makeUnserializable();
        Window.getScene().addGameObjectToScene(up);
        Window.getScene().addGameObjectToScene(right);
    }

    public void update(float dt){
        GameObject activeGameObject = propertiesWindow.getActiveGameObject();
        if(activeGameObject != null){
            makeVisible();
            up.tf.position.set(activeGameObject.tf.position.add(yAxisOffset, new Vector2f()));
            right.tf.position.set(activeGameObject.tf.position.add(xAxisOffset, new Vector2f()));
        }else{
            makeTransparent();
        }
    }

    public void makeVisible(){
        up.getComponent(SpriteRenderer.class).setColour(yaxisColour);
        right.getComponent(SpriteRenderer.class).setColour(xaxisColour);
    }

    public void makeTransparent(){
        up.getComponent(SpriteRenderer.class).setColour(new Vector4f(0,0,0,0));
        right.getComponent(SpriteRenderer.class).setColour(new Vector4f(0,0,0,0));
    }
}