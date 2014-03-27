package dk.itu.bodysim.context.server;

import com.jme3.scene.Spatial;
import dk.itu.bodysim.context.EgocentricContextManager;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
 * Resource which has only one representation.
 *
 */
public class AllSetsResource extends ServerResource {

    private static String data;
    static {
        try {
            data = new String(Files.readAllBytes(new File("resources/all_sets.html").toPath()));
        } catch (IOException ex) {
            Logger.getLogger(AllSetsResource.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private String toHtml() {
        String result = "empty";
        
        final EgocentricContextManager manager = EgocentricContextManager.getInstance();
        if(manager != null) {
                final Set<Spatial> worldSpace = manager.getPerceptionSpace();
                
                final StringBuilder sb = new StringBuilder();
                for(final Spatial elem: worldSpace) {
                   sb.append(elem).append("; ");
                }
                System.out.println("Perception Space: " + sb.toString());
                
                result = data.replace("<<DATA>>", "Perception Space: " + sb.toString());
        }
        
        return result;
    }
    
    @Get("text/html")
    public Representation represent() throws IOException {
        final StringRepresentation stringRep = new StringRepresentation(toHtml(), MediaType.TEXT_HTML);

        return stringRep;
    }
}
