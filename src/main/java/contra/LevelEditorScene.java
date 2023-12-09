package contra;

import Render.Shader;
import Render.Texture;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene{
    private Shader defaultShader;
    private
    Texture testTexture;
    private int vertexID, fragmentID, shaderProgram;
    private float[] vertexArray = {
             //position                      //colour                 //UV coordinates
             100.5f,   0.5f,  0.0f,          1.0f,0.0f,0.0f,1.0f,         1,1,             //Bottom right 0
              -0.5f, 100.5f,  0.0f,          0.0f,1.0f,0.0f,1.0f,         0,0,             //Top Left     1
             100.5f, 100.5f,  0.0f,          0.0f,0.0f,1.0f,1.0f,         1,0,             //Top Right    2
               0.5f,  -0.5f,  0.0f,          1.0f,1.0f,0.0f,1.0f,         0,1              //Bottom left  3
    };

    //vertices form triangles, which constitute elements
    //we add up triangles to get complex figures
    private int[] elementArray = {
            2,1,0, //
            0,1,3
    };

    private int vaoID, vboID, eboID;

    public LevelEditorScene() {

    }
    @Override
    public void init(){
        this.camera = new Camera(new Vector2f(-200, -300));
        defaultShader = new Shader("assets/shaders/default.glsl");
        defaultShader.compile(); //compile individual shaders and link them

        this.testTexture = new Texture("assets/images/superContra.png");
        //Generate VAO, VBO, and EBO buffer objects, and send to GPU
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        //allocating memory(buffer) for our vertices, depends on vertex array size
        //create float buffer for vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        //initializing it
        vertexBuffer.put(vertexArray).flip();

        //Create VBO upload the vertex buffer
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER,vboID);
        glBufferData(GL_ARRAY_BUFFER,vertexBuffer,GL_STATIC_DRAW);

        //Create the indices and upload
        //int buffer for elements
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER,elementBuffer,GL_STATIC_DRAW);

        //add the vertex attribute pointers
        int positionSize = 3;
        int colourSize = 4;
        int uvSize = 2;
        int vertexSizeBytes = (positionSize + colourSize + uvSize) * Float.BYTES;
        glVertexAttribPointer(0,positionSize,GL_FLOAT,false,vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1,colourSize,GL_FLOAT,false,vertexSizeBytes, positionSize*Float.BYTES);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(2,uvSize,GL_FLOAT, false,vertexSizeBytes,(positionSize + colourSize)*Float.BYTES);
        glEnableVertexAttribArray(2);
    }

    @Override
    public void update(float dt) {glEnableVertexAttribArray(1);
        defaultShader.use();

        //upload texture to shader
        defaultShader.uploadTexture("TEX_SAMPLER", 0);
        glActiveTexture(GL_TEXTURE0);
        testTexture.bind();

        defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());

        //bind the VAO that we're using
        glBindVertexArray(vaoID);

        //enable the vertex attribute pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES,elementArray.length,GL_UNSIGNED_INT,0);

        //unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);
        defaultShader.detach();
    }
}
