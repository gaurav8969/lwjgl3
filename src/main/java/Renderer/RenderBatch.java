package Renderer;

import components.SpriteRenderer;
import components.Transform;
import contra.Window;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

//render many sprites(quads) at once!
public class RenderBatch {
    private final int POS_SIZE= 2;
    private final int COLOUR_SIZE= 4;

    private final int POS_OFFSET= 0;
    private final int COLOUR_OFFSET = POS_OFFSET + POS_SIZE* Float.BYTES;
    private final int VERTEX_SIZE = 6;
    private final int VERTEX_SIZE_BYTES= VERTEX_SIZE* Float.BYTES;

    private SpriteRenderer[] sprites;
    private int numSprites;
    public boolean hasRoom;
    private float[] vertices;

    private int vaoID, vboID;
    private int maxBatchSize;
    private Shader shader;

    public RenderBatch(int maxBatchSize){
        shader = new Shader("assets/shaders/default.glsl");
        shader.compile();
        this.sprites = new SpriteRenderer[maxBatchSize];
        this.maxBatchSize = maxBatchSize;

        //4 vertices quads
        vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];

        this.numSprites = 0;
        this.hasRoom = true;
    }

    public void init(){
        //Generate and bind a Vertec array object
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        //Allocate space for vertices
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES,  GL_DYNAMIC_DRAW);

        int eboID = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        //indices tell opengl how to interpret the unique vertices to avoid repetition
        //static since the manner doesn't have to be changed afterwards
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        //enable buffer for attribute pointers
        glVertexAttribPointer(0,POS_SIZE,GL_FLOAT,false,VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOUR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOUR_OFFSET);
        glEnableVertexAttribArray(1);
    }

    public void render(){
        //for now, we rebuffer all data every frame
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER,0,vertices);

        shader.use();
        shader.uploadMat4f("uProjection", Window.getScene().camera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().camera().getViewMatrix());

        /*the vao object contains pointers we setup in the init function,
        we just enable them before rendering here*/
        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES,this.numSprites*6,GL_UNSIGNED_INT,0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        shader.detach();
    }
    public void addSprite(SpriteRenderer sprite){
        int index = numSprites;
        sprites[index] = sprite;
        numSprites++;

        //add properties to local vertices array, colour and pos
        loadVertexProperty(index);

        if(numSprites >= maxBatchSize){
            hasRoom = false;
        }
    }

    public void loadVertexProperty(int index){
        SpriteRenderer sprite = this.sprites[index];
        Transform tf = sprite.gameObject.getComponent(Transform.class);

        //index within array
        int offset = index * 4 * VERTEX_SIZE;

        Vector4f colour = sprite.colour;

        float xAdd = 1.0f;
        float yAdd = 1.0f;

        for(int i = 0; i < 4; i++){
            if(i == 1){
                yAdd = 0.0f;
            }else if(i == 2){
                xAdd = 0.0f;
            }else if(i == 3){
                yAdd = 1.0f;
            }


            //load position
            vertices[offset] =   tf.position.x + xAdd*tf.scale.x ;
            vertices[offset+1] = tf.position.y + yAdd*tf.scale.y;

            vertices[offset + 2] = colour.x;
            vertices[offset + 3] = colour.y;
            vertices[offset + 4] = colour.z;
            vertices[offset + 5] = colour.w;

            offset+= VERTEX_SIZE;
        }
    }

    private int[] generateIndices(){
        // 6 indices per quad (3 per triangle)
        int[] elementIndices = new int[maxBatchSize*6];
        for(int i = 0; i < maxBatchSize; i++){
            loadElementIndices(elementIndices, i);
        }
        return elementIndices;
    }

    public void loadElementIndices(int[] elements, int index){
        int offset = 4*index; //offset is the index of the vertex in VAO/VBO, 4 vertices per quad/element
        int offsetArrayIndex = 6*index; //6 indices in EBO to interpret the 4 vertices in VBO

        //using triangles to make up quads, a quad is an element, made up of 4
        // vertices but needs 6 indices for the 2 triangles used in making it

        //always draw clockwise 0 to 3 then 2 anticl triangles along 3,2,0 and 0,2,0 to make up a quad
        //3,2,0,0,2,1          7,6,4,4,6,5

        //triangle 1
        elements[offsetArrayIndex] = offset + 3;
        elements[offsetArrayIndex+1] = offset + 2;
        elements[offsetArrayIndex+2] = offset + 0;

        //triangle 2
        elements[offsetArrayIndex+3] = offset + 0;
        elements[offsetArrayIndex+4] = offset + 2;
        elements[offsetArrayIndex+5] = offset + 1;
    }
}