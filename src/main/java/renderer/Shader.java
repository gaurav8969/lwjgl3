package renderer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader {
    private int shaderProgramID;

    private String vertexSource;
    private String fragmentSource;
    private String filepath;

    public Shader(String Filepath){
        this.filepath = Filepath;
        try{
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitstring = source.split("(#type)( )+([a-zA-Z]+)");

            //first pattern
            int index = source.indexOf("#type") + 6;
            int eol = source.indexOf("\n",index);
            String firstPattern = source.substring(index,eol).trim();

            //second pattern
            index = source.indexOf("#type",eol) + 6;
            eol = source.indexOf("\n",index);
            String secondPattern = source.substring(index,eol).trim();

            if(firstPattern.equals("vertex")){
                vertexSource = splitstring[1];
            }else if(firstPattern.equals("fragment")){
                fragmentSource = splitstring[1];
            }else{
                throw new IOException("Unexpected token: '" + filepath + "'");
            }

            if(secondPattern.equals("fragment")){
                fragmentSource = splitstring[2];
            }else if(secondPattern.equals("vertex")){
                vertexSource = splitstring[2];
            }else{
                throw new IOException("Unexpected token: '" + filepath + "'");
            }
        }catch(IOException e){
            e.printStackTrace();
            assert false: "Error: Could not open file for shader: '" + filepath + "'";
        }
    }

    public void compile(){
        // ============================================================
        // Compile and link shaders
        // ============================================================
        int vertexID, fragmentID;


        vertexID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexID,vertexSource);
        glCompileShader(vertexID);

        //Check for errors in compilation
        int success = glGetShaderi(vertexID,GL_COMPILE_STATUS);
        if(success == GL_FALSE){
            int len = glGetShaderi(vertexID,GL_INFO_LOG_LENGTH);
            System.out.println("Error: Vertex Shader compilation failed - '" + filepath + "'");
            System.out.println(glGetShaderInfoLog(vertexID,len));
            assert false: "";
        }

        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentID,fragmentSource);
        glCompileShader(fragmentID);

        //Check for errors in compilation
        success = glGetShaderi(fragmentID,GL_COMPILE_STATUS);
        if(success == GL_FALSE){
            int len = glGetShaderi(fragmentID,GL_INFO_LOG_LENGTH);
            System.out.println("Error: Fragment Shader compilation failed - '" + filepath + "'");
            System.out.println(glGetShaderInfoLog(fragmentID,len));
            assert false:"";
        }

        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID,vertexID);
        glAttachShader(shaderProgramID,fragmentID);
        glLinkProgram(shaderProgramID);

        //linker errors check
        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        if(success == GL_FALSE){
            int len = glGetProgrami(shaderProgramID,GL_INFO_LOG_LENGTH);
            System.out.println("Error: 'defaultShader.glsl'\n\tLinking of shaders failed.");
            System.out.println(glGetShaderInfoLog(shaderProgramID,len));
            assert false : "";
        }
    }

    public void use(){
        //bind shader program
        glUseProgram(shaderProgramID);
    }

    public void detach(){
        glUseProgram(0);
    }

    public void uploadMat4f(String varName, Matrix4f mat4){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        mat4.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }

    public void uploadFloat(String varName, float val){
        int varLocation = glGetUniformLocation(shaderProgramID,varName);
        use();
        glUniform1f(varLocation,val);
    }

    public void uploadInt(String varName, int val){
        int varLocation = glGetUniformLocation(shaderProgramID,varName);
        use();
        glUniform1i(varLocation,val);
    }

    public void uploadVec2f(String varName, Vector2f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform2f(varLocation, vec.x, vec.y);
    }

    public void uploadVec3f(String varName, Vector3f vec){
        int varLocation = glGetUniformLocation(shaderProgramID,varName);
        use();
        glUniform3f(varLocation,vec.x,vec.y,vec.z);
    }

    public void uploadVec4f(String varName, Vector4f vec){
        int varLocation = glGetUniformLocation(shaderProgramID,varName);
        use();
        glUniform4f(varLocation,vec.x,vec.y,vec.z,vec.w);
    }
    public void uploadTexture(String varName,int slot){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation,slot);
    }

    public void uploadIntArray(String varName, int[] textures){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1iv(varLocation,textures);
    }
}