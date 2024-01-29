package editor;

import contra.GameObject;
import contra.Window;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import java.util.List;

public class SceneHierarchyWindow{
    public void imGui(){
        ImGui.begin("Scene Hierarchy");


        List<GameObject> gameObjects = Window.getScene().getGameObjects();
        int index = 0;
        for(GameObject go: gameObjects){
            if(!go.isSerializable()){
                continue;
            }

            ImGui.pushID(index);
            boolean treeNodeOpen = ImGui.treeNodeEx(
                    go.name,
                     ImGuiTreeNodeFlags.FramePadding |
                            ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth,
                    go.name
            );
            ImGui.popID();

            if(treeNodeOpen){
                ImGui.treePop();
            }
            index++;
        }
        ImGui.end();
    }
}
