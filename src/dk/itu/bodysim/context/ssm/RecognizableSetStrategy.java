package dk.itu.bodysim.context.ssm;

import com.jme3.renderer.Camera;
import dk.itu.bodysim.context.EgocentricContextData;

/**
 *
 * @author kszanto
 */
public class RecognizableSetStrategy extends AbstractProximitySSMSpaceComputationStrategy {

    public RecognizableSetStrategy(Camera camera) {
        super(camera);
    }

    @Override
    protected float getReferenceDistance(final EgocentricContextData data) {
        return data.getRecognitionDistance();
    }
}
