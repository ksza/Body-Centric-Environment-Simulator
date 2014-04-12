package dk.itu.bodysim;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import dk.itu.bodysim.agent.Agent;
import dk.itu.bodysim.environment.SimpleEnvironment;

/**
 *
 * @author kszanto
 */
public class WarmUpTask extends EgocentricApp {

    public static void main(String[] args) {
        WarmUpTask app = new WarmUpTask();
        app.start();
    }

    @Override
    protected Node createEnvironmentScene() {
        return new SimpleEnvironment(getAssetManager());
    }

    @Override
    protected Agent getAgentConfiguration() {
        return new Agent(new Vector3f(0, 10, 30), 15, 15);
    }
}