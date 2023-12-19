package components;

import Renderer.Texture;
import contra.Component;
import contra.GameObject;
import org.joml.Vector2f;
import org.joml.Vector4f;

//contains sprites that are to be rendered
public class SpriteRenderer extends Component {

    private Vector4f colour;
    private Sprite sprite;
    private boolean isDirty = false;
    /*no setter here since transform is actually stored and controlled in game object, we
    maintain a copy of it that we update, and if we do, we raise the dirty flag*/
    private Transform lastTransform;

    public SpriteRenderer(Vector4f colour, Sprite sprite){
        this.sprite = sprite;
        this.colour = colour;
    }

    public SpriteRenderer(Vector4f colour){
        this.colour = colour;
        this.sprite = new Sprite(null);
    }

    public SpriteRenderer(Sprite sprite){
        this.colour = new Vector4f(1.0f,1.0f,1.0f,1.0f);
        this.sprite = sprite;
    }

    @Override
    public void init() {
        //must deep-copy since shallow copy is always up to date, rendering dirty flagging ineffective
        lastTransform = this.gameObject.getComponent(Transform.class).copy(); //deep-copy allocation
    }

    @Override
    public void update(float dt) {
        Transform tf = this.gameObject.getComponent(Transform.class);
        if(!lastTransform.equals(tf)){
            tf.copyTo(lastTransform); //no copy(shallow or deep)
            isDirty = true;
        }
    }

    public void setSprite(Sprite sprite){
        this.sprite = sprite;
        this.isDirty = true;
    }

    public void setColour(Vector4f colour){
        if( !this.colour.equals(colour)){
            this.colour.set(colour);
            this.isDirty = true;
        }
    }

    public Texture getTexture(){
        return sprite.getTexture();
    }

    public Vector2f[] getTextureCoords(){
        return sprite.getTexCoords();
    }

    public Vector4f getColour(){
        return this.colour;
    }

    public Sprite getSprite(){
        return this.sprite;
    }

    public boolean isDirty(){
        return this.isDirty;
    }
    public void makeClean(){
        this.isDirty= false;
    }

}