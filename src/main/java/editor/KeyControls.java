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
        if(!activeGameObjects.isEmpty()){
            if(KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) && KeyListener.keyBeginPress(GLFW_KEY_D)) {
                for(GameObject go: activeGameObjects){
                    GameObject newObj = go.copy();
                    newObj.getComponent(SpriteRenderer.class).setColour(new Vector4f(1,1,1,1));
                    Window.getScene().addGameObjectToScene(newObj);
                    newObj.tf.position.add(Settings.GRID_WIDTH, Settings.GRID_HEIGHT);
                }
            }else if(KeyListener.keyBeginPress(GLFW_KEY_DELETE)){
                for(GameObject go: activeGameObjects){
                    go.destroy();
                }
                activeGameObjects.clear();
            }
        }
    }
}