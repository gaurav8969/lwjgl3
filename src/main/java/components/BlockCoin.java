package components;

import org.joml.Vector2f;
import util.AssetPool;

public class BlockCoin extends Component{
    private Vector2f topY;
    private float coinSpeed = 1.4f; //adjust top y and coin speed to allow the coin to flip nicely

    @Override
    public void init(){
        topY = new Vector2f(this.gameObject.tf.position.y).add(0, 0.5f);
        AssetPool.getSound("assets/sounds/coin.ogg").play();
    }

    @Override
    public void update(float dt){
        if(this.gameObject.tf.position.y < topY.y){
            this.gameObject.tf.position.y += coinSpeed*dt;
            //gives the appearance of a coin flip
            this.gameObject.tf.scale.x -= (0.5f*dt);
        }else{
            gameObject.destroy();
        }
    }
}
