package dk.itu.bodysim;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.WireBox;
import dk.itu.bodysim.context.EgocentricContextData;

/**
 *
 * @author kszanto
 */
public class Util {

    public static void highlightEntity(final SimpleApplication app, final Spatial spatial) {

        final EgocentricContextData data = spatial.getUserData(EgocentricContextData.TAG);
        
                    final AmbientLight light = new AmbientLight();
            light.setColor(ColorRGBA.Gray);
            spatial.addLight(light);
        
        final WireBox box = new WireBox();
        box.setBound(spatial.getWorldBound());
        box.setLineWidth(5);

        Geometry cube = new Geometry(data.getId() + "_highlight", box);
        cube.setLocalTranslation(spatial.getWorldTranslation());
        Material mat1 = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");

        cube.setLocalScale(spatial.getWorldScale());
        cube.setMaterial(mat1);

        app.getRootNode().attachChild(cube);
    }
    
    public static void removeHightlight(final SimpleApplication app, final Spatial spatial) {
        
        final EgocentricContextData data = spatial.getUserData(EgocentricContextData.TAG);
        final Spatial highlight = app.getRootNode().getChild(data.getId() + "_highlight");
        
        highlight.removeFromParent();
    }
}
