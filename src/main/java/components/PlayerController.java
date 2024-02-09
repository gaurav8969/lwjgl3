package components;

import contra.GameObject;
import contra.KeyListener;
import contra.Window;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import physics2D.RaycastInfo;
import physics2D.components.PillboxCollider;
import physics2D.components.RigidBody2D;
import physics2D.enums.BodyType;
import util.AssetPool;
import util.Settings;

import java.security.Key;

import static org.lwjgl.glfw.GLFW.*;

public class PlayerController extends Component{
    private enum PlayerState{
        Small,
        Big,
        Fire,
        Invincible,
        Die
    }

    public float walkSpeed = 1.9f;
    public float jumpBoost = 1.0f;
    public float jumpImpulse = 3.0f;
    public float friction = 0.05f;
    public float hurtInvincibility = 3f;
    public Vector2f terminalVelocity = new Vector2f(2.1f, 3.1f);
    public float gravityScale = 0.7f;

    private PlayerState playerState = PlayerState.Small;
    private transient  boolean hurt = false;
    public transient boolean onGround = false;
    private transient float groundDebounce = 0.0f;
    private transient float groundDebounceTime = 0.1f;
    private transient RigidBody2D rb;
    private transient StateMachine stateMachine;
    private transient float bigJumpBoostFactor = 1.05f;
    public transient float playerWidth = 0.25f;
    public transient int jumpTime = 0;
    private transient Vector2f acceleration = new Vector2f();
    private transient Vector2f velocity = new Vector2f();
    private boolean isDead = false;
    private transient int enemyDebounce = 0;
    private transient float deathMaxHeight = 0.34f;
    private transient boolean deathGoingUp = false;
    private transient float blinkDuration = 0.2f;
    public transient boolean sliding = false;


    @Override
    public void init(){
        this.rb = gameObject.getComponent(RigidBody2D.class);
        this.stateMachine = gameObject.getComponent(StateMachine.class);
        this.rb.setGravityScale(0.0f);
    }

    @Override
    public void update(float dt) {
        hurtInvincibility -= dt;
        blinkDuration -= dt;

        if(hurt){
            if(hurtInvincibility < 0){
                if(playerState == PlayerState.Die){
                    EventSystem.notify(null, new Event(EventType.LoadLevel));
                }
                hurt = false;
            }else if(playerState == PlayerState.Die) {
                    Vector2f marioPos = this.gameObject.tf.position;
                    if(deathGoingUp && marioPos.y < deathMaxHeight) {
                        marioPos.y += jumpImpulse/2 * dt;
                    }else if(marioPos.y > -0.5f){
                        deathGoingUp = false;
                        marioPos.y -= jumpImpulse * dt;
                    }
                return;
            }else{
                blink();
            }
        }

        if ((KeyListener.isKeyPressed(GLFW_KEY_RIGHT) || KeyListener.isKeyPressed(GLFW_KEY_D)) && !sliding) {
            this.gameObject.tf.scale.x = playerWidth;
            this.acceleration.x = walkSpeed;

            if (this.velocity.x < 0) {
                this.stateMachine.trigger("switchDirection");
                this.velocity.x += friction;
            } else{
                this.stateMachine.trigger("startRunning");
            }

        }else if((KeyListener.isKeyPressed(GLFW_KEY_LEFT) || KeyListener.isKeyPressed(GLFW_KEY_A)) && !sliding){
            //basically how we flip the object when changing directions
            this.gameObject.tf.scale.x = -playerWidth;
            this.acceleration.x = -walkSpeed;

            if(this.velocity.x > 0){
                this.stateMachine.trigger("switchDirection");
                this.velocity.x -= friction;
            }else{
                this.stateMachine.trigger("startRunning");
            }
        }else{
            //friction
            this.acceleration.x = 0;
            if(this.velocity.x > 0){
                this.velocity.x = Math.max(0, this.velocity.x - friction);
            }else if(this.velocity.x < 0){
                this.velocity.x = Math.min  (0, this.velocity.x + friction);
            }

            if(this.velocity.x == 0){
                this.stateMachine.trigger("stopRunning");
            }
        }

        checkOnGround();
        if (KeyListener.isKeyPressed(GLFW_KEY_SPACE) && (jumpTime > 0 || onGround || groundDebounce > 0)) {
            if ((onGround || groundDebounce > 0) && jumpTime == 0) {
                AssetPool.getSound("assets/sounds/jump-small.ogg").play();
                jumpTime = 35;
                this.velocity.y = jumpImpulse;
            } else if (jumpTime > 0) {
                jumpTime--;
                this.velocity.y = ((jumpTime) * jumpBoost);
            } else {
                this.velocity.y = 0;
            }
            groundDebounce = 0;
        }else if(enemyDebounce > 0){
            enemyDebounce--;
            this.velocity.y = (enemyDebounce)* jumpBoost*0.2f;
        }else if (!onGround) {
            if (this.jumpTime > 0) {
                this.velocity.y *= 0.35f;
                this.jumpTime = 0;
            }
            groundDebounce -= dt;
            this.acceleration.y = Window.getPhysics().getGravity().y * gravityScale;
        } else {
            this.velocity.y = 0;
            this.acceleration.y = 0;
            groundDebounce = groundDebounceTime;
        }

        this.velocity.x += this.acceleration.x*dt;
        this.velocity.y += this.acceleration.y*dt;

        this.velocity.x = Math.max(Math.min(this.velocity.x, this.terminalVelocity.x), -this.terminalVelocity.x);
        this.velocity.y = Math.max(Math.min(this.velocity.y, this.terminalVelocity.y), -this.terminalVelocity.y);
        this.rb.setVelocity(this.velocity);
        this.rb.setAngularVelocity(0);

        if (!onGround) {
            stateMachine.trigger("jump");
        } else {
            stateMachine.trigger("stopJumping");
        }
    }

