package util;

import contra.Sound;
import renderer.Shader;
import renderer.Texture;
import components.Spritesheet;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AssetPool {
    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();
    private static Map<String, Spritesheet> spritesheets = new HashMap<>();
    private static Map<String, Sound> sounds = new HashMap<>();

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

    public static Collection<Sound> getAllSounds() {
        return sounds.values();
    }

    public static Sound getSound(String soundFile) {
        File file = new File(soundFile);
        if (sounds.containsKey(file.getAbsolutePath())) {
            return sounds.get(file.getAbsolutePath());
        } else {
            assert false : "Sound file not added '" + soundFile + "'";
        }

        return null;
    }

    public static Sound addSound(String soundFile, boolean loops) {
        File file = new File(soundFile);
        if (sounds.containsKey(file.getAbsolutePath())) {
            return sounds.get(file.getAbsolutePath());
        } else {
            Sound sound = new Sound(file.getAbsolutePath(), loops);
            AssetPool.sounds.put(file.getAbsolutePath(), sound);
            return sound;
        }
    }
}