package editor;

import components.Component;
import contra.Camera;
import contra.KeyListener;
import contra.MouseListener;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_DECIMAL;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class EditorCamera extends Component {
    private Vector2f dragStart, dragEnd, currentPos, newPosition; //current and new camera position i.e
    private float dragSensitivity = 40.0f;
    private float scrollSensitivity = 0.2f;
    private Camera camera;
    private boolean wasDragging;
    private boolean toReset;
    private float zoomLerpTime;
    private float posLerpTime;

    public EditorCamera(Camera camera){
        this.camera = camera;
        this.wasDragging = false;
        this.currentPos = camera.position();
        this.newPosition = new Vector2f().set(currentPos);
        this.dragStart = new Vector2f();
        this.dragEnd = new Vector2f();
        this.toReset = false;
    }

    @Override
    public void editorUpdate(float dt){
        toReset(dt); //check and perform reset if needed

        float screenX = MouseListener.getScreenX();
        float screenY = MouseListener.getScreenY();

        //click is outside the viewport
        if(screenX < 0 || screenX > 1920 || screenY < 0 || screenY > 1080 || !GameViewWindow.isFocused()){
            return;
        }

        if(!wasDragging && MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)){//dragging started this frame
            //some other window is clicked on
            wasDragging = true;
            dragStart.set(MouseListener.getOrthoX(), MouseListener.getOrthoY());
        }else if(wasDragging){ //dragging stopped
            wasDragging = false;
            dragEnd.set(MouseListener.getOrthoX(), MouseListener.getOrthoY());
            camera.position().sub(dragEnd.sub(dragStart), newPosition);
        }

        if(!currentPos.equals(newPosition)){
            currentPos.lerp(newPosition, dragSensitivity*dt);
            Vector2f diff = new Vector2f();
            newPosition.sub(currentPos,diff);
            if(diff.x < 0.30f && diff.y < 0.30f){
                currentPos.set(newPosition);
            }
        }

        if(MouseListener.getScrollY() != 0 ){
            float scrollY = MouseListener.getScrollY();
            float addValue = (float)Math.pow(Math.abs(scrollY * scrollSensitivity),
                    1/camera.getZoom());
            addValue *= -Math.signum(scrollY);
            camera.addZoom(addValue);
        }
    }

    private void toReset(float dt){
        if (KeyListener.isKeyPressed(GLFW_KEY_KP_DECIMAL)) {
            toReset = true;
        }

        if(toReset){
            zoomLerpTime += 0.2f*dt;
            posLerpTime += 0.8f*dt;
            float zoomAddValue = Math.abs(1.0f-camera.getZoom())* zoomLerpTime;
            if(camera.getZoom() > 1){
                zoomAddValue = -zoomAddValue;
            }
            camera.addZoom(zoomAddValue);
            if((Math.abs(1 - camera.getZoom()) < 0.001f) || posLerpTime > 0.4f){
                camera.setZoom(1.0f);
            }

            camera.position().lerp(new Vector2f(), posLerpTime);

            if((Math.abs(camera.position().x) < 0.30f && Math.abs(camera.position().y) < 0.30f) || posLerpTime > 0.4f ){
                camera.position().set(0.0f);
            }

            if(camera.getZoom() == 1.0f && camera.position().equals(0f,0f)){
                toReset = false;
                zoomLerpTime = 0.0f;
                posLerpTime = 0.0f;
                newPosition = currentPos;
            }
        }
    }

    @Override
    public void imGui(){}
}