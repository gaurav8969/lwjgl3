package components;

import contra.GameObject;
import contra.Prefabs;
import contra.Window;

public class QuestionBlock extends Block {
    private enum BlockType {
        Coin,
        Powerup,
        Invincibility
    }

    public BlockType blockType = BlockType.Coin;

    @Override
    void playerHit(PlayerController playerController) {
        switch (blockType) {
            case Coin:
                doCoin(playerController);
                break;
            case Powerup:
                doPowerup(playerController);
                break;
            case Invincibility:
                doInvincibility(playerController);
                break;
        }

        StateMachine stateMachine = gameObject.getComponent(StateMachine.class);
        if (stateMachine != null) {
            stateMachine.trigger("setInactive");
            this.setInactive();
        }
    }

    private void doInvincibility(PlayerController playerController){
    }

    private void doPowerup(PlayerController playerController){
        if(playerController.isSmall()){
            spawnMushroom();
        }else{
            spawnFlower();
        }
    }

    private void doCoin(PlayerController playerController) {
        GameObject coin = Prefabs.generateBlockCoin();
        coin.tf.position.set(this.gameObject.tf.position);
        coin.tf.position.y += 0.25f;
        Window.getScene().addGameObjectToScene(coin);
    }

    private void spawnMushroom(){
        GameObject mushroom = Prefabs.generateMushroom();
        mushroom.tf.position.set(this.gameObject.tf.position);
        mushroom.tf.position.y += 0.25f;
        Window.getScene().addGameObjectToScene(mushroom);
    }

    private void spawnFlower(){
        GameObject flower = Prefabs.generateFlower();
        flower.tf.position.set(this.gameObject.tf.position);
        flower.tf.position.y += 0.25f;
        Window.getScene().addGameObjectToScene(flower);
    }
}