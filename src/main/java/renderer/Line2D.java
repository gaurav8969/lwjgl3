package renderer;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class Line2D {
    private Vector2f to,from;
    private Vector3f colour;
    private int lifetime;

    public Line2D(Vector2f from, Vector2f to, Vector3f colour, int lifetime ){
        this.from = from;
        this.to = to;
        this.colour = colour;
        this.lifetime = lifetime;
    }

    public Vector2f getTo() {
        return to;
    }

    public Vector2f getFrom() {
        return from;
    }

    public Vector3f getColour() {
        return colour;
    }

    public int beginFrame(){
        this.lifetime--;
        return this.lifetime;
    }
    public int getLifetime() {
        return lifetime;
    }
}
