package contra;

import components.Component;
import components.Pipe;
import components.PlayerController;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.Settings;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;

public class GameCamera extends Component {
    public boolean locked;
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

        if(playerController.isDead())return;

        if(playerAbs.y > 0){
            locked = false;
            sceneCamera.clearColour = new Vector4f(107f/255,140f/255,1f,1f);
            cameraPos.y = 0;
        }else{
            if(Pipe.isUnderground()){
                sceneCamera.clearColour = new Vector4f(0f,0f,0f,0f);
                if(locked)return;
                cameraPos.x = playerController.gameObject.tf.position.x - 3*Settings.GRID_WIDTH;
                cameraPos.y = -projectionHeight;
                locked = true;
            }else{
                playerController.kill();
            }

        }

        if(playerController.hasWon || locked)return;

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
    }

    public void adjustGameCamera(){
        Vector2f playerAbs = new Vector2f(player.tf.position);
        cameraPos.x = playerAbs.x - sceneCamera.projectionWidth / 2f;
        cameraPos.y = (playerAbs.y > 0)?0:-projectionHeight;
    }
}
