package dk.itu.bodysim.context.ssm;

import com.jme3.renderer.Camera;
import dk.itu.bodysim.context.EgocentricContextData;

/**
 *
 * @author kszanto
 */
public class PerceptionSpaceStrategy extends AbstractProximitySSMSpaceComputationStrategy {

    public PerceptionSpaceStrategy(Camera camera) {
        super(camera);
    }

    @Override
    protected float getReferenceDistance(final EgocentricContextData data) {
        return data.getPerceptionDistance();
    }
}
