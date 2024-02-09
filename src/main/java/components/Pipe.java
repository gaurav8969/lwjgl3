package components;

import contra.*;
import editor.CImgui;
import imgui.ImGui;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2D.components.RigidBody2D;
import util.AssetPool;

import static org.lwjgl.glfw.GLFW.*;

public class Pipe extends Component{
    public Direction direction;
    private transient RigidBody2D rb;
    private String name;
    private String connectingPipeName;
    private boolean incoming = false;
    private static GameObject collidingPlayer;
    private static GameObject collidingPipe;
    private Vector2f contactNormal;
    private transient float pipeDowntime = 0.5f;
    private transient boolean entering;
    private boolean receiving = false;

    public Pipe(){}

    public Pipe(Direction direction){
        this.direction = direction;
        entering = false;
    }

    @Override
    public void init() {
        entering = false;
        this.rb = this.gameObject.getComponent(RigidBody2D.class);
    }

    @Override
    public void update(float dt) {
        if (collidingPipe == this.gameObject && incoming && collidingPlayer != null) {
            if(!entering){
                switch (this.direction) {
                    case Down: {
                        if (!KeyListener.isKeyPressed(GLFW_KEY_UP) || !(contactNormal.y < -0.5f)) {
                            return;
                        }
                        break;
                    }
                    case Up: {
                        if (!KeyListener.isKeyPressed(GLFW_KEY_DOWN) || !(contactNormal.x < 0.6f)) {
                            return;
                        }
                        break;
                    }
                    case Right: {
                        if (!KeyListener.isKeyPressed(GLFW_KEY_LEFT) || !(contactNormal.x > 0.5f)) {
                            return;
                        }
                        break;
                    }
                    case Left: {
                        if (!KeyListener.isKeyPressed(GLFW_KEY_RIGHT) || !(contactNormal.x < -0.5f)) {
                            return;
                        }
                        break;
                    }
                }

                entering = true;
                AssetPool.getSound("assets/sounds/pipe.ogg").play();
                collidingPlayer.getComponent(RigidBody2D.class).setIsSensor();
                this.gameObject.removeComponent(Ground.class);
                collidingPlayer.getComponent(SpriteRenderer.class).setZIndex(-20);
                PlayerController playerController = collidingPlayer.getComponent(PlayerController.class);
                playerController.gravityScale = 0f;
                playerController.friction = 0f;
                playerController.sliding = true;
                slide(direction, true);
            }

            if(entering){
                pipeDowntime -= dt;
            }

            if(pipeDowntime < 0) {
                entering = false;
                GameObject connectingPipe = Window.getScene().getGameObject(connectingPipeName);
                Pipe pipeComponent = connectingPipe.getComponent(Pipe.class);
                pipeComponent.receiving = true;

                Vector2f pipePos = connectingPipe.tf.position;
                RigidBody2D rbPlayer = collidingPlayer.getComponent(RigidBody2D.class);
                rbPlayer.setPosition(new Vector2f(pipePos.x, pipePos.y));

                Direction pipeDirection = connectingPipe.getComponent(Pipe.class).direction;
                slide(pipeDirection, false);
                pipeDowntime = 0.5f;
                this.gameObject.addComponent(new Ground());
                Window.getScene().getGameObject(GameCamera.class).getComponent(GameCamera.class).adjustGameCamera();
            }
        }
    }

    @Override
    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f contactNormal){
        PlayerController playerController = collidingObject.getComponent(PlayerController.class);

        if (playerController != null){
            if(receiving){
                this.gameObject.removeComponent(Ground.class);
            }

            collidingPipe = this.gameObject;
            collidingPlayer = playerController.gameObject;
            this.contactNormal = contactNormal;
        }
    }

    @Override
    public void endCollision(GameObject collidingObject, Contact contact, Vector2f contactNormal){
        if(collidingPlayer != null && receiving){
            receiving = false;
            collidingPlayer.getComponent(SpriteRenderer.class).setZIndex(0);
            collidingPlayer.getComponent(RigidBody2D.class).setNotSensor();
            collidingPlayer.getComponent(PlayerController.class).gravityScale = 0.7f;
            collidingPlayer.getComponent(PlayerController.class).friction = 0.05f;
            collidingObject.getComponent(PlayerController.class).sliding = false;
            this.gameObject.addComponent(new Ground());
            collidingPlayer = null;
        }
    }

    @Override
    public void imGui(){
        this.name = CImgui.inputText( "Name: ",
                name);
        this.gameObject.name = name;

        this.connectingPipeName = CImgui.inputText("Connecting pipe name: ", connectingPipeName);

        if(ImGui.checkbox("Incoming: ", incoming)){
            incoming = !incoming;
        }
    }

    private void slide(Direction direction, boolean enter){
        float slideDirection, slideSpeed;

        slideDirection = enter?-1:1;
        if(enter){
            slideSpeed = 0.3f;
        }else{
            slideSpeed = 1f;
        }

        PlayerController playerController = collidingPlayer.getComponent(PlayerController.class);
        switch (direction) {
            case Down: {
                playerController.setVelocity(new Vector2f(0,slideDirection*-1.8f));
                break;
            }
            case Up: {
                playerController.setVelocity(new Vector2f(0, slideDirection*1f));
                break;
            }
            case Right: {
                playerController.setVelocity(new Vector2f(slideDirection * slideSpeed, 0));
                if(!entering) {
                    playerController.gameObject.tf.scale.x = playerController.playerWidth;
                }
                break;
            }
            case Left: {
                playerController.setVelocity(new Vector2f(slideDirection * -slideSpeed, 0));
                if(!entering) {
                    playerController.gameObject.tf.scale.x = -playerController.playerWidth;
                }
                break;
            }
        }
    }
}