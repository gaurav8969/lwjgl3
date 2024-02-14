package components;

import contra.GameObject;
import contra.Window;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Math;
import org.joml.Vector2f;
import physics2D.components.RigidBody2D;
import physics2D.enums.BodyType;
import util.AssetPool;
import util.Settings;

public class GoombaAI extends Component{
    private RigidBody2D rb;
    private boolean goRight = true;
    private transient float hitCoolDown = 0f;
    private transient float disintegrate = 2f;
    private transient boolean squash = false;
    private Vector2f velocity = new Vector2f();
    private Vector2f acceleration = new Vector2f();

    @Override
    public void init() {
        this.rb = this.gameObject.getComponent(RigidBody2D.class);
    }

    @Override
    public void update(float dt){
        if(!Window.getScene().camera().withinProjection(this.gameObject.tf.position))return;

        hitCoolDown -= dt;
        disintegrate -= dt;

        if(squash && disintegrate < 0f){
            this.gameObject.destroy();
        }

        if(!onGround()){
            acceleration.y = 7f;
        }else{
            velocity.y = 0f;
            acceleration.y = 0f;
        }

        velocity.y -= acceleration.y*dt;

        if(goRight){
            velocity.x = 0.9f;
        }else{
            velocity.x = -0.9f;
        }

        this.rb.setVelocity(velocity);
    }

    @Override
    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f contactNormal) {
        PlayerController playerController = collidingObject.getComponent(PlayerController.class);
        float yHitStrength = Math.abs(contactNormal.y);
        if(playerController != null){
            if(hitCoolDown < 0) {
                hitCoolDown = Settings.HURT_INVINCIBILITY;

                //make sure collision is horizontal
                if (yHitStrength < 0.5f && !squash) {
                    playerController.damage();
                }

                if (contactNormal.y > 0.58f) {
                    squash();
                    playerController.debounce(8);
                }
            }
        }

        if(yHitStrength < 0.1f){
            goRight = contactNormal.x < 0f;
        }
    }

    public void squash(){
        StateMachine stateMachine = this.gameObject.getComponent(StateMachine.class);
        stateMachine.trigger("squash");
        rb.setIsSensor();
        rb.setBodyType(BodyType.Static);
        velocity.x = 0f;
        squash = true;
        disintegrate = 1f;
        AssetPool.getSound("assets/sounds/stomp.ogg").play();
    }

    private boolean onGround(){
        float width = 0.25f;
        float yVal = -0.1355f;
        return Window.getPhysics().checkOnGround(this.gameObject, width, yVal);
    }
}