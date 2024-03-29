package physics2D.components;

import components.Component;
import contra.Window;
import org.joml.Vector2f;

public class PillboxCollider extends Component {
    private transient CircleCollider topCircle = new CircleCollider();
    private transient CircleCollider bottomCircle = new CircleCollider();
    private transient Box2DCollider box = new Box2DCollider();
    private transient boolean resetFixtureNextFrame = false;

    private transient float width;
    private transient float height;
    public Vector2f offset;

    public PillboxCollider(){
        this.width = 0.2f;
        this.height = 0.25f;
        this.offset = new Vector2f();
    }

    @Override
    public void init(){
        this.topCircle.gameObject = this.gameObject;
        this.bottomCircle.gameObject = this.gameObject;
        this.box.gameObject = this.gameObject;
        recalculateColliders();
    }

    @Override
    public void update(float dt) {
        if(resetFixtureNextFrame){
            resetfixture();
        }
    }

    @Override
    public void editorUpdate(float dt) {
        topCircle.editorUpdate(dt);
        bottomCircle.editorUpdate(dt);
        box.editorUpdate(dt);

        if(resetFixtureNextFrame){
            resetfixture();
        }
    }

    public void resetfixture(){
        if(Window.getPhysics().isLocked()){
            resetFixtureNextFrame = true;
            return;
        }
        resetFixtureNextFrame = false;

        if(gameObject != null){
            RigidBody2D rb = gameObject.getComponent(RigidBody2D.class);
            if(rb != null){
                Window.getPhysics().resetPillboxCollider(rb, this);
            }
        }

    }

    public void recalculateColliders(){
        float circleRadius = this.width/2;
        float boxHeight = height - 2*circleRadius;
        topCircle.setRadius(circleRadius);
        bottomCircle.setRadius(circleRadius);
        topCircle.setOffset(new Vector2f(offset).add(0, boxHeight/2.0f));
        bottomCircle.setOffset(new Vector2f(offset).sub(0, boxHeight/2.0f));
        box.setHalfSize(new Vector2f(circleRadius,boxHeight/2.0f));
        box.setOffset(offset);
    }

    public CircleCollider getTopCircle() {
        return topCircle;
    }

    public CircleCollider getBottomCircle() {
        return bottomCircle;
    }

    public Box2DCollider getBox() {
        return box;
    }

    public void setHeight(float height){
        this.height = height;
        recalculateColliders();
        resetfixture();
    }
}
