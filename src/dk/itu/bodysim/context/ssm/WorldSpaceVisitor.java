package dk.itu.bodysim.context.ssm;

import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingBox;
import com.jme3.light.AmbientLight;
import com.jme3.light.Light;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.WireBox;
import dk.itu.bodysim.EgocentricApp;
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
//            final WireBox box = new WireBox();
//            box.setBound(spatial.getWorldBound());
//            box.setLineWidth(5);
//            
//            Geometry cube = new Geometry("highlight", box);
//            cube.setLocalTranslation(spatial.getLocalTranslation());
//            Material mat1 = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");            
//            
//            cube.setMaterial(mat1);
////            cube.setUserData(EgocentricContextData.TAG, data);
//
//            ((Node) spatial.getParent()).attachChild(cube);
            
            Util.highlightEntity(app, spatial);
            
            ssmBundle.updateSet(SSMSpaceType.WORLD_SPACE, spatial);
        }
    }
}
