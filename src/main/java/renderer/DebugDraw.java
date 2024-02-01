package renderer;

import contra.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import util.AssetPool;
import util.JMath;

import java.util.ArrayList;
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

    private static int maxNumOfLines = 20000;
    private static List<Line2D> lines;
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
                vertices[offset + 2] = +10.0f;

                vertices[offset + 3] = colour.x;
                vertices[offset + 4] = colour.y;
                vertices[offset + 5] = colour.z;

                offset += 6;
            }
        }

        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        //glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_DYNAMIC_DRAW);

        // Use shader
        shader.use();
        shader.uploadMat4f("uProjection", Window.getScene().camera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().camera().getViewMatrix());

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawArrays(GL_LINES,0,numLines*2);

        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);

        shader.detach();
    }

    public static void addLine2D(Vector2f from, Vector2f to) {
        addLine2D(from,to, new Vector3f(0,0,0),240 );
    }

    public static void addLine2D(Vector2f from, Vector2f to, int lifetime) {
        addLine2D(from,to,new Vector3f(0,0,0),lifetime);
    }

    public static void addLine2D(Vector2f from, Vector2f to, Vector3f colour) {
        addLine2D(from,to,colour,240);
    }

    public static void addLine2D(Vector2f from,Vector2f to, Vector3f colour, int lifetime) {
        if(lines.size() >= maxNumOfLines) return;
        lines.add(new Line2D(from,to,colour,lifetime));
    }

    // Add Box2D methods
    public static void addBox2D(Vector2f center, Vector2f dimensions) {
        // TODO: ADD CONSTANTS FOR COMMON COLORS
        addBox2D(center, dimensions, 0, new Vector3f(0, 1, 0), 1);
    }
    public static void addBox2D(Vector2f center, Vector2f dimensions, float rotation) {
        addBox2D(center, dimensions, rotation, new Vector3f(0, 1, 0), 1);
    }

    public static void addBox2D(Vector2f center, Vector2f dimensions, float rotation, Vector3f color) {
        addBox2D(center, dimensions, rotation, color, 1);
    }

    public static void addBox2D(Vector2f center, Vector2f dimensions, float rotation, int lifetime) {
        addBox2D(center, dimensions, rotation, new Vector3f(0,1,0), lifetime);
    }

    public static void addBox2D(Vector2f center, Vector2f dimensions, float rotation,
                                Vector3f color, int lifetime) {
        Vector2f min = new Vector2f(center).sub(new Vector2f(dimensions).mul(0.5f));
        Vector2f max = new Vector2f(center).add(new Vector2f(dimensions).mul(0.5f));

        Vector2f[] vertices = {
                new Vector2f(min.x, min.y), new Vector2f(min.x, max.y),
                new Vector2f(max.x, max.y), new Vector2f(max.x, min.y)
        };

        if (rotation != 0.0f) {
            for (Vector2f vert : vertices) {
                JMath.rotate(vert, rotation, center);
            }
        }

        DebugDraw.addLine2D(vertices[0], vertices[1], color, lifetime);
        DebugDraw.addLine2D(vertices[0], vertices[3], color, lifetime);
        DebugDraw.addLine2D(vertices[1], vertices[2], color, lifetime);
        DebugDraw.addLine2D(vertices[2], vertices[3], color, lifetime);
    }

    public static void addCircle2D(Vector2f centre, float radius){
        addCircle2D(centre,radius,120,new Vector3f(0,0,1));
    }
    public static void addCircle2D(Vector2f centre, float radius, int lifetime){
        addCircle2D(centre,radius,lifetime, new Vector3f(0,0,1));
    }

    public static void addCircle2D(Vector2f centre, float radius, Vector3f colour){
        addCircle2D(centre,radius,120,colour);
    }

    public static void addCircle2D(Vector2f centre, float radius, int lifetime, Vector3f colour){
        Vector2f[] points = new Vector2f[20];
        int size = points.length;
        float increment = 360.0f/size;

        float angle= 0.0f; // in degrees
        for(int i = 0; i < size; i++){
            Vector2f point = new Vector2f(centre.x + radius,centre.y); //point on circle, (radius,0) is the 'first'
            JMath.rotate(point,angle,centre);
            points[i] = point;
            angle += increment;

            if(i > 0) {
                addLine2D(points[i - 1], points[i], colour,lifetime);
            }
        }
        addLine2D(points[size-1], points[0], colour,lifetime);
    }

}
