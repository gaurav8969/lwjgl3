package editor;

import components.*;
import contra.GameObject;
import contra.MouseListener;

public class TranslateGizmo extends Gizmo {
    public TranslateGizmo(Sprite gizmoSprite, GizmoSystem gizmoSystem){
        super(gizmoSprite,gizmoSystem);
    }

    @Override
    public void editorUpdate(float dt){
        GameObject activeGameObject = gizmoSystem.activeGameObject;
        if(activeGameObject != null){
            if(!gizmoSystem.isGizmo(activeGameObject)){
                gizmoSystem.attachedGameObject = activeGameObject;
            }

            if(MouseListener.isDragging()){
                GameObject attachedGameObject = gizmoSystem.attachedGameObject;

                if(activeGameObject.getID() == xaxis.getID()){
                    xaxis.getComponent(SpriteRenderer.class).setColour(gizmoSystem.xaxisHoverColour);
                    float dragX = MouseListener.getWorldDx();
                    attachedGameObject.tf.position.x += dragX;
                    gizmoSystem.changePosition(dragX, 0);
                }

                if(activeGameObject.getID() == yaxis.getID()){
                    yaxis.getComponent(SpriteRenderer.class).setColour(gizmoSystem.yaxisHoverColour);
                    float dragY = MouseListener.getWorldDy();
                    attachedGameObject.tf.position.y += dragY;
                    gizmoSystem.changePosition(0,dragY);
                }
            }else{
                makeVisible();
            }

            if(gizmoSystem.isGizmo(activeGameObject))return;

            gizmoSystem.move(activeGameObject.tf.position);
        }else{
            gizmoSystem.attachedGameObject = null;
            makeTransparent();
        }
    }
}