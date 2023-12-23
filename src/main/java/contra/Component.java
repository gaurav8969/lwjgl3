package contra;

import imgui.ImGui;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public abstract class Component {
    //parent entity, transient to avoid infinitely recursive serialization of game objects
    public transient GameObject gameObject = null;

    public void init(){}

    public void update(float dt){}

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

                if(type == int.class){
                    int val = (int)value;
                    int[] imInt = {val};
                    if(ImGui.dragInt(name + ": ", imInt)){
                        field.set(this, imInt[0]);
                    }
                }else if(type == float.class){
                    float val = (float)value;
                    float[] imFloat = {val};
                    if(ImGui.dragFloat(name + ": ", imFloat)){
                        field.set(this, imFloat[0]);
                    }
                }else if(type == boolean.class){
                    boolean val = (boolean)value; //binary value, no array needed
                    if(ImGui.checkbox(name + ": ", val)){
                        field.set(this, !val);
                    }
                }else if(type == Vector3f.class){
                    Vector3f val = (Vector3f)value;
                    float[] imFloat3f = {val.x, val.y, val.z};
                    if(ImGui.dragFloat3(name + ": ", imFloat3f)){
                        val.set(imFloat3f[0],imFloat3f[1],imFloat3f[2]);
                    }
                }else if(type == Vector4f.class) {
                    Vector4f val = (Vector4f) value;
                    float[] imFloat4f = {val.x, val.y, val.z, val.w};
                    if (ImGui.dragFloat3(name + ": ", imFloat4f)) {
                        val.set(imFloat4f[0], imFloat4f[1], imFloat4f[2]);
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

}