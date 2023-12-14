package components;

import contra.Component;
import org.joml.Vector4f;

public class SpriteRenderer extends Component {

    public Vector4f colour;
    public SpriteRenderer(Vector4f color){
        colour = color;
    }

    @Override
    public void init() {
    }

    @Override
    public void update(float dt) {
    }
}
