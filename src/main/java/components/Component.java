package components;

import contra.GameObject;
import editor.CImgui;
import imgui.ImGui;
import imgui.type.ImInt;
import org.jbox2d.dynamics.contacts.Contact;
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

    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal){}

    public void endCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal){}

    public void preSolve(GameObject collidingObject, Contact contact, Vector2f hitNormal){}

    public void postSolve(GameObject collidingObject, Contact contact, Vector2f hitNormal){}

    /*base imGui for component class, overridable for any component*/
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

                if(type == String.class || value != null){
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
                    }else if(type.isEnum()){
                        String[] enumValues = getEnumValues(type);
                        String enumType = ((Enum)value).name();
                        ImInt index = new ImInt(indexOf(enumType, enumValues));
                        if(ImGui.combo(field.getName(), index, enumValues, enumValues.length)){
                            field.set(this, type.getEnumConstants()[index.get()]);
                        }
                    }else if (type == String.class) {
                        field.set(this,
                                CImgui.inputText(field.getName() + ": ",
                                        (String)value));
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

    public void generateID(){
        this.uniqueID = IDCounter++;
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

    //helper functions
    private <T extends Enum<T>> String[] getEnumValues(Class<T> enumType){
        String[] enumValues = new String[enumType.getEnumConstants().length];
        int i = 0;
        for(T enumIntegerValue: enumType.getEnumConstants()){
            enumValues[i] = enumIntegerValue.name();
            i++;
        }
        return enumValues;
    }

    private int indexOf(String str, String[] arr){
        for(int i = 0; i < arr.length; i++){
            if(str.equals(arr[i])){
                return i;
            }
        }
        return -1;
    }
}