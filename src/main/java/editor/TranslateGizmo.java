package editor;

import Renderer.Texture;
import components.*;
import contra.GameObject;
import contra.MouseListener;
import contra.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import util.AssetPool;

public class TranslateGizmo extends Gizmo {
    public TranslateGizmo(Sprite gizmoSprite, GizmoSystem gizmoSystem){
        super(gizmoSprite,gizmoSystem);
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
                    xaxis.getComponent(SpriteRenderer.class).setColour(xaxisHoverColour);
                    float dragX = MouseListener.getWorldDx();
                    attachedGameObject.tf.position.x += dragX;
                    gizmoSystem.changePosition(dragX, 0);
                }

                if(activeGameObject.getID() == yaxis.getID()){
                    yaxis.getComponent(SpriteRenderer.class).setColour(yaxisHoverColour);
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