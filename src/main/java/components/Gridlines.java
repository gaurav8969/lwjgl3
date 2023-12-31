package components;

import Renderer.DebugDraw;
import contra.Window;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.Settings;

import java.util.Set;

public class Gridlines extends Component {
    private boolean toDraw = true;
    private Vector2f cameraPos;

    @Override
    public void update(float dt){
        if(toDraw) {
            cameraPos = Window.getScene().camera().position();
            //frustum used in projection
            Vector2f projectionSize = Window.getScene().camera().projectionSize();
            //+2 to account for corners which are rounded off due to integer division
            int verticalLines = (int) (projectionSize.x)/Settings.GRID_WIDTH + 2;
            int horizontalLines = (int) (projectionSize.y)/Settings.GRID_HEIGHT + 2;

            Vector2f cameraGridPos = getGridPos(cameraPos);
            //to account for missing corner lines
            cameraGridPos.set(cameraGridPos.x - Settings.GRID_WIDTH, cameraGridPos.y - Settings.GRID_HEIGHT);

            int x = 0, y = 0;
            while (x < verticalLines || y < horizontalLines) {
                if (x < verticalLines) {
                    float xLine = cameraGridPos.x + x * Settings.GRID_WIDTH;
                    Window.getScene().debugDraw().addLine2D(new Vector2f(xLine, cameraGridPos.y),
                            new Vector2f(xLine, cameraPos.y + projectionSize.y), 1);
                    x++;
                }

                if (y < horizontalLines) {
                    float yLine = cameraGridPos.y + y* Settings.GRID_HEIGHT;
                    y++;
                    Window.getScene().debugDraw().addLine2D(new Vector2f(cameraGridPos.x, yLine),
                            new Vector2f(cameraPos.x + projectionSize.x, yLine), 1);
                }
            }
        }
    }

    //returns world coords of an object as it snaps to grid, call after updating cameraPos
    public Vector2f getGridPos(Vector2f pos){
        Vector2f gridPos = new Vector2f();
        gridPos.x = ((int)pos.x/Settings.GRID_WIDTH)*Settings.GRID_WIDTH;
        gridPos.y = ((int)pos.y/Settings.GRID_HEIGHT)*Settings.GRID_HEIGHT;
        return gridPos;
    }

    public void toggleGrid(){
        toDraw = !toDraw;
    }

    public boolean shouldDraw(){
        return this.toDraw;
    }
}
