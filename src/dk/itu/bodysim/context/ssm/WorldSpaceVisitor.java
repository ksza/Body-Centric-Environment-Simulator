package dk.itu.bodysim.context.ssm;

import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import dk.itu.bodysim.context.EgocentricContextData;

/**
 * The world space consists of all entities in the current scene, carying
 * EgocentricContextData.
 * 
 * @author kszanto
 */
public class WorldSpaceVisitor implements SceneGraphVisitor {

    private final SSMBundle ssmBundle = SSMBundle.getInstance();

    public WorldSpaceVisitor() {
        ssmBundle.clearSet(SSMSpaceType.WORLD_SPACE);
    }       
    
    public void visit(Spatial spatial) {
        final EgocentricContextData data = spatial.getUserData(EgocentricContextData.TAG);
        if (data != null) {
            ssmBundle.updateSet(SSMSpaceType.WORLD_SPACE, spatial);
        }
    }
}
