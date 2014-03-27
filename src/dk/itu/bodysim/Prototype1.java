package dk.itu.bodysim;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import dk.itu.bodysim.environment.SimpleEnvironment;

/**
 *
 * @author kszanto
 */
public class Prototype1 extends EgocentricApp {

    public static void main(String[] args) {
        Prototype1 app = new Prototype1();
        app.start();
    }

    @Override
    protected Node createEnvironmentScene() {
        return new SimpleEnvironment(getAssetManager());
    }      

    @Override
    public Vector3f getInitialAgentPosition() {
        return new Vector3f(0, 10, 30);
    }
}