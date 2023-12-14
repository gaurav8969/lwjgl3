package util;

import Renderer.Shader;
import Renderer.Texture;

import java.util.HashMap;
import java.util.Map;

public class AssetPool {
    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();

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
            Texture texture = new Texture(resource);
            AssetPool.textures.put(resource, texture);
            return texture;
        }
    }
}