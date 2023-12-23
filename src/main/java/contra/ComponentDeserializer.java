package contra;

import com.google.gson.*;

import java.lang.reflect.Type;

public class ComponentDeserializer implements JsonSerializer<Component>, JsonDeserializer<Component> {

    @Override
    public Component deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext Context) throws JsonParseException {
        if(!jsonElement.getAsJsonObject().isJsonNull()) {
            JsonObject json = jsonElement.getAsJsonObject();
            String typeName = json.get("type").getAsString();
            JsonElement properties = json.get("properties");

            try {
                return Context.deserialize(properties, Class.forName(typeName));
            } catch (ClassNotFoundException e) {
                throw new JsonParseException("Unknown element type: " + typeName, e);
            }
        }
        return null;

    }

    @Override
    public JsonElement serialize(Component component, Type type, JsonSerializationContext Context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("type", new JsonPrimitive(component.getClass().getCanonicalName()));
        jsonObject.add("properties", Context.serialize(component));
        return jsonObject;
    }

}
