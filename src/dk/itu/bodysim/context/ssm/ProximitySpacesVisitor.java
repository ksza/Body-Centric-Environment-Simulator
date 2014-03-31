package dk.itu.bodysim.context.ssm;

import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.sun.j3d.internal.Distance;
import dk.itu.bodysim.context.EgocentricContextData;

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
    
    /**
     * 
     * @param cam represents the current location of the agent
     */
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
//            ssmBundle.updateSet(SSMSpaceType.WORLD_SPACE, spatial);
            
            canBePercieved(spatial);
        }
    }
    
    private boolean canBePercieved(final Spatial spatial) {
        
        final EgocentricContextData data = spatial.getUserData(EgocentricContextData.TAG);
        
        final Vector3f objectLocation = spatial.getWorldTranslation();
        final Vector3f agentLocation = cam.getLocation();
        
        
        
        final Ray ray = new Ray(agentLocation, objectLocation);
        final CollisionResults res = new CollisionResults();
        spatial.collideWith(ray, res);
        
        System.out.println(spatial.getName() + ": " + cam.distanceToNearPlane(objectLocation));
        
        return true;
    }
}
