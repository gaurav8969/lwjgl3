package renderer;

import components.SpriteRenderer;
import components.Transform;
import contra.GameObject;
import contra.Window;
import org.joml.*;
import org.joml.Math;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

//render many sprites(quads) at once!
public class RenderBatch implements Comparable<RenderBatch>{
    boolean toRebuffer = true;
    private final int POS_SIZE = 2;
    private final int COLOR_SIZE = 4;
    private final int TEX_COORDS_SIZE = 2;
    private final int TEX_ID_SIZE = 1;
    private final int ENTITY_ID_SIZE = 1;

    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    private final int TEX_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
    private final int TEX_ID_OFFSET = TEX_COORDS_OFFSET + TEX_COORDS_SIZE * Float.BYTES;
    private final int ENTITY_ID_OFFSET = TEX_ID_OFFSET + TEX_ID_SIZE*Float.BYTES;
    private final int VERTEX_SIZE = 10;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private Renderer renderer;
    private SpriteRenderer[] sprites;
    private int numSprites;
    public boolean hasRoom;
    public boolean hasTextureRoom;
    private float[] vertices;
    private int[] texSlots = {0, 1, 2, 3, 4, 5, 6, 7};

    private List<Texture> textures;
    private int vaoID, vboID;
    private int maxBatchSize;
    private int maxTextureSize;
    private int zIndex;

    public RenderBatch(int maxBatchSize, int maximumTexturesSize, int zIndex, Renderer renderer) {
        this.renderer = renderer;
        this.sprites = new SpriteRenderer[maxBatchSize];
        // 4 vertices quads
        vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];

