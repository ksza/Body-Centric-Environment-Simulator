package dk.itu.bodysim.context.server.view;

import com.jme3.scene.Spatial;
import dk.itu.bodysim.EgocentricApp;
import dk.itu.bodysim.context.EgocentricContextData;
import dk.itu.bodysim.context.EgocentricContextManager;
import dk.itu.bodysim.context.ssm.SSMBundle;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.restlet.data.MediaType;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class ViewContextResource extends ServerResource {

    private static String data;

    static {
        data = EgocentricApp.getInstance().getViewPresentationTemplate();
    }

    private String serilizeSet(final String setTitle, final Set<Spatial> collectedSpatials) {

        final StringBuilder sb = new StringBuilder("<tr>").append("<td>").append(setTitle).append("</td>");

        final EgocentricContextManager manager = EgocentricContextManager.getInstance();
        if (manager != null) {

            final Set<Spatial> setValue = SSMBundle.getInstance().getSet(setTitle);

            if (setValue != null) {
                for (final Spatial elem : setValue) {
                    collectedSpatials.add(elem);
                    final EgocentricContextData userData = elem.getUserData(EgocentricContextData.TAG);
                    sb.append("<td>").append(userData.getId()).append("</td>");
                }
            }
        }

        sb.append("</tr>");
        
        return sb.toString();
    }

    private String serializeEntityContext(final Spatial entity) {
        
        final StringBuilder sb = new StringBuilder();
        
        final EgocentricContextData userData = entity.getUserData(EgocentricContextData.TAG);
        
        sb.append("<tr>");
        sb.append("<td>").append(userData.getId()).append("</td>");
        sb.append("<td>").append(userData.getLastMeasuredDistance()).append("</td>");
        sb.append("<td>").append(userData.getType()).append("</td>");
        sb.append("<td>").append(userData.isCanBeMoved()).append("</td>");
        sb.append("<td>").append(userData.getPerceptionDistance()).append("</td>");
        sb.append("<td>").append(userData.getRecognitionDistance()).append("</td>");
        sb.append("<td>").append(userData.getExaminationDistance()).append("</td>");
        sb.append("</tr>");
        
        return sb.toString();
    }
    
    private String toHtml(final String... setNames) {
        String result = "_error_";

        final StringBuilder sb = new StringBuilder();

        sb.append("<br>").append("<h3>SSM Spaces</h3>").append("<table>");
        
        final Set<Spatial> collectedSpatials = new HashSet<Spatial>();
        for (final String setName : setNames) {
            sb.append(serilizeSet(setName, collectedSpatials));
        }
        
        sb.append("</table>").append("<br><br><br>").append("<h3>Entity Context Data</h3>").append("<table>");
           
        sb.append("<tr>");
        sb.append("<th>").append("</th>");
        sb.append("<th>").append("Last distance from agent").append("</th>");
        sb.append("<th>").append("Type").append("</th>");
        sb.append("<th>").append("Can be moved").append("</th>");
        sb.append("<th>").append("Perception Distance (WU)").append("</th>");
        sb.append("<th>").append("Recognition Distance (WU)").append("</th>");
        sb.append("<th>").append("Examination Distance (WU)").append("</th>");
        sb.append("</tr>");
        
        for(final Spatial entity: collectedSpatials) {
            sb.append(serializeEntityContext(entity));
        }
        
        sb.append("</table>").append("<small>* WU = expressed in World Units</small>");
        
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