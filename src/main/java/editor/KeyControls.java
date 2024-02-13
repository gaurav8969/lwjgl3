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
    private final float debounceTime = 0.1f;
    private float debounce = 0f;
    @Override
    public void editorUpdate(float dt) {
        debounce -= dt;

        PropertiesWindow propertiesWindow = Window.getImGuilayer().getPropertiesWindow();
        //we search for the objects in the current scene
        List<GameObject> activeGameObjects = propertiesWindow.getActiveObjects();
        List<Vector4f> activeColours = propertiesWindow.getActiveColours();

        float moveFactor = KeyListener.isKeyPressed(GLFW_KEY_LEFT_SHIFT) ? 0.1f : 1;

        if(!activeGameObjects.isEmpty()) {
            if(KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) && KeyListener.keyBeginPress(GLFW_KEY_D)) {
                for (int i = 0; i < activeGameObjects.size(); i++) {
                    GameObject go = activeGameObjects.get(i);
                    GameObject newObj = go.copy();
                    newObj.tf.position.add(Settings.GRID_WIDTH, Settings.GRID_HEIGHT);
                    SpriteRenderer spr = newObj.getComponent(SpriteRenderer.class);
                    if (spr != null) {
                        spr.setColour(activeColours.get(i));
                    }
                    Window.getScene().addGameObjectToScene(newObj);
                }
            }else if (KeyListener.isKeyPressed(GLFW_KEY_UP) && debounce < 0) {
                for (int i = 0; i < activeGameObjects.size(); i++) {
                    GameObject go = activeGameObjects.get(i);
                    go.tf.position.y += moveFactor * Settings.GRID_HEIGHT;
                }
            }else if (KeyListener.isKeyPressed(GLFW_KEY_DOWN) && debounce < 0) {
                for (int i = 0; i < activeGameObjects.size(); i++) {
                    GameObject go = activeGameObjects.get(i);
                    go.tf.position.y -= moveFactor * Settings.GRID_HEIGHT;
                }
            }else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT) && debounce < 0) {
                for (int i = 0; i < activeGameObjects.size(); i++) {
                    GameObject go = activeGameObjects.get(i);
                    go.tf.position.x -= moveFactor * Settings.GRID_WIDTH;
                }
            }else if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT) && debounce < 0) {
                for (int i = 0; i < activeGameObjects.size(); i++) {
                    GameObject go = activeGameObjects.get(i);
                    go.tf.position.x += moveFactor * Settings.GRID_WIDTH;
                }
            }else if (KeyListener.keyBeginPress(GLFW_KEY_DELETE)) {
                for (GameObject go : activeGameObjects) {
                    go.destroy();
                }
                propertiesWindow.clearActiveObjects();
            }

            GameObject go = Window.getImGuilayer().getPropertiesWindow().getActiveGameObject();

            if (go != null) {
                if (KeyListener.isKeyPressed(GLFW_KEY_P) && GameViewWindow.isFocused()) {
                    MouseControls mouseControls = this.gameObject.getComponent(MouseControls.class);
                    mouseControls.scoop(go);
                }
            }
        }

        if(debounce < 0) {
            debounce = debounceTime;
        }
    }
}