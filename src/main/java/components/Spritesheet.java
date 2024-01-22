package components;

import Renderer.Texture;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

//contains sprites inside a spritesheet, the texture info is implementation details, end goal is to extract
//it all as sprites stored in a list

public class Spritesheet{
    //no list, a single texture, since that is the point of spritesheets
    private Texture texture;
    private List<Sprite> sprites;

    private int rows,columns;

    public Spritesheet(Texture texture, int numOfSprites, int spriteWidth,int spriteHeight, int spacing) {
        this.texture = texture;
        sprites = new ArrayList<>();
        int currentX = 0;
        int currentY = texture.height - spriteHeight;
        int rowCount = 0;

        for (int i = 0; i < numOfSprites; i++) {
            //normalized
            float bottom = currentY / (float)texture.height;
            float top = (currentY + spriteHeight) / (float)texture.height;

            float left = currentX / (float)texture.width;
            float right = (currentX + spriteWidth) / (float)texture.width;

            Vector2f[] texCoords = {            //clockwise
                    new Vector2f(right, top),   //top-right
                    new Vector2f(right, bottom),//bottom-right
                    new Vector2f(left, bottom),//bottom-right
                    new Vector2f(left, top)    //top left
            };

            Sprite sprite = new Sprite();
            //chaining setters, they return 'this'
            sprites.add(sprite.setTexture(texture).setTexCoords(texCoords).setWidth(spriteWidth).setHeight(spriteHeight));

            currentX += spriteWidth + spacing;
            if (currentX >= texture.width) {
                ++rowCount;
                currentY -= spriteHeight + spacing;
                currentX = 0;
            }
        }

        rows = rowCount;
        columns = numOfSprites/rows;
    }

    public Sprite getSprite(int row, int column){
        return sprites.get((row-1)*this.columns + column-1);
    }

    public Sprite getSprite(int index){
        return sprites.get(index);
    }

    public int size(){
        return sprites.size();
    }

}