        this.numSprites = 0;
        this.hasRoom = true;
        this.hasTextureRoom = true;
        this.textures = new ArrayList<>();
        this.maxTextureSize = maximumTexturesSize;
        this.maxBatchSize = maxBatchSize;
        this.zIndex = zIndex;
    }

    public void init() {
        // Generate and bind a Vertex Array Object
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Allocate space for vertices
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // Create and upload indices buffer
        int eboID = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        //VAO is set like this, we can't "fill it up" like the vertices array
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, TEX_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_COORDS_OFFSET);
        glEnableVertexAttribArray(2);

        glVertexAttribPointer(3, TEX_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_ID_OFFSET);
        glEnableVertexAttribArray(3);

        glVertexAttribPointer(4, ENTITY_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, ENTITY_ID_OFFSET);
        glEnableVertexAttribArray(4);

        Shader shader = Renderer.getBoundShader();
        shader.uploadIntArray("uTextures", texSlots);
    }

    public void render() {
        for(int i = 0; i < numSprites; i++){
            SpriteRenderer spr = sprites[i];
            if(spr.isDirty()){
                if (!hasTexture(spr.getTexture())) {
                    this.renderer.destroyGameObject(spr.gameObject);
                    this.renderer.add(spr.gameObject);
                } else {
                    loadVertexProperties(i);
                    spr.makeClean();
                    toRebuffer = true;
                }
            }

            //spr z-index mismatches containing render batcher z-index, blasphemy!
            if(spr.zIndex() != this.zIndex){
                destroyIfExists(spr.gameObject);
                renderer.add(spr.gameObject);
            }
        }

        if(toRebuffer){
            // For now, we will rebuffer all data every frame
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
            toRebuffer = false;
        }

        Shader shader = Renderer.getBoundShader();
        shader.uploadMat4f("uProjection", Window.getScene().camera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().camera().getViewMatrix());
        shader.uploadIntArray("uTextures", texSlots);

        for (int i=0; i < textures.size(); i++) {
            glActiveTexture(GL_TEXTURE0 + i + 1);
            textures.get(i).bind();
        }

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(3);
        glEnableVertexAttribArray(4);

        glDrawElements(GL_TRIANGLES, this.numSprites * 6, GL_UNSIGNED_INT, 0);

        glEnableVertexAttribArray(4);
        glDisableVertexAttribArray(3);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);

        for (int i=0; i < textures.size(); i++) {
            textures.get(i).unbind();
        }
        shader.detach();
    }

    public boolean destroyIfExists(GameObject go){
        SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
        for(int i = 0; i < numSprites; i++){
            if(sprites[i] == spr){
                for(int j = i; j < numSprites; j++){
                    sprites[j] = sprites[j + 1];
                    if(sprites[j] != null){
                        sprites[j].setDirty(); //reload vertices and rebuffer(offloading)
                    }
                }
                numSprites--;
                return true;
            }
        }
        return false;
    }

    public void addSprite(SpriteRenderer spr) {
        //Get index and add renderObject
        int index = this.numSprites;
        this.sprites[index] = spr;
        this.numSprites++;

        if (spr.getSprite().getTexture() != null) {
            if (!textures.contains(spr.getSprite().getTexture())) {
                textures.add(spr.getSprite().getTexture());
            }
        }

        // Add properties to local vertices array
        loadVertexProperties(index);

        if ((numSprites >= this.maxBatchSize)) {
            this.hasRoom = false;
        }

        if(textures.size() >= maxTextureSize){
            this.hasTextureRoom = false;
        }

    }

    private void loadVertexProperties(int index) {
        SpriteRenderer spr = this.sprites[index];

        // Find offset within array (4 vertices per sprite)
        int offset = index * 4 * VERTEX_SIZE;

        Vector4f color = spr.getColour();
        Vector2f[] texCoords = spr.getSprite().getTexCoords();

        int texId = 0;
        if (spr.getSprite().getTexture() != null) {
            for (int i = 0; i < textures.size(); i++) {
                if (textures.get(i) == spr.getSprite().getTexture()){
                    texId = i + 1;
                    break;
                }
            }
        }

        Transform tf = spr.gameObject.tf;

        Matrix4f transformationMatrix = new Matrix4f().identity();
        transformationMatrix.translate(tf.position.x, tf.position.y, 0.0f);
        transformationMatrix.rotate(Math.toRadians(tf.rotation),0,0,1f);
        transformationMatrix.scale(tf.scale.x, tf.scale.y, 1f);

        // Add vertices with the appropriate properties
        float xAdd = 0.5f;
        float yAdd = 0.5f;
        for (int i=0; i < 4; i++) {
            if (i == 1) {
                yAdd = -0.5f;
            } else if (i == 2) {
                xAdd = -0.5f;
            } else if (i == 3) {
                yAdd = +0.5f;
            }

            Vector4f position = new Vector4f(xAdd, yAdd, 0,1);
            position.mul(transformationMatrix);

            // Load position
            vertices[offset] = position.x;
            vertices[offset + 1] = position.y;

            // Load color
            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;

            // Load texture coordinates
            vertices[offset + 6] = texCoords[i].x;
            vertices[offset + 7] = texCoords[i].y;

            // Load texture id
            vertices[offset + 8] = texId;

            //load unique game object id
            vertices[offset + 9] = spr.gameObject.getID() + 1;

            offset += VERTEX_SIZE;
        }
    }

    private int[] generateIndices() {
        // 6 indices per quad (3 per triangle)
        int[] elements = new int[6 * maxBatchSize];
        for (int i=0; i < maxBatchSize; i++) {
            loadElementIndices(elements, i);
        }

        return elements;
    }

    private void loadElementIndices(int[] elements, int index) {
        int offsetArrayIndex = 6 * index;
        int offset = 4 * index;

        // 3, 2, 0, 0, 2, 1        7, 6, 4, 4, 6, 5
        // Triangle 1
        elements[offsetArrayIndex] = offset + 3;
        elements[offsetArrayIndex + 1] = offset + 2;
        elements[offsetArrayIndex + 2] = offset + 0;

        // Triangle 2
        elements[offsetArrayIndex + 3] = offset + 0;
        elements[offsetArrayIndex + 4] = offset + 2;
        elements[offsetArrayIndex + 5] = offset + 1;
    }

    public boolean hasTexture(Texture tex){
        return textures.contains(tex);
    }

    public int zIndex(){
        return this.zIndex;
    }

    @Override
    public int compareTo(RenderBatch o) {
        return Integer.compare(this.zIndex, o.zIndex());
    }

}