package components;

import contra.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Math;
import org.joml.Vector2f;
import physics2D.components.RigidBody2D;
import util.AssetPool;

public class MushroomAI extends Component{
    private transient boolean goRight = true;
    //vroom speed?
    private transient float shroomSpeed = 2f;
    private transient RigidBody2D rb;
    private transient boolean hitPlayer = false;

    @Override
    public void init(){
        rb = this.gameObject.getComponent(RigidBody2D.class);
        AssetPool.getSound("assets/sounds/powerup_appears.ogg").play();
    }

    @Override
    public void update(float dt){
        if(goRight){
            rb.setVelocity(new Vector2f(shroomSpeed, rb.getVelocity().y));
        }else{
            rb.setVelocity(new Vector2f(-shroomSpeed, rb.getVelocity().y));
        }
    }

    @Override
    public void preSolve(GameObject collidingObject, Contact contact, Vector2f contactNormal) {
        PlayerController playerController = collidingObject.getComponent(PlayerController.class);
        if(playerController != null){
            contact.setEnabled(false);
            if(!hitPlayer) {
                playerController.powerUp();
                this.gameObject.destroy();
                hitPlayer = true;
            }
        }else{
            if(Math.abs(contactNormal.y) < 0.1f) {
                goRight = contactNormal.x < 0;
            }
        }
    }
}