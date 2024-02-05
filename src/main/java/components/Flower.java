package components;

import contra.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2D.components.RigidBody2D;
import util.AssetPool;

public class Flower extends Component{
    private transient RigidBody2D rb;

    @Override
    public void init(){
        this.rb = this.gameObject.getComponent(RigidBody2D.class);
        AssetPool.getSound("assets/sounds/powerup_appears.ogg").play();
        rb.setIsSensor();
    }
    @Override
    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
        PlayerController playerController = collidingObject.getComponent(PlayerController.class);

        if(playerController != null){
            this.gameObject.destroy();
            playerController.powerUp();
        }
    }
}
