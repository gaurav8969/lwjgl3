package components;

import contra.GameObject;
import editor.CImgui;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public abstract class Component {
    //parent entity, transient to avoid infinitely recursive serialization of game objects
    public transient GameObject gameObject = null;
    private static int IDCounter = 0;
    private int uniqueID = -1;
    public void init(){}

    public void update(float dt){}

    public void editorUpdate(float dt){}

    /*base imGui for component class, advisable to write override for each component to only affect
    relevant fields, avoiding reflection overhead*/
    public void imGui(){
        try{
            Field[] fields = this.getClass().getDeclaredFields();
            for(Field field: fields){
                boolean isTransient = Modifier.isTransient(field.getModifiers()); //fields we mark as transient are skipped
                if(isTransient){
                    continue;
                }

                boolean isPrivate = Modifier.isPrivate(field.getModifiers());
                if(isPrivate){
                    field.setAccessible(true);
                }

                Class type = field.getType();
                Object value = field.get(this); //this field of the relevant component instance
                String name = field.getName();

                if(value != null){
                    if(type == int.class){
                        int val = (int)value;
                        field.set(this, CImgui.dragInt(name, val));
                    }else if(type == float.class){
                        float val = (float)value;
                        field.set(this, CImgui.dragFloat(name, val));
                    }else if(type == boolean.class){
                        boolean val = (boolean)value; //binary value, no array needed
                        if(ImGui.checkbox(name + ": ", val)){
                            field.set(this, !val);
                        }
                    }else if(type == Vector2f.class){
                        Vector2f val = (Vector2f)value;
                        CImgui.drawVec2Control(name,val);
                    }else if(type == Vector3f.class){
                        Vector3f val = (Vector3f)value;
                        CImgui.drawVec3Control(name,val);
                    }else if(type == Vector4f.class) {
                        Vector4f val = (Vector4f) value;
                        CImgui.colorPicker4(name,val);
                    }
                }

                if(isPrivate){
                    field.setAccessible(false);
                }
            }
        }catch(IllegalAccessException e){
            e.printStackTrace();
        }
    }

    public int generateID(){
        if(uniqueID == -1){
            uniqueID = IDCounter++;
        }
        return uniqueID;
    }

    public int getID(){
        return uniqueID;
    }

    public void setMaxID(int maxID){
        IDCounter = maxID;
    }

    public void destroy(){}

    public static void loadCounter(int maxID){
        IDCounter = maxID;
    }
}