package dk.itu.bodysim;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import dk.itu.bodysim.agent.Agent;
import dk.itu.bodysim.environment.ALFEnvironment;

/**
 *
 * @author kszanto
 */
public class Prototype2 extends EgocentricApp {

    public static void main(String[] args) {
        Prototype2 app = new Prototype2();
        app.start();
    }

    @Override
    protected Node createEnvironmentScene() {

        return new ALFEnvironment(getAssetManager());
    }

    @Override
    protected Agent getAgentConfiguration() {
        return new Agent(new Vector3f(-55, 26, 70), 25, 15);
    }
}
