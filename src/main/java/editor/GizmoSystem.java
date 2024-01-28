package editor;

import components.Component;
import components.Sprite;
import components.Transform;
import contra.GameObject;
import contra.KeyListener;
import contra.MouseListener;
import contra.Window;
import org.joml.Vector2f;
import org.joml.Vector4f;
import renderer.DebugDraw;
import util.AssetPool;

import java.lang.annotation.Target;

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
    protected Vector2f gizmoSize = new Vector2f(26,52);
    protected Vector2f xAxisOffset = new Vector2f(66.5f,-4.1f);
    protected Vector2f yAxisOffset = new Vector2f(21.8f ,64f);
    protected Vector4f xaxisColour = new Vector4f(1f,0f,0f,1f);
    protected Vector4f yaxisColour = new Vector4f(0,1f,0f,1f);
    protected Vector4f xaxisHoverColour = new Vector4f(0.5f, 0f,0f,1f);
    protected Vector4f yaxisHoverColour = new Vector4f(0f, 0.7f,0f,1f);

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
    public void editorUpdate(float dt){
        activeGameObject = propertiesWindow.getActiveGameObject();

        activeGizmo.editorUpdate(dt);

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
    public void imGui(){
        CImgui.drawVec2Control("Gizmo Size", gizmoSize);
        CImgui.drawVec2Control("X-Axis offset", xAxisOffset);
        CImgui.drawVec2Control("Y-Axis offset", yAxisOffset);
        CImgui.colorPicker4("X-Axis Colour", xaxisColour);
        CImgui.colorPicker4("X-Axis Hover Colour", xaxisHoverColour);
        CImgui.colorPicker4("Y-Axis Colour", yaxisColour);
        CImgui.colorPicker4("Y-Axis Hover Colour", yaxisHoverColour);
    }
}