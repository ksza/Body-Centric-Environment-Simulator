package dk.itu.bodysim.context.server.api;

import com.jme3.scene.Spatial;
import dk.itu.bodysim.context.EgocentricContextData;
import dk.itu.bodysim.context.EgocentricContextManager;
import dk.itu.bodysim.context.ssm.SSMBundle;
import dk.itu.bodysim.context.server.view.ApiContextResource;
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

public class ViewContextResource extends ServerResource {

    private static String data;

    static {
        try {
            data = new String(Files.readAllBytes(new File("resources/all_sets.html").toPath()));
        } catch (IOException ex) {
            Logger.getLogger(ApiContextResource.class.getName()).log(Level.SEVERE, null, ex);
        }

        final EgocentricContextManager manager = EgocentricContextManager.getInstance();
        if (manager != null) {
        }
    }

    private String serilizeSet(final String setTitle) {

        final StringBuilder sb = new StringBuilder(setTitle).append(": ");

        final EgocentricContextManager manager = EgocentricContextManager.getInstance();
        if (manager != null) {

            final Set<Spatial> setValue = SSMBundle.getInstance().getSet(setTitle);

            if (setValue != null) {
                for (final Spatial elem : setValue) {
                    final EgocentricContextData userData = elem.getUserData(EgocentricContextData.TAG);
                    sb.append(elem.getName()).append("(").append(userData.getId()).append(")").append("; ");
                }

                System.out.println(sb.toString());
            }
        }

        return sb.toString();
    }

    private String toHtml(final String... setNames) {
        String result = "_error_";

        final StringBuilder sb = new StringBuilder();

        for (final String setName : setNames) {
            sb.append(serilizeSet(setName)).append("<br>");
        }

        final String serializedValue = sb.toString();
        if (serializedValue != null && serializedValue.length() > 0) {
            result = data.replace("<<DATA>>", sb.toString());
        }

        return result;
    }

    @Get("text/html")
    public StringRepresentation HTML() throws IOException {

        final String paramValue = getQueryValue("name");
        StringRepresentation stringRep;
        
        if("all".equalsIgnoreCase(paramValue)) {
            final String allSpace = toHtml("worldSpace", "perceptionSpace", "recognizableSet", "examinableSet", "actionSpace", "selectedSet", "manipulatedSet");
            stringRep = new StringRepresentation(allSpace, MediaType.TEXT_HTML);
        } else {
            stringRep = new StringRepresentation(toHtml(paramValue), MediaType.TEXT_HTML);
        }
             
        return stringRep;
    }
}