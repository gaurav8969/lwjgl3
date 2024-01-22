package util;

import Renderer.Shader;
import Renderer.Texture;
import components.Spritesheet;

import java.util.HashMap;
import java.util.Map;

public class AssetPool {
    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();
    private static Map<String, Spritesheet> spritesheets = new HashMap<>();

    private AssetPool(){}
    public static Shader getShader(String resource){
        if(shaders.containsKey(resource)){
            return shaders.get(resource);
        }else{
            Shader shader = new Shader(resource);
            shader.compile();
            AssetPool.shaders.put(resource,shader);
            return shader;
        }
    }

    public static Texture getTexture(String resource){
        if(textures.containsKey(resource)){
            return textures.get(resource);
        }else{
            Texture texture = new Texture().init(resource);
            AssetPool.textures.put(resource, texture);
            return texture;
        }
    }
    public static Spritesheet loadSpriteSheet(String resource, int numOfSprites,
                            int spriteWidth, int spriteHeight, int spacing){
        if(AssetPool.spritesheets.containsKey(resource)){
            return spritesheets.get(resource);
        }else{
            Spritesheet spritesheet = new Spritesheet(AssetPool.getTexture(resource),numOfSprites,spriteWidth,
                    spriteHeight,spacing);
            AssetPool.spritesheets.put(resource, spritesheet);
            return spritesheet;
        }
    }

    public static Spritesheet getSpriteSheet(String resource){
        return AssetPool.spritesheets.get(resource);
    }
}