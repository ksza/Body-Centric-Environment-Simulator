package dk.itu.bodysim.context.server.view;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.jme3.scene.Spatial;
import dk.itu.bodysim.context.EgocentricContextData;
import dk.itu.bodysim.context.EgocentricContextManager;
import dk.itu.bodysim.context.ssm.SSMBundle;
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

                final Set<Spatial> spaceValue = SSMBundle.getInstance().getSet(setName);
                if (spaceValue != null) {

                    for (final Spatial elem : spaceValue) {

                        final EgocentricContextData data = elem.getUserData(EgocentricContextData.TAG);

                        GsonBuilder builder = new GsonBuilder();
                        spaceArray.add(builder.create().toJsonTree(data));
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
