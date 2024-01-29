package contra;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Camera {
    private Matrix4f projectionMatrix, viewMatrix, inverseProjection, inverseView;
    private Vector2f position;
    private float zoom = 1.0f;

    private float projectionWidth = 6;
    private float projectionHeight = 3;
    private Vector2f projectionSize = new Vector2f(projectionWidth,projectionHeight);

    public Camera(Vector2f Position){
        this.position = Position;

        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.inverseProjection = new Matrix4f();
        this.inverseView = new Matrix4f();

        adjustProjection();
    }

    public void adjustProjection(){
        projectionMatrix.identity();
        projectionMatrix.ortho(0,zoom * projectionSize.x,0,zoom * projectionSize.y,
                0.0f, 100.0f);
        projectionMatrix.invert(inverseProjection);
    }

    public Matrix4f getViewMatrix(){
        Vector3f cameraFront = new Vector3f(0.0f,0.0f,-1.0f);
        Vector3f cameraUp = new Vector3f(0.0f,1.0f,0.0f);
        this.viewMatrix.identity();
        viewMatrix.lookAt(new Vector3f(position.x,position.y, 20.0f),
                cameraFront.add(position.x, position.y,0.0f), cameraUp);
        viewMatrix.invert(inverseView);
        return this.viewMatrix;
    }

    public Matrix4f getProjectionMatrix(){
        return this.projectionMatrix;
    }

    public Matrix4f getInverseViewMatrix(){
        return this.inverseView;
    }

    public Matrix4f getInverseProjectionMatrix(){
        return this.inverseProjection;
    }

    public Vector2f position(){
        return this.position;
    }

    public Vector2f projectionSize(){
        return this.projectionSize;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
        this.adjustProjection();
    }

    public void addZoom(float value){
        this.zoom += value;
        this.adjustProjection();
    }
}