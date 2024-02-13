package components;

import contra.*;
import editor.CImgui;
import imgui.ImGui;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import org.joml.Vector4f;
import physics2D.RaycastInfo;
import physics2D.components.RigidBody2D;
import util.AssetPool;

import static org.lwjgl.glfw.GLFW.*;

public class Pipe extends Component{
    public Direction direction;
    private String name;
    private String connectingPipeName;
    private boolean incoming = false;
    private static GameObject collidingPlayer;
    private transient float pipeDowntime = 0.5f;
    private transient boolean entering;
    private transient boolean receiving = false;
    private transient float receiveDebounce = 0.4f;
    private static boolean underground = false;

    public Pipe(){}

    public Pipe(Direction direction){
        this.direction = direction;
        entering = false;
    }

    @Override
    public void init() {
        collidingPlayer = Window.getScene().getGameObject(PlayerController.class);
        entering = false;
    }

    @Override
    public void update(float dt) {
        if(collidingPlayer == null)return;

        if (incoming && !receiving){
            if(!entering){
                switch (this.direction) {
                    case Down: {
                        if (!KeyListener.isKeyPressed(GLFW_KEY_UP)) {
                            return;
                        }
                        break;
                    }
                    case Up: {
                        if (!KeyListener.isKeyPressed(GLFW_KEY_DOWN)) {
                            return;
                        }
                        break;
                    }
                    case Right: {
                        if (!KeyListener.isKeyPressed(GLFW_KEY_LEFT)) {
                            return;
                        }
                        break;
                    }
                    case Left: {
                        if (!KeyListener.isKeyPressed(GLFW_KEY_RIGHT)) {
                            return;
                        }
                        break;
                    }
                }

                if(!atEntrance())return;

                entering = true;
                AssetPool.getSound("assets/sounds/pipe.ogg").play();
                collidingPlayer.getComponent(RigidBody2D.class).setIsSensor();
                this.gameObject.removeComponent(Ground.class);
                collidingPlayer.getComponent(SpriteRenderer.class).setZIndex(-20);
                PlayerController playerController = collidingPlayer.getComponent(PlayerController.class);
                playerController.gravityScale = 0f;
                playerController.friction = 0f;
                playerController.locked = true;
                slide(direction, true);
            }

            if(entering){
                pipeDowntime -= dt;
            }

            if(pipeDowntime < 0) {
                entering = false;
                GameObject connectingPipe = Window.getScene().getGameObject(connectingPipeName);
                Pipe pipeComponent = connectingPipe.getComponent(Pipe.class);
                if(connectingPipeName.equals("underground0")){
                    underground = true;
                }

                if(connectingPipeName.equals("ground01")){
                    underground = false;
                }

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
        }else if(receiving){
            this.gameObject.removeComponent(Ground.class);
            receiveDebounce -= dt;

            if(receiveDebounce < 0) {
                receiveDebounce = 0.4f;
                receiving = false;
                collidingPlayer.getComponent(SpriteRenderer.class).setZIndex(0);
                collidingPlayer.getComponent(RigidBody2D.class).setNotSensor();
                collidingPlayer.getComponent(PlayerController.class).gravityScale = 0.7f;
                collidingPlayer.getComponent(PlayerController.class).friction = 0.05f;
                collidingPlayer.getComponent(PlayerController.class).locked = false;
                this.gameObject.addComponent(new Ground());
            }
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
                playerController.setVelocity(new Vector2f(0,slideDirection*-1f));
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

    private boolean atEntrance(){
        switch (direction) {
            case Down:{
                //raytracing downwards
                Vector2f rayStart = new Vector2f(gameObject.tf.position.x,
                        gameObject.tf.position.y - this.gameObject.tf.scale.y * 0.5f);
                Vector2f rayEnd = new Vector2f(rayStart.x, rayStart.y - collidingPlayer.tf.scale.y);

                RaycastInfo obj = Window.getPhysics().raycast(this.gameObject,
                        rayStart, rayEnd);

                return obj.hit && obj.hitObject.getComponent(PlayerController.class) != null;
            }

            case Up:{
                //raytracing downwards
                Vector2f rayStart = new Vector2f(gameObject.tf.position.x,
                        gameObject.tf.position.y + this.gameObject.tf.scale.y*0.5f);
                Vector2f rayEnd = new Vector2f(rayStart.x, rayStart.y + collidingPlayer.tf.scale.y);

                RaycastInfo obj = Window.getPhysics().raycast(this.gameObject,
                        rayStart, rayEnd);

                return obj.hit && obj.hitObject.getComponent(PlayerController.class) != null;
            }

            case Right: {
                //raytracing rightwards
                Vector2f rayStart = new Vector2f(gameObject.tf.position.x + gameObject.tf.scale.x*0.5f,
                        gameObject.tf.position.y);
                Vector2f rayEnd = new Vector2f(rayStart.x + Math.abs(collidingPlayer.tf.scale.x*0.6f), rayStart.y);

                RaycastInfo obj = Window.getPhysics().raycast(this.gameObject,
                        rayStart, rayEnd);

                return obj.hit && obj.hitObject.getComponent(PlayerController.class) != null;
            }

            case Left: {
                //raytracing rightwards
                Vector2f rayStart = new Vector2f(gameObject.tf.position.x - this.gameObject.tf.scale.x*0.5f,
                        gameObject.tf.position.y);
                Vector2f rayEnd = new Vector2f(rayStart.x - Math.abs(collidingPlayer.tf.scale.x * 0.6f), rayStart.y);

                RaycastInfo obj = Window.getPhysics().raycast(this.gameObject,
                        rayStart, rayEnd);

                return obj.hit && obj.hitObject.getComponent(PlayerController.class) != null;
            }
        }
        return false;
    }

    public static boolean isUnderground(){
        return underground;
    }
}