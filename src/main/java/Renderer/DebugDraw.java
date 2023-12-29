package Renderer;

import contra.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import util.AssetPool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL45.glClearNamedBufferSubData;

public class DebugDraw {
    boolean hasStarted = false;
    private final int POS_SIZE = 3;
    private final int COLOUR_SIZE = 3;

    private final int POS_OFFSET = 0;
    private final int COLOUR_OFFSET = POS_OFFSET + POS_SIZE* Float.BYTES;
    private final int VERTEX_SIZE = 6; //POS_SIZE + COLOUR_SIZE
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private int maxNumOfLines = 500;
    private List<Line2D> lines;
    private Shader shader;
    private float[] vertices;
    int vaoID,vboID;

    public DebugDraw(){
        this.shader = AssetPool.getShader("assets/shaders/debugLine2D.glsl");
        vertices = new float[maxNumOfLines * 2 * VERTEX_SIZE];
        lines = new ArrayList<Line2D>();
    }

    public void init(){
        //generate and bind a Vertex Array Object(VAO) that tells opengl how to interpret vertices
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        //reserve space for vertices
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER,vboID);
        glBufferData(GL_ARRAY_BUFFER,vertices.length * Float.BYTES,GL_DYNAMIC_DRAW);

        //setting the VAO object we generating
        glVertexAttribPointer(0,POS_SIZE,GL_FLOAT,false,VERTEX_SIZE_BYTES,POS_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1,COLOUR_SIZE,GL_FLOAT,false,VERTEX_SIZE_BYTES,COLOUR_OFFSET);
        glEnableVertexAttribArray(1);

        glLineWidth(2.0f);
    }

    public void beginFrame(){
        if(!hasStarted){
            init();
            hasStarted = true;
        }

        //remove dead lines
        for (int i=0; i < lines.size(); i++) {
            if (lines.get(i).beginFrame() < 0) {
                int offset = (lines.size()-1) * VERTEX_SIZE * 2;
                for(int index = 0; index < VERTEX_SIZE * 2 ; index++){
                    vertices[offset + index] = 0;
                }
                glBindBuffer(GL_ARRAY_BUFFER, vboID);
                glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertices, 0, lines.size() * 6 * 2));
                lines.remove(i);
                i--;
            }
        }
    }

    public void draw(){
        if( lines.size() > maxNumOfLines) {
            return;
        }

        int numLines = lines.size();
        //no load vertex func. since we actively remove vertices, only possible if we reload the entire array every frame
        for(int index = 0; index < numLines; index++){
            Line2D line = lines.get(index);
            int offset = index * VERTEX_SIZE * 2;
            Vector3f colour = line.getColour();

            for (int i = 0; i < 2; i++) {
                Vector2f position = (i == 0) ? line.getFrom() : line.getTo();

                vertices[offset] = position.x;
                vertices[offset + 1] = position.y;
                vertices[offset + 2] = -10.0f;

                vertices[offset + 3] = colour.x;
                vertices[offset + 4] = colour.y;
                vertices[offset + 5] = colour.z;

                offset += 6;
            }
        }

        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertices, 0, lines.size() * 6 * 2));

        // Use shader
        shader.use();
        shader.uploadMat4f("uProjection", Window.getScene().camera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().camera().getViewMatrix());

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawArrays(GL_LINES,0,numLines*6*2);

        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);

        shader.detach();
    }

    public void addLine2D(Vector2f from, Vector2f to) {
        addLine2D(from,to, new Vector3f(0,1,0),240 );
    }

    public void addLine2D(Vector2f from, Vector2f to, int lifetime) {
        addLine2D(from,to,new Vector3f(0,1,0),lifetime);
    }

    public void addLine2D(Vector2f from, Vector2f to, Vector3f colour) {
        addLine2D(from,to,colour,240);
    }

    public void addLine2D(Vector2f from,Vector2f to, Vector3f colour, int lifetime) {
        if(lines.size() >= maxNumOfLines) return;
        lines.add(new Line2D(from,to,colour,lifetime));
    }

}
