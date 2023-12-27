package  contra;
import imgui.ImFontAtlas;
import imgui.ImFontConfig;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiFreeTypeBuilderFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.internal.ImGui;
import scenes.Scene;

import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;

public class ImGuiLayer {
    public ImGuiImplGlfw imGuiGlfw;
    public ImGuiImplGl3 imGuiGl3;
    public void initImGui(long glfwWindow, String glslVersion){
        imGuiGlfw =  new ImGuiImplGlfw();
        imGuiGl3 = new ImGuiImplGl3();
        imgui.internal.ImGui.createContext(); //create imgui context
        ImGuiIO io = imgui.internal.ImGui.getIO(); //control it through this io object

        io.setIniFilename("imgui.ini");
        final ImFontAtlas fontAtlas = io.getFonts();
        final ImFontConfig fontConfig = new ImFontConfig(); // Natively allocated object, should be explicitly destroyed

        // Glyphs could be added per-font as well as per config used globally like here
        fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesDefault());

        // Fonts merge example
        fontConfig.setPixelSnapH(true);
        fontAtlas.addFontFromFileTTF("assets/fonts/8bitOperator.ttf", 20, fontConfig);

        fontConfig.destroy(); // After all fonts were added we don't need this config more

        // ------------------------------------------------------------
        // Use freetype instead of stb_truetype to build a fonts texture
        fontAtlas.setFlags(ImGuiFreeTypeBuilderFlags.LightHinting);
        fontAtlas.build();

        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);


        imGuiGlfw.init(glfwWindow, true);
        imGuiGl3.init(glslVersion);
    }


    public void update(Scene currentScene, float dt){
        imGuiGlfw.newFrame();
        ImGui.newFrame();

        currentScene.sceneImgui();

        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupWindowPtr = glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            glfwMakeContextCurrent(backupWindowPtr);
        }
    }
}