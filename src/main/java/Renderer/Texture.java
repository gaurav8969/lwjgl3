package Renderer;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture {
    public String filepath;
    public int width, height;
    transient private int texID;

    public Texture(){
        this.width = -1;
        this.height = -1;
        this.texID = -1;

    }
    public Texture(int width, int height){
        this.filepath = "Generated";
        this.width = width;
        this.height = height;

        // Generate texture on GPU
        texID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texID);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height,
                0, GL_RGB, GL_UNSIGNED_BYTE, 0);
    }

    public Texture init(String filepath) {
        this.filepath = filepath;
        //gen texture on gpu
        texID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texID);

        //set texture parameters
        //repeat image in both directions

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        //when stretching the image, pixelate using nearest neighbour interpolation
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);

        stbi_set_flip_vertically_on_load(true);
        ByteBuffer image = stbi_load(filepath, width, height, channels, 0);

        this.width = width.get(0);
        this.height = height.get(0);

        if (image != null) {
            if (channels.get(0) == 3) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0),
                        0, GL_RGB, GL_UNSIGNED_BYTE, image);
            } else if (channels.get(0) == 4) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0),
                        0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            } else {
                assert false : "Error: (Texture) Unknown number of channesl '" + channels.get(0) + "'";
            }
        } else {
            assert false : "Error: (Texture) Could not load image '" + filepath + "'";
        }
        stbi_image_free(image);

        return this;
    }

    @Override
    public boolean equals(Object o){
        if(o == null) return false;
        if(!(o instanceof  Texture))return false;
        Texture tex = (Texture)o;
        return this.width == tex.width && this.height == tex.height &&
                this.texID == tex.texID && (this.filepath.equals(tex.filepath));
    }

    public void bind(){
        glBindTexture(GL_TEXTURE_2D,texID);
    }

    public void unbind(){
        glBindTexture(GL_TEXTURE_2D,0);
    }

    public int getTexID() {
        return texID;
    }
}