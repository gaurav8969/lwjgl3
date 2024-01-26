package editor;

import components.Sprite;
import components.SpriteRenderer;
import contra.GameObject;
import contra.MouseListener;
import org.joml.Vector2f;

public class ScaleGizmo extends Gizmo{
    private float scalingConstant = 0.3f;
    public ScaleGizmo(Sprite gizmoSprite, GizmoSystem gizmoSystem){
        super(gizmoSprite, gizmoSystem);
    }

    @Override
    public void update(float dt){
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
                    attachedGameObject.tf.scale.x += scalingConstant*dragX;
                }

                if(activeGameObject.getID() == yaxis.getID()){
                    yaxis.getComponent(SpriteRenderer.class).setColour(gizmoSystem.yaxisHoverColour);
                    float dragY = MouseListener.getWorldDy();
                    attachedGameObject.tf.scale.y += scalingConstant*dragY;
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
