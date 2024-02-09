package components;

import contra.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Math;
import org.joml.Vector2f;
import physics2D.components.RigidBody2D;
import physics2D.enums.BodyType;
import util.AssetPool;
import util.Settings;

public class GoombaAI extends Component{
    private RigidBody2D rb;
    private transient float velocity = 0.9f;
    private transient boolean goRight = true;
    private transient float hitCoolDown = 0f;
    private transient float disintegrate = 2f;
    private transient boolean squash = false;

    @Override
    public void init() {
        this.rb = this.gameObject.getComponent(RigidBody2D.class);
    }

    @Override
    public void update(float dt){
        hitCoolDown -= dt;
        disintegrate -= dt;

        if(squash && disintegrate < 0f){
            this.gameObject.destroy();
        }

        if(goRight){
            this.rb.setVelocity(new Vector2f(velocity,0f));
        }else{
            this.rb.setVelocity(new Vector2f(-velocity, 0f));
        }

    }

    @Override
    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f contactNormal) {
        PlayerController playerController = collidingObject.getComponent(PlayerController.class);
        float yHitStrength = Math.abs(contactNormal.y);
        if(playerController != null){
            if(hitCoolDown < 0) {
                hitCoolDown = Settings.HURT_INVINCIBILITY;

                //make sure collision is horizontal
                if (yHitStrength < 0.3f) {
                    playerController.damage();
                }

                if (contactNormal.y > 0.58f) {
                    squash();
                    AssetPool.getSound("assets/sounds/stomp.ogg").play();
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
        this.velocity = 0f;
        squash = true;
        disintegrate = 1f;
    }

}