package components;

import contra.Window;
import editor.PropertiesWindow;
import imgui.internal.ImGui;
import renderer.Texture;
import editor.CImgui;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.AssetPool;

//contains sprites that are to be rendered
public class SpriteRenderer extends Component {
    private int zIndex= 0;//layer it is on, higher values go on top, try to use [-2,+2] range, default 0
    private Vector4f colour = new Vector4f(1,1,1,1);
    private Sprite sprite;
    private boolean isDirty = true;
    /*no setter here since transform is actually stored and controlled in game object, we
    maintain a copy of it that we update, and if we do, we raise the dirty flag*/
    private Transform lastTransform;

    @Override
    public void init() {
        if(this.sprite.getTexture() != null){
            this.sprite.setTexture(AssetPool.getTexture(this.sprite.getTexture().filepath));
        }
        //must deep-copy since shallow copy is always up to date, rendering dirty flagging ineffective
        lastTransform = this.gameObject.tf.copy(); //deep-copy allocation
    }

    //sprite to be marked dirty if its transform changes, in both editor and realtime play
    @Override
    public void update(float dt) {
        Transform tf = this.gameObject.tf;
        if(!lastTransform.equals(tf)){
            tf.copyTo(lastTransform); //no copy(shallow or deep)
            isDirty = true;
        }
    }

    @Override
    public void editorUpdate(float dt) {
        Transform tf = this.gameObject.tf;
        if(!lastTransform.equals(tf)){
            tf.copyTo(lastTransform); //no copy(shallow or deep)
            isDirty = true;
        }
    }

    public SpriteRenderer setSprite(Sprite sprite){
        this.sprite = sprite;
        this.isDirty = true;
        return this;
    }

    public SpriteRenderer setColour(Vector4f colour){
        if( !this.colour.equals(colour)){
            this.colour.set(colour);
            this.isDirty = true;
        }
        return this;
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

    public void setDirty(){this.isDirty = true;}
    public void makeClean(){
        this.isDirty= false;
    }

    public Vector4f getColour(){
        return this.colour;
    }

    public int zIndex(){return zIndex;}

    public SpriteRenderer setZIndex(int zIndex){
        isDirty = true;
        this.zIndex = zIndex;
        return this;
    }

    public Sprite getSprite(){
        return this.sprite;
    }

    public void setTexture(Texture texture){
        this.sprite.setTexture(texture);
    }

    @Override
    public void imGui(){
        Vector4f spriteCol = Window.getImGuilayer().getPropertiesWindow().getActiveColours().get(0);
        if(CImgui.colorPicker4("Colour picker: ", spriteCol)){
            colour.set(spriteCol);
            isDirty = true;
        }

        this.zIndex = CImgui.dragInt("Z-index", this.zIndex);
    }
}