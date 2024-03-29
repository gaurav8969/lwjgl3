package physics2D;

import components.Ground;
import components.PlayerController;
import components.Transform;
import contra.GameObject;

import contra.Window;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.joml.Vector2f;
import org.joml.Vector3f;
import physics2D.components.Box2DCollider;
import physics2D.components.CircleCollider;
import physics2D.components.PillboxCollider;
import physics2D.components.RigidBody2D;


public class Physics2D {
    private Vec2 gravity = new Vec2(0, -10.0f);
    private World world = new World(gravity);

    private float physicsTime = 0.0f;
    private float physicsTimeStep = 1.0f / 120.0f;
    private int velocityIterations = 8;
    private int positionIterations = 3;

    private boolean locked = false;
    public Physics2D(){
        world.setContactListener(new ContraContactListener());
    }

    public void add(GameObject go) {
        RigidBody2D rb = go.getComponent(RigidBody2D.class);
        if (rb != null && rb.getRawBody() == null) {
            Transform transform = go.tf;

            BodyDef bodyDef = new BodyDef();
            bodyDef.angle = (float)Math.toRadians(transform.rotation);
            bodyDef.position.set(transform.position.x, transform.position.y);
            bodyDef.angularDamping = rb.getAngularDamping();
            bodyDef.linearDamping = rb.getLinearDamping();
            bodyDef.fixedRotation = rb.isFixedRotation();
            bodyDef.bullet = rb.isContinuousCollision();
            bodyDef.gravityScale = rb.getGravityScale();
            bodyDef.angularVelocity = rb.getAngularVelocity();
            bodyDef.userData = rb.gameObject;

            switch (rb.getBodyType()) {
                case Kinematic: bodyDef.type = BodyType.KINEMATIC; break;
                case Static: bodyDef.type = BodyType.STATIC; break;
                case Dynamic: bodyDef.type = BodyType.DYNAMIC; break;
            }

            Body body = this.world.createBody(bodyDef);
            body.m_mass = rb.getMass();
            rb.setRawBody(body);

            CircleCollider circleCollider;
            Box2DCollider boxCollider;
            PillboxCollider pillboxCollider;

            if ((circleCollider = go.getComponent(CircleCollider.class)) != null) {
                addCircleCollider(rb, circleCollider);
            }

            if ((boxCollider = go.getComponent(Box2DCollider.class)) != null) {
                addBox2DCollider(rb, boxCollider);
            }

            if ((pillboxCollider = go.getComponent(PillboxCollider.class)) != null) {
                addPillboxCollider(rb, pillboxCollider);
            }
        }
    }

    public void destroyGameObject(GameObject go) {
        RigidBody2D rb = go.getComponent(RigidBody2D.class);
        if (rb != null) {
            if (rb.getRawBody() != null) {
                world.destroyBody(rb.getRawBody());
                rb.setRawBody(null);
            }
        }
    }

    public void update(float dt) {
        if(!locked) {
            physicsTime += dt;
            if (physicsTime >= 0.0f) {
                physicsTime -= physicsTimeStep;
                world.step(physicsTimeStep, velocityIterations, positionIterations);
            }
        }
    }

    public RaycastInfo raycast(GameObject requestingObject, Vector2f point1, Vector2f point2){
        RaycastInfo callback = new RaycastInfo(requestingObject);
        world.raycast(callback, new Vec2(point1.x,point1.y), new Vec2(point2.x, point2.y));
        return callback;
    }

    private void addBox2DCollider(RigidBody2D rb, Box2DCollider boxCollider){
        Body body = rb.getRawBody();
        assert  (body != null): "Raw body must not be null";

        PolygonShape shape = new PolygonShape();
        Vector2f halfSize = new Vector2f(boxCollider.getHalfSize());
        Vector2f offset = boxCollider.getOffset();
        shape.setAsBox(halfSize.x, halfSize.y, new Vec2(offset.x, offset.y), 0);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = rb.getFriction();
        fixtureDef.userData = boxCollider.gameObject;
        fixtureDef.isSensor = rb.isSensor();
        body.createFixture(fixtureDef);
    }

