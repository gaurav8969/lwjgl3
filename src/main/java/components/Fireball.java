package components;

import contra.GameObject;
import contra.Window;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import org.joml.Vector4f;
import physics2D.components.RigidBody2D;

public class Fireball extends Component{
    RigidBody2D rb;
    private boolean goingRight;
    private boolean onGround = false;
    private Vector2f velocity;
    private Vector2f acceleration;
    private transient float lifetime = 1.5f;
    public static int count = 0;

    @Override
    public void init(){
        count++;
        this.velocity = new Vector2f();
        this.acceleration = new Vector2f();
        rb = this.gameObject.getComponent(RigidBody2D.class);
        goingRight = Window.getScene().getGameObject(PlayerController.class).tf.scale.x > 0;
    }

    public void update(float dt){
        lifetime -= dt;
        if(lifetime < 0){
            this.gameObject.destroy();
            count--;
        }

        onGround = onGround();
        if(onGround){
            velocity.y = 2f;
            acceleration.y = 0f;
        }else{
            acceleration.y = 7f;
        }

        if(goingRight){
            velocity.x = +4f;
        }else{
            velocity.x = -4f;
        }

        velocity.y -= acceleration.y*dt;

        rb.setVelocity(velocity);
    }

    //so fireballs bounce
    private boolean onGround(){
        float width = 0.25f;
        float yVal = -0.1355f;
        return Window.getPhysics().checkOnGround(this.gameObject, width, yVal);
    }

    @Override
    public void preSolve(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
        if(collidingObject.getComponent(Ground.class) != null){
            return;
        }

        contact.setEnabled(false);

        GoombaAI goomba = collidingObject.getComponent(GoombaAI.class);
        if(goomba != null){
            goomba.squash();
        }

        TurtleAI turtle = collidingObject.getComponent(TurtleAI.class);
        if(turtle != null){
            turtle.kill();
        }
    }

    public static boolean isCrowded(){
        return count >= 4;
    }
}
