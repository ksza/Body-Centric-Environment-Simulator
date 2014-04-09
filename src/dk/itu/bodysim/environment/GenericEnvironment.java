package dk.itu.bodysim.environment;

import com.jme3.asset.AssetManager;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author kszanto
 */
public class GenericEnvironment extends Node {

    private final AssetManager assetManager;

    public GenericEnvironment(final String scenePath, final String sceneName, final AssetManager assetManager) {

        super(sceneName);

        this.assetManager = assetManager;
        final Spatial sceneModel = assetManager.loadModel(scenePath);
        sceneModel.setLocalTranslation(0, -5.2f, 0);
        sceneModel.setLocalScale(2);

        this.attachChild(sceneModel);

        DirectionalLight dl1 = new DirectionalLight();
        dl1.setColor(ColorRGBA.White);
        dl1.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
        this.addLight(dl1);

        DirectionalLight dl2 = new DirectionalLight();
        dl2.setColor(ColorRGBA.White);
        dl2.setDirection(new Vector3f(2.8f, -2.8f, 2.8f).normalizeLocal());
        this.addLight(dl2);

        DirectionalLight dl3 = new DirectionalLight();
        dl3.setColor(ColorRGBA.White);
        dl3.setDirection(new Vector3f(-2.8f, -2.8f, 2.8f).normalizeLocal());
        this.addLight(dl3);
    }
}
