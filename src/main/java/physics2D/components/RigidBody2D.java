package physics2D.components;

import components.Component;
import contra.GameObject;
import contra.Window;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2D.enums.BodyType;

import java.sql.SQLOutput;

public class RigidBody2D extends Component {
    private Vector2f velocity = new Vector2f();
    private float gravityScale = 1f;
    private float friction = 0.1f;
    private boolean isSensor = false;


    private float angularVelocity = 0f;

    private float angularDamping = 0.8f;
    private float linearDamping = 0.9f;
    private float mass = 0;
    private BodyType bodyType = BodyType.Dynamic;
    private boolean fixedRotation = false;

    private boolean continuousCollision = true;
    private transient Body rawBody = null;

    @Override
    public void update(float dt) {
        if (rawBody != null) {
            if (this.bodyType == BodyType.Dynamic || this.bodyType == BodyType.Kinematic) {
                this.gameObject.tf.position.set(
                        rawBody.getPosition().x, rawBody.getPosition().y
                );
                this.gameObject.tf.rotation = (float) Math.toDegrees(rawBody.getAngle());
                Vec2 vel = rawBody.getLinearVelocity();
                this.velocity.set(vel.x, vel.y);
            } else if (this.bodyType == BodyType.Static) {
                this.rawBody.setTransform(
                        new Vec2(this.gameObject.tf.position.x, this.gameObject.tf.position.y),
                        this.gameObject.tf.rotation
                );
            }
        }
    }

    public void addVelocity(Vector2f forceToAdd){
        if(rawBody != null){
            rawBody.applyForceToCenter(new Vec2(forceToAdd.x, forceToAdd.y));
        }
    }

    public void addImpulse(Vector2f impulse){
        if(rawBody != null){
            rawBody.applyLinearImpulse(new Vec2(impulse.x,impulse.y), rawBody.getWorldCenter());
        }
    }

    public boolean isSensor() {
        return isSensor;
    }

    //changes made to getter outputs won't be reflected in the world. Use the setters provided for that
    public Vector2f getVelocity() {
        return velocity;
    }

    public float getGravityScale() {
        return gravityScale;
    }

    public float getFriction() {
        return friction;
    }

    public float getAngularVelocity() {
        return angularVelocity;
    }

    public float getAngularDamping() {
        return angularDamping;
    }

    public float getLinearDamping() {
        return linearDamping;
    }

    public float getMass() {
        return mass;
    }

    public Body getRawBody() {
        return rawBody;
    }

    public BodyType getBodyType() {
        return bodyType;
    }

    //on this rigidbody, which means setting it for all its fixtures
    public void setIsSensor(){
        isSensor = true;
        if(rawBody != null){
            Window.getPhysics().setIsSensor(this);
        }
    }

    public void setNotSensor(){
        isSensor = false;
        if(rawBody != null){
            Window.getPhysics().setNotSensor(this);
        }
    }

    public void setBodyType(BodyType bodyType) {
        this.bodyType = bodyType;
    }

    public void setContinuousCollision(boolean continuousCollision){
        this.continuousCollision = continuousCollision;
    }

    public void setLinearDamping(float linearDamping) {
        this.linearDamping = linearDamping;
        if(rawBody != null){
            this.rawBody.setLinearDamping(linearDamping);
        }
    }

    public void setAngularDamping(float angularDamping) {
        this.angularDamping = angularDamping;
        if(rawBody != null){
            this.rawBody.setAngularDamping(angularDamping);
        }
    }

    public void setVelocity(Vector2f velocity) {
        this.velocity.set(velocity);
        if(rawBody != null){
            this.rawBody.setLinearVelocity(new Vec2(velocity.x,velocity.y));
        }
    }

    public void setAngularVelocity(float angularVelocity){
        this.angularVelocity = angularVelocity;
        if(rawBody != null){
            this.rawBody.setAngularVelocity(angularVelocity);
        }
    }

    public void setMass(float mass) {
        this.mass = mass;
        if(rawBody != null){
            this.rawBody.m_mass = mass;
        }
    }

    public void setFixedRotation(boolean fixedRotation) {
        this.fixedRotation = fixedRotation;
        if(rawBody != null){
            this.rawBody.setFixedRotation(fixedRotation);
        }
    }

    public void setGravityScale(float gravityScale) {
        this.gravityScale = gravityScale;
        if(rawBody != null){
            this.rawBody.setGravityScale(gravityScale);
        }
    }

    public void setRawBody(Body rawBody) {
        this.rawBody = rawBody;
    }

    //use when world isn't stepping
    public void setPosition(Vector2f pos){
        this.gameObject.tf.setPosition(pos);
        this.rawBody.setTransform(new Vec2(pos.x,pos.y), gameObject.tf.rotation);
    }

    public boolean isFixedRotation() {
        return fixedRotation;
    }

    public boolean isContinuousCollision() {
        return continuousCollision;
    }

    @Override
    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
        //System.out.println("Collision Taking place!");
    }
}