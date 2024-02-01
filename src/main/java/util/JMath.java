package util;

import org.joml.Vector2f;

public class JMath {
    public static void rotate(Vector2f vec, float angleDeg, Vector2f origin) {
        float x = vec.x - origin.x;
        float y = vec.y - origin.y;

        float cos = (float)Math.cos(Math.toRadians(angleDeg));
        float sin = (float)Math.sin(Math.toRadians(angleDeg));

        float xPrime = (x * cos) - (y * sin);
        float yPrime = (x * sin) + (y * cos);

        xPrime += origin.x;
        yPrime += origin.y;

        vec.x = xPrime;
        vec.y = yPrime;
    }

    public static boolean compare(float x, float y, float epsilon) {
        return Math.abs(x - y) <= epsilon * Math.max(1.0f, Math.max(Math.abs(x), Math.abs(y)));
    }

    public static boolean compare(Vector2f vec1, Vector2f vec2, float epsilon) {
        return compare(vec1.x, vec2.x, epsilon) && compare(vec1.y, vec2.y, epsilon);
    }

    public static boolean compare(float x, float y) {
        return Math.abs(x - y) <= Float.MIN_VALUE * Math.max(1.0f, Math.max(Math.abs(x), Math.abs(y)));
    }

    public static boolean compare(Vector2f vec1, Vector2f vec2) {
        return compare(vec1.x, vec2.x) && compare(vec1.y, vec2.y);
    }

    public static float smallest(float[] arr){
        float smallest = Float.MAX_VALUE;
        for(float f: arr){
            if( f < smallest){
                smallest = f;
            }
        }
        return smallest;
    }

    public static float largest(float[] arr){
        float largest = Float.MIN_VALUE;
        for(float f: arr){
            if( f > largest){
                largest = f;
            }
        }
        return largest;
    }

    //no rotated triangles for now
    public static boolean pointInRect(Vector2f centre, Vector2f dimensions, Vector2f point){
        Vector2f min = new Vector2f(centre).sub(new Vector2f(dimensions).mul(0.5f));
        Vector2f max = new Vector2f(centre).add(new Vector2f(dimensions).mul(0.5f));

        float xMin = min.x;
        float xMax = max.x;
        float yMin = min.y;
        float yMax = max.y;

        float x = point.x;
        float y = point.y;
        return x > xMin && x < xMax && y > yMin && y < yMax;
    }
}
