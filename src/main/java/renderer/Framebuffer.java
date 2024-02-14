package renderer;

import org.joml.Vector2f;

import static org.lwjgl.opengl.GL30.*;

public class Framebuffer {
    private int fboID= 0;
    public Texture texture= null;
    private int height, width;

    public Framebuffer(int width, int height){
        this.width = width;
        this.height = height;

        //Generate frame buffer
        fboID = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER,fboID);

        //
        //create the texture to render the data to, and attach it to our framebuffer
        this.texture = new Texture(width,height);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D,texture.getTexID(), 0);

        //create renderbuffer to store the depth info, optimized for read-only use
        int rboID = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rboID);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32, width,height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER,GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rboID );

        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE){
            assert false: "Frame buffer is not complete!";
        }
        glBindFramebuffer(GL_FRAMEBUFFER,0);
    }

    public void bind(){
        glBindFramebuffer(GL_FRAMEBUFFER, fboID);
    }

    public void unbind(){
        glBindFramebuffer(GL_FRAMEBUFFER,0);
    }

    public int getFboID() {
        return fboID;
    }

    public int getTextureID() {
        return this.texture.getTexID();
    }

    public Vector2f getDimensions(){return new Vector2f(width, height);}
}