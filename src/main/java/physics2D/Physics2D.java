package physics2D;

import components.Transform;
import contra.GameObject;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import org.joml.Vector2f;
import physics2D.components.Box2DCollider;
import physics2D.components.CircleCollider;
import physics2D.components.RigidBody2D;

import static physics2D.enums.BodyType.Kinematic;
import static physics2D.enums.BodyType.Static;

public class Physics2D {
    private Vec2 gravity = new Vec2(0,-10.0f);
    private World world = new World(gravity);

    private float physicsTime = 0.0f;
    private final float physicsTimeStep = 1.0f/120.f;
    private final int velocityIterations = 8;
    private final int positionIterations = 3;

    //adding a gameobject to the JBox 2D engine
    public void add(GameObject go){
        RigidBody2D rb = go.getComponent(RigidBody2D.class);
        if(rb != null && rb.getRawBody() == null){
            Transform tf = go.tf;

            BodyDef bodyDef = new BodyDef();
            bodyDef.position.set(tf.position.x, tf.position.y);
            bodyDef.angle =  (float)Math.toRadians(tf.rotation);
            bodyDef.linearDamping = rb.getLinearDamping();
            bodyDef.angularDamping = rb.getAngularDamping();
            bodyDef.fixedRotation = rb.isFixedRotation();
            bodyDef.bullet = rb.isContinuousCollision();

            switch(rb.getBodyType()){
                case Kinematic: bodyDef.type = BodyType.KINEMATIC; break;
                case Static: bodyDef.type = BodyType.STATIC; break;
                case Dynamic: bodyDef.type = BodyType.DYNAMIC; break;
            }

            PolygonShape shape = new PolygonShape();
            CircleCollider circleCollider;
            Box2DCollider boxCollider;

            if((circleCollider = go.getComponent(CircleCollider.class)) != null){
                shape.setRadius(circleCollider.getRadius());
            }else if((boxCollider = go.getComponent(Box2DCollider.class)) != null){
                Vector2f halfSize = new Vector2f(boxCollider.getHalfSize().mul(0.5f, new Vector2f()));
                Vector2f offset = boxCollider.getOffset();
                Vector2f origin = new Vector2f(boxCollider.getOrigin());
                shape.setAsBox(halfSize.x, halfSize.y, new Vec2(origin.x, origin.y), 0);

                Vec2 pos = bodyDef.position;
                float xPos = pos.x + offset.x;
                float yPos = pos.y + offset.y;
                bodyDef.position.set(xPos,yPos);
            }

            Body body = this.world.createBody(bodyDef);
            rb.setRawBody(body);
            body.createFixture(shape, rb.getMass());
        }
    }
    public void update(float dt){
        physicsTime += dt;
        if(physicsTime > 0.0f){
            physicsTime -= physicsTimeStep;
            world.step(dt, velocityIterations, positionIterations);
        }
    }

    public void destroyGameObject(GameObject go){
        RigidBody2D rb = go.getComponent(RigidBody2D.class);
        if(rb != null){
            if(rb.getRawBody() != null){
                world.destroyBody(rb.getRawBody());
                rb.setRawBody(null);
            }
        }
    }
}
