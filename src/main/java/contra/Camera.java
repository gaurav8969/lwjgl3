package contra;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera{
    private Matrix4f projectionMatrix, viewMatrix;
    public Vector3f position;
    public Vector3f cameraFront = new Vector3f(0.0f,0.0f,-1.0f);
    public Vector3f cameraUp = new Vector3f(0.0f,1.0f,0.0f);

    public Camera(Vector3f Position){
        this.position = Position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        adjustProjection();
    }

    public void adjustProjection(){
        projectionMatrix.identity();
        projectionMatrix.ortho(0.0f,32.0f * 40.0f,0.0f,32.0f * 21.0f, 0.0f, 100.0f);
    }

    public Matrix4f getViewMatrix(){
        cameraFront.set(position.x, position.y, cameraFront.z);
        this.viewMatrix.identity();
        viewMatrix.lookAt(new Vector3f(position.x,position.y, position.z),
                cameraFront, cameraUp);
        return this.viewMatrix;
    }

    public Matrix4f getProjectionMatrix(){
        return this.projectionMatrix;
    }
}
