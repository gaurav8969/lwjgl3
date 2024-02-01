package editor;

import components.Component;
import contra.Camera;
import contra.Window;
import org.joml.Vector2f;
import renderer.DebugDraw;
import util.Settings;

public class Gridlines extends Component {
    private boolean toDraw = true;

    @Override
    public void editorUpdate(float dt){
        if(toDraw) {
            Camera camera = Window.getScene().camera();
            Vector2f cameraPos = camera.position();
            float zoom = camera.getZoom();
            //frustum used in projection
            Vector2f projectionSize = camera.projectionSize();
            //+2 to account for corners which are rounded off due to integer division
            float verticalLines = (zoom* projectionSize.x)/ Settings.GRID_WIDTH + 2;
            float horizontalLines = (zoom* projectionSize.y)/Settings.GRID_HEIGHT + 2;

            Vector2f cameraGridPos = getGridPos(cameraPos);
            //to account for missing corner lines
            cameraGridPos.set(cameraGridPos.x - Settings.GRID_WIDTH, cameraGridPos.y - Settings.GRID_HEIGHT);

            int x = 0, y = 0;
            while (x < verticalLines || y < horizontalLines) {
                if (x < verticalLines) {
                    float xLine = cameraGridPos.x + x * Settings.GRID_WIDTH;
                    DebugDraw.addLine2D(new Vector2f(xLine, cameraGridPos.y),
                            new Vector2f(xLine, cameraPos.y + zoom* projectionSize.y), 1);
                    x++;
                }

                if (y < horizontalLines) {
                    float yLine = cameraGridPos.y + y* Settings.GRID_HEIGHT;
                    y++;
                    DebugDraw.addLine2D(new Vector2f(cameraGridPos.x, yLine),
                            new Vector2f(cameraPos.x + zoom* projectionSize.x, yLine), 1);
                }
            }
        }
    }

    //returns world coords of an object as it snaps to grid, call after updating cameraPos
    public Vector2f getGridPos(Vector2f pos){
        Vector2f gridPos = new Vector2f();
        gridPos.x = ((int)Math.floor((pos.x/Settings.GRID_WIDTH))*Settings.GRID_WIDTH);
        gridPos.y = ((int)Math.floor((pos.y/Settings.GRID_HEIGHT))*Settings.GRID_HEIGHT);
        return gridPos;
    }

    public void toggleGrid(){
        toDraw = !toDraw;
    }

    public boolean shouldDraw(){
        return this.toDraw;
    }
}