package contra;

import Render.Shader;
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
    private int vertexID, fragmentID, shaderProgram;
    private float[] vertexArray = {
             //position                      //colour
             100.5f,   0.5f,  0.0f,              1.0f,0.0f,0.0f,1.0f,    //Bottom right 0
              -0.5f, 100.5f,  0.0f,              0.0f,1.0f,0.0f,1.0f,    //Top Left     1
             100.5f, 100.5f,  0.0f,              0.0f,0.0f,1.0f,1.0f,    //Top Right    2
               0.5f,  -0.5f,  0.0f,              1.0f,1.0f,0.0f,1.0f    //Bottom-Right  3
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
        int floatSizeBytes = 4;
        int vertexSizeBytes = (positionSize + colourSize) * floatSizeBytes;
        glVertexAttribPointer(0,positionSize,GL_FLOAT,false,vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1,positionSize,GL_FLOAT,false,vertexSizeBytes, positionSize*floatSizeBytes);
        glEnableVertexAttribArray(1);
    }

    @Override
    public void update(float dt) {
        defaultShader.use();
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
