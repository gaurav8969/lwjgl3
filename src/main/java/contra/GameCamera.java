package contra;

import components.Component;
import components.PlayerController;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import util.Settings;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;

public class GameCamera extends Component {
    private Camera sceneCamera;
    private GameObject player;
    private PlayerController playerController;
    private Vector2f playerSpeed;
    private Vector2f cameraPos;
    private float projectionWidth;
    private float projectionHeight;

    @Override
    public void init(){
        player = Window.getScene().getGameObject(PlayerController.class);
        playerController = player.getComponent(PlayerController.class);
        playerSpeed = playerController.getVelocity();
        sceneCamera = Window.getScene().camera();
        cameraPos = sceneCamera.position();
        projectionHeight = sceneCamera.projectionHeight;
        projectionWidth = sceneCamera.projectionWidth;
    }

    @Override
    public void update(float dt){
        Vector2f playerAbs = new Vector2f(player.tf.position);
        Vector2f playerRel = playerAbs.sub(cameraPos, new Vector2f());

        boolean goingRight = playerSpeed.x > 0;
        if(goingRight) {
            if (playerRel.x > projectionWidth/2f) {
                cameraPos.x = Math.max(cameraPos.x, playerAbs.x - sceneCamera.projectionWidth / 2f);
            }
        }else {
            if (playerAbs.x > projectionWidth/2f && playerRel.x < projectionWidth/2f) {
                cameraPos.x = Math.min(cameraPos.x, playerAbs.x - sceneCamera.projectionWidth / 2f);
            }
        }

        boolean goingUp = playerSpeed.y > 0;
        boolean standardRange = playerAbs.y > 0f && playerAbs.y < projectionHeight;

        if(standardRange)return;

        if(goingUp){
            if(playerRel.y > projectionHeight/2f){
                cameraPos.y = Math.max(cameraPos.y, playerAbs.y - 2.5f* Settings.GRID_HEIGHT);
            }
        }else{
            if(playerRel.y < projectionHeight/2f){
                cameraPos.y = Math.min(cameraPos.y, playerAbs.y - 2.5f*Settings.GRID_HEIGHT);
            }
        }
    }
}
