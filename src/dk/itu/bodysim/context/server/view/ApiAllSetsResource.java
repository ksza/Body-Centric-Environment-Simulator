package dk.itu.bodysim.context.server.view;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.jme3.scene.Spatial;
import dk.itu.bodysim.context.EgocentricContextManager;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.restlet.data.MediaType;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class ApiAllSetsResource extends ServerResource {

    private static String data;

    static {
        try {
            data = new String(Files.readAllBytes(new File("resources/all_sets.html").toPath()));
        } catch (IOException ex) {
            Logger.getLogger(ApiAllSetsResource.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String toJson() {

        final JsonObject result = new JsonObject();
        final JsonArray perceptionSpaceArray = new JsonArray();
        result.add("perceptionSpace", perceptionSpaceArray);

        final EgocentricContextManager manager = EgocentricContextManager.getInstance();
        if (manager != null) {

            final Set<Spatial> perceptionSpace = manager.getPerceptionSpace();
            for (final Spatial elem : perceptionSpace) {
                perceptionSpaceArray.add(new JsonPrimitive(elem.getName()));
            }
        }

        return result.toString();
    }

    @Get("application/json")
    public StringRepresentation representJson() throws IOException {
        final StringRepresentation stringRep = new StringRepresentation(toJson(), MediaType.APPLICATION_JSON);

        return stringRep;
    }
}
