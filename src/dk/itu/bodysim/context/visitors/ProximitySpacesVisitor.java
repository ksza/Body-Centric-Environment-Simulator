package dk.itu.bodysim.context.visitors;

import com.jme3.renderer.Camera;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import dk.itu.bodysim.context.EgocentricContextData;
import dk.itu.bodysim.context.SSMBundle;
import dk.itu.bodysim.context.SSMSpaceType;

/**
 * Determines dynamic spaces in one traverse of the spatials graph:
 * - perceptionSpace
 * - recognizableSet
 * - examinableSet
 * - actionSpace
 * - selectedSet
 * - manipulatedSet
 * 
 * @author kszanto
 */
public class ProximitySpacesVisitor implements SceneGraphVisitor{ 

    private final SSMBundle ssmBundle = SSMBundle.getInstance();
    private final Camera cam;
    
    public ProximitySpacesVisitor(final Camera cam) {
        ssmBundle.clearSet(SSMSpaceType.PERCEPTION_SPACE);
        ssmBundle.clearSet(SSMSpaceType.RECOGNIZABLE_SET);
        ssmBundle.clearSet(SSMSpaceType.EXAMINABLE_SET);
        ssmBundle.clearSet(SSMSpaceType.ACTION_SPACE);
        ssmBundle.clearSet(SSMSpaceType.SELECTED_SET);
        ssmBundle.clearSet(SSMSpaceType.MANIPULATED_SET);
        
        this.cam = cam;
    }   
    
    public void visit(Spatial spatial) {
        
        final EgocentricContextData data = spatial.getUserData(EgocentricContextData.TAG);
        if (data != null) {
            ssmBundle.updateSet(SSMSpaceType.WORLD_SPACE, spatial);
        }
    }
    
    private boolean canBePercieved(final Spatial spatial) {
        
        final EgocentricContextData data = spatial.getUserData(EgocentricContextData.TAG);
        return true;
    }
}
