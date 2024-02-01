package editor;

import components.Sprite;
import components.SpriteRenderer;
import contra.GameObject;
import contra.MouseListener;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class ScaleGizmo extends Gizmo{
    private float scalingConstant = 0.3f;
    public ScaleGizmo(Sprite gizmoSprite, GizmoSystem gizmoSystem){
        super(gizmoSprite, gizmoSystem);
    }

    @Override
    public void editorUpdate(float dt){
        GameObject activeGameObject = gizmoSystem.activeGameObject;
        if(activeGameObject != null){
            if(MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)){
                float mouseWorldX = MouseListener.getOrthoX();
                float mouseWorldY = MouseListener.getOrthoY();

                if(xaxis.insideObject(mouseWorldX,mouseWorldY)){
                    xaxis.getComponent(SpriteRenderer.class).setColour(gizmoSystem.xaxisHoverColour);
                    float dragX = MouseListener.getWorldDx();
                    activeGameObject.tf.scale.x += scalingConstant*dragX;
                }else if(yaxis.insideObject(mouseWorldX,mouseWorldY)){ //else if so only one of the two happens at one time
                    yaxis.getComponent(SpriteRenderer.class).setColour(gizmoSystem.yaxisHoverColour);
                    float dragY = MouseListener.getWorldDy();
                    activeGameObject.tf.scale.y += scalingConstant*dragY;
                }
            }
            gizmoSystem.move(activeGameObject.tf.position);
        }else{
            makeTransparent();
        }
    }
}
