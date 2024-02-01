package editor;

import components.Component;
import contra.GameObject;
import contra.KeyListener;
import contra.Window;

import static org.lwjgl.glfw.GLFW.*;

public class KeyControls extends Component {
    @Override
    public void editorUpdate(float dt){
        PropertiesWindow propertiesWindow = Window.getImGuilayer().getPropertiesWindow();
        GameObject activeGameObject = propertiesWindow.getActiveGameObject();
        if(activeGameObject == null)return;
        if(KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) &&
                KeyListener.keyBeginPress(GLFW_KEY_D)) {
            GameObject newObj = activeGameObject.copy();
            Window.getScene().addGameObjectToScene(newObj);
            newObj.tf.position.add(0.1f, 0.1f);
            propertiesWindow.setActiveGameObject(newObj);
        }else if(KeyListener.keyBeginPress(GLFW_KEY_DELETE)){
            propertiesWindow.setActiveGameObject(null);
            activeGameObject.destroy();
        }
    }
}
