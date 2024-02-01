package editor;

import components.*;
import contra.GameObject;
import contra.MouseListener;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class TranslateGizmo extends Gizmo {
    public TranslateGizmo(Sprite gizmoSprite, GizmoSystem gizmoSystem){
        super(gizmoSprite,gizmoSystem);
    }

    @Override
    public void editorUpdate(float dt){
        GameObject activeGameObject = gizmoSystem.activeGameObject;
        if(activeGameObject != null){
            if(MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)){
                float mouseWorldX = MouseListener.getOrthoX();
                float mouseWorldY = MouseListener.getOrthoY();

                if(xaxis.insideObject(mouseWorldX, mouseWorldY)){
                    xaxis.getComponent(SpriteRenderer.class).setColour(gizmoSystem.xaxisHoverColour);
                    float dragX = MouseListener.getWorldDx();
                    activeGameObject.tf.position.x += dragX;
                    gizmoSystem.changePosition(dragX, 0);
                }else if(yaxis.insideObject(mouseWorldX, mouseWorldY)){ //else if so only one of the two happens at one time
                    yaxis.getComponent(SpriteRenderer.class).setColour(gizmoSystem.yaxisHoverColour);
                    float dragY = MouseListener.getWorldDy();
                    activeGameObject.tf.position.y += dragY;
                    gizmoSystem.changePosition(0,dragY);
                }
            }
            gizmoSystem.move(activeGameObject.tf.position);
        }else{
            makeTransparent();
        }
    }
}