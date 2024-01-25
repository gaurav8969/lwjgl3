package contra;

import com.google.gson.*;
import components.Component;
import components.Transform;
import jdk.swing.interop.SwingInterOpUtils;

import java.lang.reflect.Type;

public class GameObjectDeserializer implements JsonDeserializer<GameObject> {

    @Override
    public GameObject deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext Context) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        GameObject go = Window.getScene().createGameObject(jsonObject.get("name").getAsString());

        go.componentsBitset = Context.deserialize(jsonObject.get("componentsBitset"), boolean[].class);

        JsonArray components = jsonObject.get("components").getAsJsonArray();
        for(JsonElement e: components){
            //now custom component deserializer is used, o/w its the no arg constructor with setters
            //same reason why we may have game objects without no-arg constructors
            Component c = Context.deserialize(e, Component.class);
            go.addComponent(c);
        }

        go.tf = go.getComponent(Transform.class);

        return go;
    }

}