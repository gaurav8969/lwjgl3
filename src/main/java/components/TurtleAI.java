package components;

import contra.GameObject;
import contra.Window;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Math;
import org.joml.Vector2f;
import physics2D.RaycastInfo;
import physics2D.components.RigidBody2D;

public class TurtleAI extends Component{
    private enum State{
        Scuttle,
        Hiding,
        Tremble,
        Dead
    }

    private boolean goingRight = false;
    private transient float turtleSpeed = 0.5f;
    private RigidBody2D rb;
    private StateMachine stateMachine;
    private State turtleState;
    private transient float zombieTime = 3f;
    private transient float rattle = 2f;
    private transient float haltDebounce = -1f;
    private PlayerController playerController;

    @Override
    public void init(){
        rb = this.gameObject.getComponent(RigidBody2D.class);
        stateMachine = this.gameObject.getComponent(StateMachine.class);
        this.turtleState = State.Scuttle;
    }

    @Override
    public void update(float dt){
        if(haltDebounce > 0){
            haltDebounce -= dt;
        }else{
            if(marioOnBack(playerController)){ // we only shoot this raycast once every second
                haltDebounce = 1f;
            }
        }

        if(turtleState == State.Hiding){
            zombieTime -= dt;
            if(zombieTime < 0){
                turtleState = State.Tremble;
                stateMachine.trigger("tremble");
            }
        }

        if(turtleState == State.Tremble){
            rattle -= dt;
            if(rattle < 0){
                turtleState = State.Scuttle;
                stateMachine.trigger("resurrect");
                turtleSpeed = 0.5f;
                //turtle is left in original spritesheet so scale.x < 0 means he is facing right
                goingRight = this.gameObject.tf.scale.x < 0;
                zombieTime = 3f;
                rattle = 2f;
            }
        }

        if(goingRight){
            rb.setVelocity(new Vector2f(turtleSpeed,0f));
        }else{
            rb.setVelocity(new Vector2f(-turtleSpeed,0f));
        }
    }

    @Override
    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f contactNormal) {
        playerController = collidingObject.getComponent(PlayerController.class);

        if(playerController != null) {
            if(turtleState == State.Scuttle) {
                if (contactNormal.y > 0.5f) {
                    turtleSpeed = 0f;
                    stateMachine.trigger("stomp");
                    playerController.debounce(16);
                    turtleState = State.Hiding;
                } else {
                    playerController.damage();
                    return;
                }
            }else if(turtleState == State.Dead){
                if(Math.abs(contactNormal.x) < 0.4f){
                    turtleSpeed = 0f;
                }else if(contactNormal.y < 0.1f && turtleSpeed > 0f && haltDebounce < 0f){
                    playerController.damage();
                    return;
                }
            }
        }

        GoombaAI goomba = collidingObject.getComponent(GoombaAI.class);
        if(turtleState == State.Dead && goomba != null){
            goomba.squash();
            return;
        }

        //dont switch directions on collision with ground tiles
        if (Math.abs(contactNormal.y) < 0.2f){
            goingRight = contactNormal.x < 0;
            this.gameObject.tf.scale.x *= -1;
        }

        //send shell racing across the floor, lynching goombas
        if(playerController != null && Math.abs(contactNormal.x) > 0.8f
                && haltDebounce < 0){
            stateMachine.trigger("die");
            turtleState = State.Dead;
            turtleSpeed = 1.5f;
            goingRight = contactNormal.x < 0;
            this.gameObject.tf.scale.x *= (goingRight)? -1:1;
        }
    }

    private boolean marioOnBack(PlayerController playerController){
        if(playerController == null)return false;

        Vector2f turtlePos = this.gameObject.tf.position;
        Vector2f back = new Vector2f(turtlePos).add(new Vector2f(0,playerController.gameObject.tf.scale.y));
        RaycastInfo onBack = Window.getPhysics().raycast(this.gameObject,
                turtlePos, back );

        return onBack.hit && onBack.hitObject.getComponent(PlayerController.class) != null;
    }
}
