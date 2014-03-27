package dk.itu.bodysim.context.server.view;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.jme3.scene.Spatial;
import dk.itu.bodysim.context.EgocentricContextManager;
import java.io.IOException;
import java.util.Set;
import org.restlet.data.MediaType;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class ApiContextResource extends ServerResource {

    private String toJson(final String... setNames) {

        final JsonObject result = new JsonObject();

        final EgocentricContextManager manager = EgocentricContextManager.getInstance();
        if (manager != null) {

            for (final String setName : setNames) {

                final JsonArray spaceArray = new JsonArray();
                result.add(setName, spaceArray);

                final Set<Spatial> spaceValue = manager.getSet(setName);
                if (spaceValue != null) {
                    for (final Spatial elem : spaceValue) {
                        spaceArray.add(new JsonPrimitive(elem.getName()));
                    }
                }
            }
        }

        return result.toString();
    }

    @Get("application/json")
    public StringRepresentation representJson() throws IOException {
        final String paramValue = getQueryValue("name");
        StringRepresentation stringRep;

        if ("all".equalsIgnoreCase(paramValue)) {
            final String allSpace = toJson("worldSpace", "perceptionSpace", "recognizableSet", "examinableSet", "actionSpace", "selectedSet", "manipulatedSet");
            stringRep = new StringRepresentation(allSpace, MediaType.APPLICATION_JSON);
        } else {
            stringRep = new StringRepresentation(toJson(paramValue), MediaType.APPLICATION_JSON);
        }

        return stringRep;
    }
}
