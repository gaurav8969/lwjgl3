package editor;

import components.Component;
import components.Sprite;
import contra.GameObject;
import contra.KeyListener;
import contra.MouseListener;
import contra.Window;
import org.joml.Vector2f;
import util.AssetPool;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;

public class GizmoSystem extends Component {
    Sprite translateSprite;
    Sprite scaleSprite;
    TranslateGizmo translateGizmo;
    ScaleGizmo scaleGizmo;
    Gizmo activeGizmo;
    PropertiesWindow propertiesWindow;
    protected GameObject activeGameObject;
    protected GameObject attachedGameObject;

    public GizmoSystem(){
        translateSprite = AssetPool.getSpriteSheet("assets/images/gizmos.png").getSprite(1);
        scaleSprite = AssetPool.getSpriteSheet("assets/images/gizmos.png").getSprite(2);
        this.propertiesWindow = Window.getImGuilayer().getPropertiesWindow();
        scaleGizmo = new ScaleGizmo(scaleSprite, this);
        translateGizmo = new TranslateGizmo(translateSprite, this);

        activeGizmo = translateGizmo;
        scaleGizmo.makeTransparent();
    }

    @Override
    public void update(float dt){
        activeGameObject = propertiesWindow.getActiveGameObject();

        activeGizmo.update(dt);

        if(KeyListener.isKeyPressed(GLFW_KEY_E) && activeGizmo != translateGizmo){
            activeGizmo.makeTransparent();
            activeGizmo = translateGizmo;
            activeGizmo.makeVisible();
        }

        if(KeyListener.isKeyPressed(GLFW_KEY_R) && activeGizmo != scaleGizmo){
            activeGizmo.makeTransparent();
            activeGizmo = scaleGizmo;
            activeGizmo.makeVisible();
        }
    }

    protected boolean isGizmo(GameObject go){
        int objectID = go.getID();
        return objectID == translateGizmo.xaxis.getID() || objectID == translateGizmo.yaxis.getID()
                || objectID == scaleGizmo.xaxis.getID() || objectID == scaleGizmo.yaxis.getID();
    }

    protected void changePosition(float dx, float dy ){
        translateGizmo.xaxis.tf.position.x += dx;
        translateGizmo.yaxis.tf.position.x += dx;
        translateGizmo.xaxis.tf.position.y += dy;
        translateGizmo.yaxis.tf.position.y += dy;

        scaleGizmo.xaxis.tf.position.x += dx;
        scaleGizmo.yaxis.tf.position.x += dx;
        scaleGizmo.xaxis.tf.position.y += dy;
        scaleGizmo.yaxis.tf.position.y += dy;
    }

    //move the gizmo system
    protected void move(Vector2f position){
        translateGizmo.setPosition(position);
        scaleGizmo.setPosition(position);
    }

    @Override
    public void imGui(){}
}