    @Override
    public void editorUpdate(float dt){
        checkOnGround();
    }

    public void checkOnGround(){
        float innerPlayerWidth = this.playerWidth * 0.6f;
        float yVal = playerState == PlayerState.Small ? -0.14f: -0.221f;
        onGround = Window.getPhysics().checkOnGround(this.gameObject, innerPlayerWidth, yVal);
    }

    @Override
    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f contactNormal) {
        if (isDead) return;

        if (collidingObject.getComponent(Ground.class) != null) {
            if (Math.abs(contactNormal.x) > 0.8f){
                this.velocity.x = 0;
            }else if (contactNormal.y > 0.8f){
                this.velocity.y = 0;
                this.acceleration.y = 0;
                this.jumpTime = 0;
            }
        }

    }

    public boolean isSmall() {
        return this.playerState == PlayerState.Small;
    }

    public void powerUp(){
        if(playerState == PlayerState.Small){
            playerState = PlayerState.Big;
            gameObject.tf.scale.y = 0.42f;
            PillboxCollider pb = gameObject.getComponent(PillboxCollider.class);
            if(pb != null){
                jumpBoost *= bigJumpBoostFactor;
                walkSpeed *= bigJumpBoostFactor;
                pb.setHeight(0.42f);
            }
        }else if(playerState == PlayerState.Big){
            playerState = PlayerState.Fire;
        }

        AssetPool.getSound("assets/sounds/powerup.ogg").play();
        stateMachine.trigger("powerup");
    }

    public void damage(){
        hurt = true;
        hurtInvincibility = Settings.HURT_INVINCIBILITY;
        this.gameObject.getComponent(StateMachine.class).trigger("die");
        if(playerState == PlayerState.Small){
            playerState = PlayerState.Die;
            deathMaxHeight += this.gameObject.tf.position.y;
            rb.setBodyType(BodyType.Static);
            AssetPool.getSound("assets/sounds/mario_die.ogg").play();
            deathGoingUp = true;
            isDead = true;
            Window.getPhysics().setLock(true);
        }else{
            blinkDuration = 0f;
            playerState = PlayerState.Small;
            AssetPool.getSound("assets/sounds/pipe.ogg").play();
            gameObject.tf.scale.y = 0.25f;
            this.gameObject.getComponent(PillboxCollider.class).setHeight(0.25f);
        }
    }

    public void debounce(int debounce){
        this.enemyDebounce = debounce;
    }

    public void blink(){
        SpriteRenderer spr = this.gameObject.getComponent(SpriteRenderer.class);
        Vector4f col = spr.getColour();
        if(blinkDuration < 0f){
            blinkDuration = 0.15f;
            if(col.w > 0.8f){
                col.w = 0.8f;
            }else{
                col.w = 1f;
            }
        }

        if(hurtInvincibility < 0){
            col.w = 1;
        }

        spr.setDirty();
    }

    public void setVelocity(Vector2f vel){
        this.velocity.x = vel.x;
        this.velocity.y = vel.y;
    }

    public Vector2f getVelocity(){
        return this.velocity;
    }

    public boolean isDead(){
        return isDead;
    }
}