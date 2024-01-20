package editor;

import contra.MouseListener;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import contra.Window;
import org.joml.Vector2f;

public class GameViewWindow{
    private float leftX, bottomY;
    public void imgui(){
        ImGui.begin("Game Viewport", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);

        ImVec2 windowSize = getLargestSizeForViewport();
        ImVec2 windowPos = getCenteredPositionForViewport(windowSize);

        ImGui.setCursorPos(windowPos.x, windowPos.y);

        ImVec2 topLeft = new ImVec2();
        //pos rel to glfw parent window
        ImGui.getCursorScreenPos(topLeft);
        topLeft.x -= ImGui.getScrollX();
        topLeft.y -= ImGui.getScrollY();
        leftX = topLeft.x;
        bottomY = topLeft.y;

        int textureID = Window.getFramebuffer().getTextureID();
        //top left to bottom right, (0,1): top-left and (1,0) is bottom-right
        ImGui.image(textureID, windowSize.x, windowSize.y, 0,1,1,0);

        MouseListener.setGameViewportPos(new Vector2f(topLeft.x, topLeft.y));
        MouseListener.setGameViewportSize(new Vector2f(windowSize.x, windowSize.y));

        ImGui.end();
    }

    private ImVec2 getLargestSizeForViewport(){
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);
        windowSize.x -= ImGui.getScrollX();
        windowSize.y -= ImGui.getScrollY();

        float aspectWidth = windowSize.x;
        float aspectHeight = aspectWidth/Window.getTargetAspectRatio();
        if(aspectHeight > windowSize.y){
            //switch to pillarbox mode
            aspectHeight = windowSize.y;
            aspectWidth = aspectHeight * Window.getTargetAspectRatio();
        }
        return new ImVec2(aspectWidth,aspectHeight);
    }

    private ImVec2 getCenteredPositionForViewport(ImVec2 viewportSize){
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);
        windowSize.x -= ImGui.getScrollX();
        windowSize.y -= ImGui.getScrollY();

        float viewportX = windowSize.x/2.0f - viewportSize.x/2.0f;
        float viewportY = windowSize.y/2.0f - viewportSize.y/2.0f;

        return new ImVec2(viewportX + ImGui.getCursorPosX(),viewportY + ImGui.getCursorPosY());
    }
}