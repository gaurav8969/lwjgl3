package components;

import Renderer.Texture;
import contra.Component;
import contra.GameObject;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector4f;

//contains sprites that are to be rendered
public class SpriteRenderer extends Component {

    int zIndex= 0;//layer it is on, higher values go on top, try to use [-2,+2] range, default 0
    private Vector4f colour;
    private Sprite sprite;
    private boolean isDirty = false;
    /*no setter here since transform is actually stored and controlled in game object, we
    maintain a copy of it that we update, and if we do, we raise the dirty flag*/
    private Transform lastTransform;

    public SpriteRenderer(Sprite sprite, int zIndex,Vector4f colour){
        this.sprite = sprite;
        this.colour = colour;
        this.zIndex = zIndex;
        this.isDirty = true;
    }

    public SpriteRenderer(Sprite sprite){
        this.sprite = sprite;
        this.colour = new Vector4f(1,1,1,1);
        this.zIndex = 0;
        this.isDirty = true;
    }
    public SpriteRenderer(Vector4f colour){
        this.colour = colour;
        this.sprite = new Sprite(null);
        this.zIndex = 0;
        this.isDirty = true;
    }

    public SpriteRenderer(Sprite sprite, int zIndex){
        this.colour = new Vector4f(1.0f,1.0f,1.0f,1.0f);
        this.sprite = sprite;
        this.zIndex = zIndex;
        this.isDirty = true;
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

    public boolean isDirty(){
        return this.isDirty;
    }
    public void makeClean(){
        this.isDirty= false;
    }

    public Vector4f getColour(){
        return this.colour;
    }

    public int zIndex(){return zIndex;}
    public Sprite getSprite(){
        return this.sprite;
    }

    @Override
    public void imGui(){
        float[] imColour = {colour.x, colour.y, colour.z, colour.w};
        //ImGui calls below must take place in context of an imgui window,
        // i.e sandwiched by ImGui.begin() and ImGui.end() calls or program crashes
        if(ImGui.colorPicker4("Colour picker: ", imColour)){
            this.colour.set(imColour[0],imColour[1],imColour[2],imColour[3]);
            isDirty = true;
        }
    }
}