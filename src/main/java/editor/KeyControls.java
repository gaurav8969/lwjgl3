package editor;

import components.Component;
import components.SpriteRenderer;
import contra.GameObject;
import contra.KeyListener;
import contra.Window;
import org.joml.Vector4f;
import util.Settings;

import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class KeyControls extends Component {
    @Override
    public void editorUpdate(float dt){
        PropertiesWindow propertiesWindow = Window.getImGuilayer().getPropertiesWindow();

        //we search for the objects in the current scene
        List<GameObject> activeGameObjects = propertiesWindow.getActiveObjects();
        List<Vector4f> activeColours = propertiesWindow.getActiveColours();
        if(!activeGameObjects.isEmpty()){
            MouseControls mouseControls = this.gameObject.getComponent(MouseControls.class);
            if(mouseControls.holdingObject != null){
                if(KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)){
                    mouseControls.holdingObject.destroy();
                    mouseControls.drop();
                    propertiesWindow.setActiveGameObject(null);
                }
            }

            if(KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) && KeyListener.keyBeginPress(GLFW_KEY_D)) {
                for(int i = 0; i < activeGameObjects.size(); i++){
                    GameObject go = activeGameObjects.get(i);
                    GameObject newObj = go.copy();
                    newObj.tf.position.add(Settings.GRID_WIDTH, Settings.GRID_HEIGHT);
                    SpriteRenderer spr = newObj.getComponent(SpriteRenderer.class);
                    if(spr != null) {
                        spr.setColour(activeColours.get(i));
                    }
                    Window.getScene().addGameObjectToScene(newObj);
                }
            }else if(KeyListener.keyBeginPress(GLFW_KEY_DELETE)){
                for(GameObject go: activeGameObjects){
                    go.destroy();
                }
                propertiesWindow.clearActiveObjects();
            }
        }

        GameObject go = Window.getImGuilayer().getPropertiesWindow().getActiveGameObject();

        if(go != null){
            if(KeyListener.isKeyPressed(GLFW_KEY_P)){
                MouseControls mouseControls = this.gameObject.getComponent(MouseControls.class);
                mouseControls.scoop(go);
            }
        }
    }
}