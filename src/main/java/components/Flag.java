package components;

import contra.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2D.components.RigidBody2D;
import util.AssetPool;
import util.Settings;

public class Flag extends Component{
    private transient float slideSpeed = 1.5f;
    private transient boolean slidingDown = false;
    private transient PlayerController playerController;
    private RigidBody2D rb;
    private boolean directionSwitched = false;
    private transient boolean switchSides = false;

    @Override
    public void init(){
        this.gameObject.tf.scale.x = -0.25f;
        rb = this.gameObject.getComponent(RigidBody2D.class);
    }

    @Override
    public void update(float dt){
        if(slidingDown){
            if(switchSides){
                playerController.gameObject.tf.scale.x *= -1;
                playerController.setPosition(new Vector2f(playerController.gameObject.tf.position).
                        sub(new Vector2f(1f*Settings.GRID_WIDTH, 0)));
                switchSides = false;
            }

            playerController.triggerAnimation("climb");
            this.gameObject.tf.position.y -= slideSpeed*dt;
            playerController.setVelocity(new Vector2f(0f, -slideSpeed));

            if(this.gameObject.tf.position.y < 3*Settings.GRID_HEIGHT && !directionSwitched){
                playerController.setPosition(new Vector2f(playerController.gameObject.tf.position).
                        add(new Vector2f(1.5f*Settings.GRID_WIDTH, 0)));
                playerController.gameObject.tf.scale.x *= -1;
                playerController.gravityScale = 0.7f;
                playerController.setVelocity(new Vector2f());
                directionSwitched = true;
            }

            if(this.gameObject.tf.position.y < 2.5* Settings.GRID_HEIGHT){
                playerController = null;
                slidingDown = false;
                AssetPool.getSound("assets/sounds/stage_clear.ogg").play();
                AssetPool.getSound("assets/sounds/main-theme-overworld.ogg").stop();
            }
        }
    }

    @Override
    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f contactNormal) {
        if(this.gameObject.tf.position.y < 2.5* Settings.GRID_HEIGHT || slidingDown)return;

        playerController = collidingObject.getComponent(PlayerController.class);

        if(playerController != null){
            if(playerController.gameObject.tf.scale.x < 0){
                switchSides = true;
            }
            playerController.hasWon = true;
            rb.setIsSensor();
            slidingDown = true;
            playerController.gravityScale = 0f;
            playerController.locked = true;
            playerController.hasWon = true;
            playerController.triggerAnimation("climb");
        }
    }
}