    private void addCircleCollider(RigidBody2D rb, CircleCollider circleCollider){
        Body body = rb.getRawBody();
        assert body != null : "Raw body must not be null";

        CircleShape shape = new CircleShape();
        shape.setRadius(circleCollider.getRadius());
        shape.m_p.set(circleCollider.getOffset().x, circleCollider.getOffset().y);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = rb.getFriction();
        fixtureDef.userData = circleCollider.gameObject;
        fixtureDef.isSensor = rb.isSensor();
        body.createFixture(fixtureDef);
    }

    public void addPillboxCollider(RigidBody2D rb, PillboxCollider pillbox){
        Body body = rb.getRawBody();
        assert body != null : "Raw body must not be null";

        addBox2DCollider(rb, pillbox.getBox());
        addCircleCollider(rb, pillbox.getTopCircle());
        addCircleCollider(rb, pillbox.getBottomCircle());
    }

    public void resetPillboxCollider(RigidBody2D rb, PillboxCollider pb){
        Body body = rb.getRawBody();
        if(body == null)return;

        int size = fixtureListSize(body);
        for(int i = 0; i < size; i++){
            body.destroyFixture(body.getFixtureList());
        }

        addPillboxCollider(rb, pb);
        body.resetMassData();
    }

    //tear down current collider and replace with box collider provided as argument
    public void resetBox2DCollider(RigidBody2D rb, Box2DCollider boxCollider){
        Body body = rb.getRawBody();
        if(body == null)return;

        int size = fixtureListSize(body);
        for(int i = 0; i < size; i++){
            body.destroyFixture(body.getFixtureList()); //destroy function also traverses the linked list
        }

        addBox2DCollider(rb, boxCollider);
        body.resetMassData();
    }

    //tear down current collider and replace with circle collider provided as argument
    public void resetCircleCollider(RigidBody2D rb, CircleCollider circleCollider){
        Body body = rb.getRawBody();
        if (body == null) return;

        int size = fixtureListSize(body);
        for (int i = 0; i < size; i++) {
            body.destroyFixture(body.getFixtureList());
        }

        addCircleCollider(rb, circleCollider);
        body.resetMassData();
    }

    private int fixtureListSize(Body body){
        int size = 0;
        Fixture fixture = body.getFixtureList();
        while(fixture != null){
            ++size;
            fixture = fixture.m_next;
        }
        return size;
    }

    public void setIsSensor(RigidBody2D rb){
        Body body = rb.getRawBody();
        if(body == null)return;

        Fixture fixture = body.getFixtureList();
        while(fixture != null){
            fixture.m_isSensor = true;
            fixture = fixture.m_next;
        }
    }

    public void setNotSensor(RigidBody2D rb){
        Body body = rb.getRawBody();
        if(body == null)return;

        Fixture fixture = body.getFixtureList();
        while(fixture != null){
            fixture.m_isSensor = false;
            fixture = fixture.m_next;
        }
    }

    public boolean checkOnGround(GameObject gameObject, float innerPlayerWidth, float torsoHeight){
        Vector2f rayCastLeftBegin = new Vector2f(gameObject.tf.position);
        rayCastLeftBegin.sub(innerPlayerWidth/2f, 0f);
        Vector2f rayCastLeftEnd = new Vector2f(rayCastLeftBegin).add(0f, torsoHeight);

        RaycastInfo infoLeft = Window.getPhysics().raycast(gameObject, rayCastLeftBegin, rayCastLeftEnd);

        Vector2f rayCastRightBegin = new Vector2f(rayCastLeftBegin).add(innerPlayerWidth, 0.0f);
        Vector2f rayCastRightEnd = new Vector2f(rayCastLeftEnd).add(innerPlayerWidth, 0.0f);
        RaycastInfo infoRight = Window.getPhysics().raycast(gameObject, rayCastRightBegin, rayCastRightEnd);

        boolean left = infoLeft.hit && infoLeft.hitObject != null && infoLeft.hitObject.getComponent(Ground.class) != null;
        boolean right = infoRight.hit && infoRight.hitObject != null && infoRight.hitObject.getComponent(Ground.class) != null;
        return left || right;
    }

    public Vector2f getGravity(){
        return new Vector2f(world.getGravity().x, world.getGravity().y);
    }

    public boolean isLocked(){
        return this.world.isLocked();
    }

    public void setLock(boolean lock){this.locked = lock;}
}