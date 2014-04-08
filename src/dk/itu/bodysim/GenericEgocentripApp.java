package dk.itu.bodysim;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import dk.itu.bodysim.environment.GenericEnvironment;

/**
 *
 * @author kszanto
 */
public class GenericEgocentripApp extends EgocentricApp {

    private Vector3f agentPosition = new Vector3f(0, 0, 0);
    private float agentHeight = 15;
    
    private String sceneName = "Untitled Scene";
    private String sceneModelPath = "Scenes/untitled/untitled.j3o";

    public static void main(String[] args) {
        GenericEgocentripApp app = new GenericEgocentripApp();
        app.start();
    }
    
    @Override
    protected Node createEnvironmentScene() {
        return new GenericEnvironment(sceneModelPath, sceneName, assetManager);
    }

    @Override
    public Vector3f getInitialAgentPosition() {
        return agentPosition;
    }

    @Override
    public float getAgentHeight() {
        return agentHeight;
    }

    @Override
    public boolean shouldHighlightEntities() {
        return false;
    }
}
