package dk.itu.bodysim.context.ssm;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import dk.itu.bodysim.Util;
import dk.itu.bodysim.context.EgocentricContextData;

/**
 * The world space consists of all entities in the current scene, carying
 * EgocentricContextData.
 * 
 * @author kszanto
 */
public class WorldSpaceVisitor implements SceneGraphVisitor {

    private final SSMBundle ssmBundle = SSMBundle.getInstance();
    private final SimpleApplication app;
    
    public WorldSpaceVisitor(final SimpleApplication app) {
        ssmBundle.clearSet(SSMSpaceType.WORLD_SPACE);
        this.app = app;
    }       
    
    public void visit(Spatial spatial) {
        final EgocentricContextData data = spatial.getUserData(EgocentricContextData.TAG);
        if (data != null) {
            
            Util.highlightEntity(app, spatial);
            final AmbientLight light = new AmbientLight();
            light.setColor(ColorRGBA.Gray);
            spatial.addLight(light);
            
            ssmBundle.updateSet(SSMSpaceType.WORLD_SPACE, spatial);
        }
    }
}
