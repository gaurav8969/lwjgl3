package contra;

import components.*;
import org.joml.Vector2f;
import physics2D.components.Box2DCollider;
import physics2D.components.CircleCollider;
import physics2D.components.PillboxCollider;
import physics2D.components.RigidBody2D;
import physics2D.enums.BodyType;
import util.AssetPool;

public class Prefabs {
    public static GameObject generateSpriteObject(Sprite sprite, float width, float height){
        GameObject go = Window.getScene().createGameObject("Sprite_Object_Gen_" + GameObject.IDCounter);
        go.tf.scale.x = width;
        go.tf.scale.y = height;
        go.addComponent(new SpriteRenderer().setSprite(sprite)); //z-index is 0
        return go;
    }

    public static GameObject generateMario(){
        Spritesheet playerSprites = AssetPool.getSpriteSheet("assets/images/characterSprites.png");
        Spritesheet bigPlayerSprites = AssetPool.getSpriteSheet("assets/images/bigSpritesheet.png");
        GameObject mario = generateSpriteObject(playerSprites.getSprite(0),0.25f,0.25f );

        //mario.setName("Mario");
        //little(sprite size) mario animations
        AnimationState run = new AnimationState();
        run.title = "Run";
        float defaultFrameTime = 0.2f;
        run.addFrame(playerSprites.getSprite(0), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(3), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        run.setLoop(true);

        AnimationState switchDirection = new AnimationState();
        switchDirection.title = "Switch Direction";
        switchDirection.addFrame(playerSprites.getSprite(4), 0.1f);
        switchDirection.setLoop(false);

        AnimationState idle = new AnimationState();
        idle.title = "Idle";
        idle.addFrame(playerSprites.getSprite(0), 0.1f);
        idle.setLoop(false);

        AnimationState jump = new AnimationState();
        jump.title = "Jump";
        jump.addFrame(playerSprites.getSprite(5), 0.1f);
        jump.setLoop(false);

        // Big mario animations
        AnimationState bigRun = new AnimationState();
        bigRun.title = "BigRun";
        bigRun.addFrame(bigPlayerSprites.getSprite(0), defaultFrameTime);
        bigRun.addFrame(bigPlayerSprites.getSprite(1), defaultFrameTime);
        bigRun.addFrame(bigPlayerSprites.getSprite(2), defaultFrameTime);
        bigRun.addFrame(bigPlayerSprites.getSprite(3), defaultFrameTime);
        bigRun.addFrame(bigPlayerSprites.getSprite(2), defaultFrameTime);
        bigRun.addFrame(bigPlayerSprites.getSprite(1), defaultFrameTime);
        bigRun.setLoop(true);

        AnimationState bigSwitchDirection = new AnimationState();
        bigSwitchDirection.title = "Big Switch Direction";
        bigSwitchDirection.addFrame(bigPlayerSprites.getSprite(4), 0.1f);
        bigSwitchDirection.setLoop(false);

        AnimationState bigIdle = new AnimationState();
        bigIdle.title = "BigIdle";
        bigIdle.addFrame(bigPlayerSprites.getSprite(0), 0.1f);
        bigIdle.setLoop(false);

        AnimationState bigJump = new AnimationState();
        bigJump.title = "BigJump";
        bigJump.addFrame(bigPlayerSprites.getSprite(5), 0.1f);
        bigJump.setLoop(false);

        // Fire mario animations
        int fireOffset = 21;
        AnimationState fireRun = new AnimationState();
        fireRun.title = "FireRun";
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 0), defaultFrameTime);
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 1), defaultFrameTime);
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 2), defaultFrameTime);
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 3), defaultFrameTime);
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 2), defaultFrameTime);
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 1), defaultFrameTime);
        fireRun.setLoop(true);

        AnimationState fireSwitchDirection = new AnimationState();
        fireSwitchDirection.title = "Fire Switch Direction";
        fireSwitchDirection.addFrame(bigPlayerSprites.getSprite(fireOffset + 4), 0.1f);
        fireSwitchDirection.setLoop(false);

        AnimationState fireIdle = new AnimationState();
        fireIdle.title = "FireIdle";
        fireIdle.addFrame(bigPlayerSprites.getSprite(fireOffset + 0), 0.1f);
        fireIdle.setLoop(false);

        AnimationState fireJump = new AnimationState();
        fireJump.title = "FireJump";
        fireJump.addFrame(bigPlayerSprites.getSprite(fireOffset + 5), 0.1f);
        fireJump.setLoop(false);

        AnimationState die = new AnimationState();
        die.title = "Die";
        die.addFrame(playerSprites.getSprite(6), 0.1f);
        die.setLoop(false);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(run);
        stateMachine.addState(idle);
        stateMachine.addState(switchDirection);
        stateMachine.addState(jump);
        stateMachine.addState(die);

        stateMachine.addState(bigRun);
        stateMachine.addState(bigIdle);
        stateMachine.addState(bigSwitchDirection);
        stateMachine.addState(bigJump);

        stateMachine.addState(fireRun);
        stateMachine.addState(fireIdle);
        stateMachine.addState(fireSwitchDirection);
        stateMachine.addState(fireJump);

        stateMachine.setDefaultState(idle.title);
        stateMachine.addTrigger(run.title, switchDirection.title, "switchDirection");
        stateMachine.addTrigger(run.title, idle.title, "stopRunning");
        stateMachine.addTrigger(run.title, jump.title, "jump");
        stateMachine.addTrigger(switchDirection.title, idle.title, "stopRunning");
        stateMachine.addTrigger(switchDirection.title, run.title, "startRunning");
        stateMachine.addTrigger(switchDirection.title, jump.title, "jump");
        stateMachine.addTrigger(idle.title, run.title, "startRunning");
        stateMachine.addTrigger(idle.title, jump.title, "jump");
        stateMachine.addTrigger(jump.title, idle.title, "stopJumping");

        stateMachine.addTrigger(bigRun.title, bigSwitchDirection.title, "switchDirection");
        stateMachine.addTrigger(bigRun.title, bigIdle.title, "stopRunning");
        stateMachine.addTrigger(bigRun.title, bigJump.title, "jump");
        stateMachine.addTrigger(bigSwitchDirection.title, bigIdle.title, "stopRunning");
        stateMachine.addTrigger(bigSwitchDirection.title, bigRun.title, "startRunning");
        stateMachine.addTrigger(bigSwitchDirection.title, bigJump.title, "jump");
        stateMachine.addTrigger(bigIdle.title, bigRun.title, "startRunning");
        stateMachine.addTrigger(bigIdle.title, bigJump.title, "jump");
        stateMachine.addTrigger(bigJump.title, bigIdle.title, "stopJumping");

        stateMachine.addTrigger(fireRun.title, fireSwitchDirection.title, "switchDirection");
        stateMachine.addTrigger(fireRun.title, fireIdle.title, "stopRunning");
        stateMachine.addTrigger(fireRun.title, fireJump.title, "jump");
        stateMachine.addTrigger(fireSwitchDirection.title, fireIdle.title, "stopRunning");
        stateMachine.addTrigger(fireSwitchDirection.title, fireRun.title, "startRunning");
        stateMachine.addTrigger(fireSwitchDirection.title, fireJump.title, "jump");
        stateMachine.addTrigger(fireIdle.title, fireRun.title, "startRunning");
        stateMachine.addTrigger(fireIdle.title, fireJump.title, "jump");
        stateMachine.addTrigger(fireJump.title, fireIdle.title, "stopJumping");

        stateMachine.addTrigger(run.title, bigRun.title, "powerup");
        stateMachine.addTrigger(idle.title, bigIdle.title, "powerup");
        stateMachine.addTrigger(switchDirection.title, bigSwitchDirection.title, "powerup");
        stateMachine.addTrigger(jump.title, bigJump.title, "powerup");
        stateMachine.addTrigger(bigRun.title, fireRun.title, "powerup");
        stateMachine.addTrigger(bigIdle.title, fireIdle.title, "powerup");
        stateMachine.addTrigger(bigSwitchDirection.title, fireSwitchDirection.title, "powerup");
        stateMachine.addTrigger(bigJump.title, fireJump.title, "powerup");

        stateMachine.addTrigger(bigRun.title, run.title, "damage");
        stateMachine.addTrigger(bigIdle.title, idle.title, "damage");
        stateMachine.addTrigger(bigSwitchDirection.title, switchDirection.title, "damage");
        stateMachine.addTrigger(bigJump.title, jump.title, "damage");
        stateMachine.addTrigger(fireRun.title, bigRun.title, "damage");
        stateMachine.addTrigger(fireIdle.title, bigIdle.title, "damage");
        stateMachine.addTrigger(fireSwitchDirection.title, bigSwitchDirection.title, "damage");
        stateMachine.addTrigger(fireJump.title, bigJump.title, "damage");

        stateMachine.addTrigger(run.title, die.title, "die");
        stateMachine.addTrigger(switchDirection.title, die.title, "die");
        stateMachine.addTrigger(idle.title, die.title, "die");
        stateMachine.addTrigger(jump.title, die.title, "die");
        stateMachine.addTrigger(bigRun.title, run.title, "die");
        stateMachine.addTrigger(bigSwitchDirection.title, switchDirection.title, "die");
        stateMachine.addTrigger(bigIdle.title, idle.title, "die");
        stateMachine.addTrigger(bigJump.title, jump.title, "die");
        stateMachine.addTrigger(fireRun.title, bigRun.title, "die");
        stateMachine.addTrigger(fireSwitchDirection.title, bigSwitchDirection.title, "die");
        stateMachine.addTrigger(fireIdle.title, bigIdle.title, "die");
        stateMachine.addTrigger(fireJump.title, bigJump.title, "die");

        mario.addComponent(stateMachine);

        PillboxCollider pb = new PillboxCollider();
        RigidBody2D rb = new RigidBody2D();
        rb.setBodyType(BodyType.Dynamic);
        rb.setContinuousCollision(true);
        rb.setFixedRotation(true);
        rb.setMass(25.0f);

        mario.addComponent(rb);
        mario.addComponent(pb);
        mario.addComponent(new PlayerController());

        return mario;
    }

    public static GameObject generateQuestionBlock(){
        Spritesheet itemSprites = AssetPool.getSpriteSheet("assets/images/items.png");
        GameObject questionBlock = generateSpriteObject(itemSprites.getSprite(0), 0.25f,0.25f);
        AnimationState flicker = new AnimationState();
        flicker.title = "Flicker";
        float defaultFrameTime = 0.23f;
        flicker.addFrame(itemSprites.getSprite(0), 0.57f);
        flicker.addFrame(itemSprites.getSprite(1), defaultFrameTime);
        flicker.addFrame(itemSprites.getSprite(2), defaultFrameTime);
        flicker.setLoop(true);

        AnimationState inactive = new AnimationState();
        inactive.title = "Inactive";
        inactive.addFrame(itemSprites.getSprite(3), 0.1f);
        inactive.setLoop(false);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(flicker);
        stateMachine.addState(inactive);
        stateMachine.setDefaultState(flicker.title);
        stateMachine.addTrigger(flicker.title,inactive.title,"setInactive");
        questionBlock.addComponent(stateMachine);
        questionBlock.addComponent(new QuestionBlock());

        RigidBody2D rb = new RigidBody2D();
        rb.setBodyType(BodyType.Static);
        questionBlock.addComponent(rb);
        Box2DCollider b2d = new Box2DCollider();
        b2d.setHalfSize(new Vector2f(0.125f, 0.125f));
        questionBlock.addComponent(b2d);
        questionBlock.addComponent(new Ground());

        return questionBlock;
    }

    public static GameObject generateBlockCoin() {
        Spritesheet items = AssetPool.getSpriteSheet("assets/images/items.png");
        GameObject coin = generateSpriteObject(items.getSprite(7), 0.25f, 0.25f);

        AnimationState coinFlip = new AnimationState();
        coinFlip.title = "CoinFlip";
        float defaultFrameTime = 0.23f;
        coinFlip.addFrame(items.getSprite(7), 0.57f);
        coinFlip.addFrame(items.getSprite(8), defaultFrameTime);
        coinFlip.addFrame(items.getSprite(9), defaultFrameTime);
        coinFlip.setLoop(true);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(coinFlip);
        stateMachine.setDefaultState(coinFlip.title);
        coin.addComponent(stateMachine);
        coin.addComponent(new QuestionBlock());

        coin.addComponent(new BlockCoin());

        return coin;
    }

    public static GameObject generateMushroom() {
        Spritesheet items = AssetPool.getSpriteSheet("assets/images/items.png");
        GameObject mushroom = generateSpriteObject(items.getSprite(10), 0.25f, 0.25f);

        RigidBody2D rb = new RigidBody2D();
        rb.setBodyType(BodyType.Dynamic);
        rb.setFixedRotation(true);
        rb.setContinuousCollision(false);
        mushroom.addComponent(rb);

        CircleCollider circleCollider = new CircleCollider();
        circleCollider.setRadius(0.14f);
        mushroom.addComponent(circleCollider);
        mushroom.addComponent(new MushroomAI());
        mushroom.addComponent(new BreakableBrick());

        return mushroom;
    }

    public static GameObject generateFlower() {
        Spritesheet items = AssetPool.getSpriteSheet("assets/images/items.png");
        GameObject flower = generateSpriteObject(items.getSprite(20), 0.25f, 0.25f);

        RigidBody2D rb = new RigidBody2D();
        rb.setBodyType(BodyType.Static);
        rb.setFixedRotation(true);
        rb.setContinuousCollision(false);
        flower.addComponent(rb);

        CircleCollider circleCollider = new CircleCollider();
        circleCollider.setRadius(0.14f);
        flower.addComponent(circleCollider);
        flower.addComponent(new Flower());

        return flower;
    }
}