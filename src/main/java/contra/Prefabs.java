package contra;

import components.*;
import org.joml.Vector2f;
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
        GameObject mario = generateSpriteObject(playerSprites.getSprite(0),0.25f,0.25f );

        AnimationState run = new AnimationState();
        run.title = "Run";
        float defaultFrameTime = 0.23f;
        run.addFrame(playerSprites.getSprite(0), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(3), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        run.setLoop(true);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(run);
        stateMachine.setDefaultState(run.title);
        mario.addComponent(stateMachine);

        return mario;
    }

    public static GameObject generateQuestionBlock(){
        Spritesheet playerSprites = AssetPool.getSpriteSheet("assets/images/items.png");
        GameObject questionBlock = generateSpriteObject(playerSprites.getSprite(0), 0.25f,0.25f);
        AnimationState run = new AnimationState();
        run.title = "Question";
        float defaultFrameTime = 0.23f;
        run.addFrame(playerSprites.getSprite(0), 0.57f);
        run.addFrame(playerSprites.getSprite(1), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        run.setLoop(true);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(run);
        stateMachine.setDefaultState(run.title);
        questionBlock.addComponent(stateMachine);

        return questionBlock;
    }
}