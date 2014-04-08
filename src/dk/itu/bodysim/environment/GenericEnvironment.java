package dk.itu.bodysim.environment;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * 
 * @author kszanto
 */
public class GenericEnvironment  extends Node {

    private final AssetManager assetManager;

    public GenericEnvironment(final String scenePath, final String sceneName, final AssetManager assetManager) {

        super(sceneName);

        this.assetManager = assetManager;
        final Spatial sceneModel = assetManager.loadModel(scenePath);
        sceneModel.setLocalTranslation(0, -5.2f, 0);
        sceneModel.setLocalScale(2);
        
        this.attachChild(sceneModel);
        
//        DirectionalLight dl = new DirectionalLight();
//        dl.setColor(ColorRGBA.White);
//        dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
//        this.addLight(dl);
    }
    
}
